package com.faustas.dbms.framework;

import com.faustas.dbms.Main;
import com.faustas.dbms.framework.annotations.Repository;
import com.faustas.dbms.framework.annotations.Service;
import com.faustas.dbms.framework.annotations.Value;
import com.faustas.dbms.framework.interfaces.Shutdownable;
import com.faustas.dbms.framework.repositories.RepositoryProxy;

import java.io.*;
import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;
import java.lang.reflect.Proxy;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class ApplicationContext {

    private Map<String, String> properties = new HashMap<>();

    private Set<Class> scannedClasses = new HashSet<>();

    private Set<Object> createdServices = new HashSet<>();

    public ApplicationContext(Class application) {
        this(application, "resources/application.properties");
    }

    public ApplicationContext(Class application, String pathToProperties) {
        try {
            loadProperties(pathToProperties);
            scanForServiceClasses(application);

            String databaseDriver = getProperty("database.driver", null);
            if (databaseDriver != null) {
                Class.forName(databaseDriver);
            }

            createdServices.add(this);

            Runtime.getRuntime().addShutdownHook(new Thread(this::shutdown));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public <T> T getBean(Class<T> classType) {
        return getBean(classType, new HashMap<>());
    }

    @SuppressWarnings("unchecked")
    public <T> T getBean(Class<T> classType, Map<String, Object> additionalProperties) {
        if (classType.isAnnotationPresent(Repository.class)) {
            return getRepository(classType);
        }

        Object service = createdServices.stream()
                .filter(classType::isInstance)
                .findFirst().orElse(null);
        if (service != null) {
            return (T) service;
        }

        Class requiredClass = scannedClasses.stream()
                .filter(classType::isAssignableFrom).findFirst()
                .orElseThrow(() -> new IllegalArgumentException("No implementation for this class found. Make sure you annotated injectable class with @Service"));

        Constructor<?>[] constructors = requiredClass.getConstructors();
        if (constructors.length != 1) {
            throw new RuntimeException("Make sure service has only one constructor");
        }

        Constructor<?> constructor = constructors[0];
        List<Object> constructorArguments = new ArrayList<>();
        for (Parameter parameter: constructor.getParameters()) {
            if (parameter.isAnnotationPresent(Value.class)) {
                constructorArguments.add(getInjectableProperty(parameter.getAnnotation(Value.class), additionalProperties));
            } else {
                constructorArguments.add(getBean(parameter.getType()));
            }
        }

        try {
            Object createdService = constructor.newInstance(constructorArguments.toArray());
            Service serviceMeta = createdService.getClass().getAnnotation(Service.class);

            if (serviceMeta.singleton()) {
                createdServices.add(createdService);
            }

            return (T) createdService;
        } catch (ReflectiveOperationException e) {
            e.printStackTrace();
            throw new RuntimeException("Unable to instantiate wanted service: " + classType.getName());
        }
    }

    public String getProperty(String name) {
        return properties.get(name);
    }

    public String getProperty(String name, String defaultValue) {
        String value = getProperty(name);
        return value == null ? defaultValue : value;
    }

    private Object getInjectableProperty(Value value, Map<String, Object> additionalProperties) {
        String property = value.value();
        String defaultValue = null;

        if (property.indexOf(':') >= 0) {
            String[] splits = property.split(":");
            if (splits.length != 2) {
                throw new RuntimeException("If you want to provide default value for property separate it by one ':'");
            }

            property = splits[0];
            defaultValue = splits[1];
        }

        if (additionalProperties.containsKey(property)) {
            return additionalProperties.get(property);
        }

        return getProperty(property, defaultValue);
    }

    private void scanForServiceClasses(Class application) throws IOException,
            ClassNotFoundException, URISyntaxException {
        for (Class aClass : scanForClasses(application)) {
            if (!aClass.isAnnotationPresent(Service.class)) {
                continue;
            }

            if (aClass.isAnnotation() || Modifier.isAbstract(aClass.getModifiers())
                    || Modifier.isInterface(aClass.getModifiers())) {
                throw new RuntimeException("Service can be only concrete class");
            }

            scannedClasses.add(aClass);
        }
    }

    private List<Class> scanForClasses(Class application) throws IOException,
            ClassNotFoundException, URISyntaxException {
        String protocol = getClass().getResource("").getProtocol();

        if(Objects.equals(protocol, "jar")){
            return scanForClassesInJar(application);
        }

        return scanForClassesInFs(application.getPackage());
    }

    private List<Class> scanForClassesInJar(Class application) throws IOException,
            ClassNotFoundException, URISyntaxException {
        File jar = new File(application.getProtectionDomain().getCodeSource().getLocation().toURI());

        List<Class> classes = new ArrayList<>();
        ZipInputStream zip = new ZipInputStream(new FileInputStream(jar));

        for (ZipEntry entry = zip.getNextEntry(); entry != null; entry = zip.getNextEntry()) {
            if (!entry.isDirectory() && entry.getName().endsWith(".class")) {
                String className = entry.getName().replace('/', '.'); // including ".class"
                className = className.substring(0, className.length() - ".class".length());
                classes.add(Class.forName(className));
            }
        }

        return classes;
    }

    private List<Class> scanForClassesInFs(Package basePackage) throws IOException, ClassNotFoundException {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();

        String path = basePackage.getName().replace('.', '/');
        Enumeration<URL> resources = classLoader.getResources(path);

        List<File> directories = new ArrayList<>();
        while (resources.hasMoreElements()) {
            URL resource = resources.nextElement();
            directories.add(new File(resource.getFile()));
        }

        List<Class> classes = new ArrayList<>();
        for (File directory : directories) {
            classes.addAll(findClassesInFs(directory, basePackage.getName()));
        }

        return classes;
    }

    private List<Class> findClassesInFs(File directory, String packageName) throws ClassNotFoundException {
        List<Class> classes = new ArrayList<>();
        if (!directory.exists()) {
            return classes;
        }

        for (File file : Objects.requireNonNull(directory.listFiles())) {
            if (file.isDirectory()) {
                classes.addAll(findClassesInFs(file, packageName + "." + file.getName()));
            } else if (file.getName().endsWith(".class")) {
                String className = packageName + '.' + file.getName().substring(0, file.getName().length() - 6);
                classes.add(Class.forName(className));
            }
        }

        return classes;
    }

    private void loadProperties(String pathToProperties) throws IOException {
        // try to find file in file system with provided path
        File file = new File(pathToProperties);
        if (file.exists()) {
            List<String> properties = Files.readAllLines(Paths.get(pathToProperties));
            parseProperties(properties);
            return;
        }

        // check if properties file exists in class path
        String classPathPropertiesFile = pathToProperties;
        if (!classPathPropertiesFile.startsWith("/")) {
            classPathPropertiesFile = "/" + classPathPropertiesFile;
        }

        if (getClass().getResource(classPathPropertiesFile) != null) {
            InputStream propertiesStream = getClass().getResourceAsStream(classPathPropertiesFile);
            List<String> properties = new BufferedReader(new InputStreamReader(propertiesStream, StandardCharsets.UTF_8))
                    .lines().collect(Collectors.toList());

            parseProperties(properties);
            return;
        }

        throw new IOException("Provided properties path does not exist");
    }

    private void parseProperties(List<String> properties) {
        for (String line : properties) {
            String[] keyValuePair = line.split(" *= *");
            if (keyValuePair.length != 2) {
                throw new RuntimeException("Properties file has bad format. Make sure its \"key=exec\" and one per line");
            }

            this.properties.put(keyValuePair[0], keyValuePair[1]);
        }
    }

    @SuppressWarnings("unchecked")
    private <T> T getRepository(Class<T> repositoryType) {
        return (T) Proxy.newProxyInstance(
                ApplicationContext.class.getClassLoader(),
                new Class[] { repositoryType },
                getBean(RepositoryProxy.class)
        );
    }

    private void shutdown() {
        for (Object service : createdServices) {
            if (service instanceof Shutdownable) {
                try {
                    ((Shutdownable) service).shutdown();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
}

package com.faustas.dbms.framework.repositories;

import com.faustas.dbms.framework.annotations.*;
import com.faustas.dbms.framework.connections.DatabaseConnectionPool;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.*;
import java.util.*;
import java.util.Date;
import java.util.stream.Collectors;

@Service
public class SelectQueryExecutor extends QueryExecutor {

    public SelectQueryExecutor(DatabaseConnectionPool connectionPool) {
        super(connectionPool);
    }

    @Override
    public Object execute(Method method, Object[] args) throws SQLException, ReflectiveOperationException {
        Select selectAnnotation = method.getAnnotation(Select.class);
        List<Param> params = Arrays.stream(method.getParameters()).filter(p -> p.isAnnotationPresent(Param.class))
                .map(p -> p.getAnnotation(Param.class)).collect(Collectors.toList());

        if (args == null) {
            args = new Object[0];
        }

        if (params.size() != args.length) {
            throw new RuntimeException("All arguments passed to repository method must have annotation @Param");
        }

        ResultSet resultSet = executeQuery(selectAnnotation.value(), getNamedArgs(params, args));

        Class<?> returnClass = method.getDeclaringClass().getAnnotation(Repository.class).value();
        List<Object> createdObjects = new ArrayList<>();

        Results resultsMeta = method.getAnnotation(Results.class);
        ResultSetMetaData setMeta = resultSet.getMetaData();

        int columnCount = setMeta.getColumnCount();
        while (resultSet.next()) {
            Object returnObject = returnClass.newInstance();

            for (int column = 1; column <= columnCount; ++column) {
                String columnName = setMeta.getColumnName(column);
                String objectProperty = columnName;

                Result singleResultMeta = findResultMeta(columnName, resultsMeta);
                if (singleResultMeta != null && !singleResultMeta.property().isEmpty()) {
                    objectProperty = singleResultMeta.property();
                }

                Method setter = findSetter(returnObject, objectProperty);
                if (setter != null) {
                    if (singleResultMeta != null && !singleResultMeta.exec().aClass().equals(void.class)) {
                        Exec exec = singleResultMeta.exec();
                        Method externalMethod = extractMethodFromExec(exec);
                        if (externalMethod.getParameterTypes().length != 1) {
                            throw new RuntimeException("Remote methods should have only one parameter");
                        }

                        Object arg = chooseCorrectExtraction(externalMethod.getParameterTypes()[0], columnName, resultSet);
                        setter.invoke(returnObject, execute(externalMethod, new Object[] { arg }));
                    } else {
                        assignValue(returnObject, resultSet, setter, columnName);
                    }
                }
            }

            createdObjects.add(returnObject);
        }

        if (Collection.class.isAssignableFrom(method.getReturnType())) {
            return createdObjects;
        }

        return createdObjects.size() == 0 ? null : createdObjects.get(0);
    }

    private Method extractMethodFromExec(Exec exec) {
        return Arrays.stream(exec.aClass().getMethods())
                .filter(m -> m.getName().equals(exec.method()))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Provided method does not exist"));
    }

    private ResultSet executeQuery(String query, Map<String, Object> namedArgs) throws SQLException {
        QueryProcessor queryProcessor = new QueryProcessor();
        ProcessedQuery processedQuery = queryProcessor.process(query, namedArgs);

        Connection connection = connectionPool.getConnection();
        PreparedStatement statement = connection.prepareStatement(processedQuery.getPreparedQuery());
        prepareStatement(statement, processedQuery.getPositionalParams());
        ResultSet resultSet = statement.executeQuery();
        connectionPool.releaseConnection(connection);

        return resultSet;
    }

    private void prepareStatement(PreparedStatement statement, Map<Integer, Object> positionalParams)
        throws SQLException {
        for (Integer paramPosition : positionalParams.keySet()) {
            Object param = positionalParams.get(paramPosition);

            if (param instanceof Integer) {
                statement.setInt(paramPosition, (Integer) param);
            } else if (param instanceof String) {
                statement.setString(paramPosition, (String) param);
            } else if (param instanceof Double) {
                statement.setDouble(paramPosition, (Double) param);
            } else if (param instanceof Date) {
                statement.setDate(paramPosition, new java.sql.Date(((Date) param).getTime()));
            }
        }
    }

    private void assignValue(Object object, ResultSet resultSet, Method setter, String columnName)
            throws SQLException, InvocationTargetException, IllegalAccessException {
        Class<?> parameterType = setter.getParameterTypes()[0];
        if (parameterType.isPrimitive()) {
            throw new RuntimeException("Please don't use primitive types");
        }

        setter.invoke(object, chooseCorrectExtraction(parameterType, columnName, resultSet));
    }
    
    private Object chooseCorrectExtraction(Class<?> parameterType, String columnName, ResultSet resultSet) throws SQLException {
        if (parameterType.isAssignableFrom(Integer.class)) {
            return resultSet.getInt(columnName);
        } else if (parameterType.isAssignableFrom(Double.class)) {
            return resultSet.getDouble(columnName);
        } else if (parameterType.isAssignableFrom(String.class)) {
            return resultSet.getString(columnName);
        } else if (parameterType.isAssignableFrom(java.sql.Date.class)) {
            return new Date(resultSet.getDate(columnName).getTime());
        }

        if (parameterType.isPrimitive()) {
            throw new RuntimeException("Do not use primitive types in models or repositories");
        }
        throw new RuntimeException("Type recognition not implemented: " + parameterType.getTypeName());
    }

    private Result findResultMeta(String columnName, Results results) {
        if (results == null) {
            return null;
        }

        return Arrays.stream(results.value()).filter(r -> r.column().equals(columnName))
                .findFirst().orElse(null);
    }

    private Map<String, Object> getNamedArgs(List<Param> params, Object[] args) {
        List<String> paramNames = params.stream().map(Param::value)
                .collect(Collectors.toList());

        HashMap<String, Object> namedArgs = new HashMap<>();
        for (int i = 0; i < paramNames.size(); ++i) {
            namedArgs.put(paramNames.get(i), args[i]);
        }

        return namedArgs;
    }
}
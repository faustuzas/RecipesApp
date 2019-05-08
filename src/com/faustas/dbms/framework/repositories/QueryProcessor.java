package com.faustas.dbms.framework.repositories;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.IntStream;

public class QueryProcessor {
    private static final String SINGLE_VALUE = "#";
    private static final String LIST_VALUE = "@";

    private static final String LIST_GLUE = ", ";

    private static final Pattern PARAM_PATTERN = Pattern.compile("([@#]\\w+[\\w.]+)");

    public ProcessedQuery process(String unprepared, Map<String, Object> namedArgs) {
        Map<Integer, Object> positionalParameters = new HashMap<>();

        // collect param position and assign exec
        Matcher paramMatcher = PARAM_PATTERN.matcher(unprepared);
        String preparedQuery = unprepared;

        int paramPosition = 1; // in sql exec position starts at 1
        while (paramMatcher.find()) {
            String group = paramMatcher.group();

            String paramType = group.substring(0, 1);
            String paramName = group.substring(1);

            if (paramType.equals(SINGLE_VALUE)) {
                positionalParameters.put(paramPosition++, extractValue(paramName, namedArgs));
                preparedQuery = PARAM_PATTERN.matcher(preparedQuery).replaceFirst("?");
            } else if (paramType.equals(LIST_VALUE)) {
                Object iterableObject = extractValue(paramName, namedArgs);
                if (! (iterableObject instanceof Collection || iterableObject.getClass().isArray())) {
                    throw new RuntimeException("When using \"@paramName\" make sure you pass array or instance of Collection");
                }

                Object[] iterable;
                if (iterableObject instanceof Collection) {
                    iterable = ((Collection) iterableObject).toArray();
                } else {
                    iterable = (Object[]) iterableObject;
                }

                for (Object it : iterable) {
                    positionalParameters.put(paramPosition++, it);
                }

                String questionMarks = join(IntStream.range(0, iterable.length).mapToObj(i -> "?").toArray());
                preparedQuery = PARAM_PATTERN.matcher(preparedQuery).replaceFirst(questionMarks);
            } else {
                throw new RuntimeException("Argument in SQL exec can start with # for single or @ for lists");
            }
        }

        return new ProcessedQuery(preparedQuery, positionalParameters);
    }

    private Object extractValue(String name, Map<String, Object> namedArgs) {
        if (!name.contains(".")) {
            return namedArgs.get(name);
        }

        String[] valuePath = name.split("\\.");
        Object target = namedArgs.get(valuePath[0]); // root object
        try {
            for (int i = 1; i < valuePath.length; ++i) {
                Method getter = target.getClass().getMethod(formatFieldToGetter(valuePath[i]));
                target = getter.invoke(target);
            }
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException("When providing nested objects make sure there are correct getters");
        }

        return target;
    }

    private String formatFieldToGetter(String field) {
        return "get" + field.substring(0, 1).toUpperCase() + field.substring(1);
    }

    private String join(Object[] objects) {
        if (objects.length == 0) {
            return "";
        }

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < objects.length - 1; ++i) {
            sb.append(objects[i]).append(LIST_GLUE);
        }
        sb.append(objects[objects.length - 1]); // after last one we don't need glue

        return sb.toString();
    }
}

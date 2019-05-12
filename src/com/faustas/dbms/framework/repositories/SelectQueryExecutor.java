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

        ResultSet resultSet = (ResultSet) executeQuery(selectAnnotation.value(), constructNamedArgs(method, args));

        Class<?> returnClass = method.getDeclaringClass().getAnnotation(Repository.class).value();
        List<Object> createdObjects = new ArrayList<>();

        Results resultsMeta = method.getAnnotation(Results.class);
        ResultSetMetaData setMeta = resultSet.getMetaData();

        int columnCount = setMeta.getColumnCount();
        while (resultSet.next()) {
            Object returnObject = returnClass.newInstance();

            // sql column indexing starts from 1
            for (int columnIndex = 1; columnIndex <= columnCount; ++columnIndex) {
                String columnName = setMeta.getColumnName(columnIndex);
                List<Result> columnMetas = findColumnMetas(columnName, resultsMeta);

                if (columnMetas.size() == 0) {
                    Method setter = findSetter(returnObject, columnName);
                    /*
                     * Object could not have setter for some relational field
                     * E.g. recipe table can have user_id, but recipe does not have property user
                     */
                    if (setter != null) {
                        assignValue(returnObject, resultSet, setter, columnName);
                        continue;
                    }
                }

                for (Result columnMeta : columnMetas) {
                    String objectProperty = columnName;
                    if (!columnMeta.property().isEmpty()) {
                        objectProperty = columnMeta.property();
                    }

                    Method setter = findSetter(returnObject, objectProperty);
                    if (setter != null) {
                        if (!columnMeta.exec().aClass().equals(void.class)) {
                            Exec exec = columnMeta.exec();
                            Method externalMethod = extractMethodFromExec(exec);
                            if (externalMethod.getParameterTypes().length != 1) {
                                throw new RuntimeException("Remote methods should have only one parameter");
                            }

                            Object arg = chooseCorrectExtraction(externalMethod.getParameterTypes()[0], columnName, resultSet);
                            /*
                             * Execute recursively this method if there is indicated method from another
                             * class to get all object relational tree
                             */
                            setter.invoke(returnObject, execute(externalMethod, new Object[] { arg }));
                        } else {
                            /*
                             * Just assign value from column with different name than property
                             */
                            assignValue(returnObject, resultSet, setter, columnName);
                        }
                    }
                }
            }

            createdObjects.add(returnObject);
        }

        /*
         * If invoked repository method returns some kind of collection
         * then return collection otherwise return single element
         */
        if (Collection.class.isAssignableFrom(method.getReturnType())) {
            return createdObjects;
        }

        return createdObjects.size() == 0 ? null : createdObjects.get(0);
    }

    @Override
    Object executeStatement(PreparedStatement statement) throws SQLException {
        return statement.executeQuery();
    }

    private Method extractMethodFromExec(Exec exec) {
        return Arrays.stream(exec.aClass().getMethods())
                .filter(m -> m.getName().equals(exec.method()))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Provided method does not exist"));
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

    private List<Result> findColumnMetas(String columnName, Results results) {
        if (results == null) {
            return Collections.emptyList();
        }

        return Arrays.stream(results.value()).filter(r -> r.column().equals(columnName))
            .collect(Collectors.toList());
    }
}
package com.faustas.dbms.framework.repositories;

import com.faustas.dbms.framework.annotations.Param;
import com.faustas.dbms.framework.connections.DatabaseConnectionPool;

import java.io.IOException;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;
import java.util.stream.Collectors;

public abstract class QueryExecutor {

    protected final DatabaseConnectionPool connectionPool;

    QueryExecutor(DatabaseConnectionPool connectionPool) {
        this.connectionPool = connectionPool;
    }

    /**
     * Main method for external call
     */
    public abstract Object execute(Method method, Object[] args) throws IOException, SQLException, ReflectiveOperationException;

    /**
     * Choose correct way to execute statement
     */
    abstract Object executeStatement(PreparedStatement statement) throws SQLException;

    /**
     * Parse query, put arguments in it and execute it
     */
    QueryResult executeQuery(String query, Map<String, Object> namedArgs) throws SQLException {
        QueryProcessor queryProcessor = new QueryProcessor();
        ProcessedQuery processedQuery = queryProcessor.process(query, namedArgs);

        Connection connection = connectionPool.getConnection();
        PreparedStatement statement = connection.prepareStatement(processedQuery.getPreparedQuery(), Statement.RETURN_GENERATED_KEYS);
        prepareStatement(statement, processedQuery.getPositionalParams());
        Object result = executeStatement(statement);
        connectionPool.releaseConnection(connection);

        return new QueryResult(statement, result);
    }

    Map<String, Object> constructNamedArgs(Method method, Object[] args) {
        List<Param> params = extractParams(method);

        if (args == null) {
            args = new Object[0];
        }

        if (params.size() != args.length) {
            throw new RuntimeException("All arguments passed to repository method must have annotation @Param");
        }

        return getNamedArgs(params, args);
    }

    /**
     * Iterate through all methods in object
     * and search for required setter.
     *
     * Search is done by taking each method and
     * cutting its first three letters ("set" to be exact)
     * and comparing if the leftover from the setter name,
     * which is some property name,
     * matches the one which is requested
     */
    Method findSetter(Object object, String propertyToSet) {
        return Arrays.stream(object.getClass().getMethods())
                .filter(m -> m.getName().startsWith("set"))
                .filter(m -> {
                    String property = m.getName().substring(3);
                    property = property.substring(0, 1).toLowerCase() + property.substring(1);
                    return property.equals(propertyToSet);
                })
                .findFirst().orElse(null);
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

    private List<Param> extractParams(Method method) {
        return Arrays.stream(method.getParameters()).filter(p -> p.isAnnotationPresent(Param.class))
                .map(p -> p.getAnnotation(Param.class)).collect(Collectors.toList());
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

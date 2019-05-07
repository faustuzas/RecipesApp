package com.faustas.dbms.framework.repositories;

import com.faustas.dbms.framework.connections.DatabaseConnectionPool;

import java.io.IOException;
import java.lang.reflect.Method;
import java.sql.SQLException;
import java.util.Arrays;

public abstract class QueryExecutor {

    protected final DatabaseConnectionPool connectionPool;

    public QueryExecutor(DatabaseConnectionPool connectionPool) {
        this.connectionPool = connectionPool;
    }

    abstract Object execute(Method method, Object[] args) throws IOException, SQLException, ReflectiveOperationException;

    protected Method findSetter(Object object, String property) {
        return Arrays.stream(object.getClass().getMethods())
            .filter(m -> m.getName().startsWith("set"))
            .filter(m -> m.getName().substring(3).toLowerCase().equals(property))
            .findFirst().orElse(null);
    }
}

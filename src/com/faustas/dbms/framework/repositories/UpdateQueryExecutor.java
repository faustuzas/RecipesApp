package com.faustas.dbms.framework.repositories;

import com.faustas.dbms.framework.annotations.Service;
import com.faustas.dbms.framework.annotations.Update;
import com.faustas.dbms.framework.connections.DatabaseConnectionPool;

import java.lang.reflect.Method;
import java.sql.PreparedStatement;
import java.sql.SQLException;

@Service
public class UpdateQueryExecutor extends QueryExecutor {

    public UpdateQueryExecutor(DatabaseConnectionPool connectionPool) {
        super(connectionPool);
    }

    @Override
    public Object execute(Method method, Object[] args) throws SQLException {
        Update updateAnnotation = method.getAnnotation(Update.class);

        return executeQuery(updateAnnotation.value(), constructNamedArgs(method, args));
    }

    @Override
    Object executeStatement(PreparedStatement statement) throws SQLException {
        return statement.executeUpdate();
    }
}

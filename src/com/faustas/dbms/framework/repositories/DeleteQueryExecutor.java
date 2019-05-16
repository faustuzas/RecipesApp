package com.faustas.dbms.framework.repositories;

import com.faustas.dbms.framework.annotations.Delete;
import com.faustas.dbms.framework.annotations.Service;
import com.faustas.dbms.framework.connections.DatabaseConnectionPool;

import java.lang.reflect.Method;
import java.sql.PreparedStatement;
import java.sql.SQLException;

@Service
public class DeleteQueryExecutor extends QueryExecutor {

    public DeleteQueryExecutor(DatabaseConnectionPool connectionPool) {
        super(connectionPool);
    }

    @Override
    public Object execute(Method method, Object[] args) throws SQLException {
        Delete deleteAnnotation = method.getAnnotation(Delete.class);

        return executeQuery(deleteAnnotation.value(), constructNamedArgs(method, args));
    }

    @Override
    Object executeStatement(PreparedStatement statement) throws SQLException {
        return statement.execute();
    }
}

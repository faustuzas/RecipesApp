package com.faustas.dbms.framework.repositories;

import com.faustas.dbms.framework.annotations.Delete;
import com.faustas.dbms.framework.annotations.Service;

import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

@Service
public class DeleteQueryExecutor extends QueryExecutor {

    @Override
    public Object execute(Connection connection, Method method, Object[] args) throws SQLException {
        Delete deleteAnnotation = method.getAnnotation(Delete.class);

        return executeQuery(connection, deleteAnnotation.value(), constructNamedArgs(method, args));
    }

    @Override
    Object executeStatement(PreparedStatement statement) throws SQLException {
        return statement.execute();
    }
}

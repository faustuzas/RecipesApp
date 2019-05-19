package com.faustas.dbms.framework.repositories;

import com.faustas.dbms.framework.annotations.Service;
import com.faustas.dbms.framework.annotations.Update;

import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

@Service
public class UpdateQueryExecutor extends QueryExecutor {

    @Override
    public Object execute(Connection connection, Method method, Object[] args) throws SQLException {
        Update updateAnnotation = method.getAnnotation(Update.class);

        return executeQuery(connection, updateAnnotation.value(), constructNamedArgs(method, args));
    }

    @Override
    Object executeStatement(PreparedStatement statement) throws SQLException {
        return statement.executeUpdate();
    }
}

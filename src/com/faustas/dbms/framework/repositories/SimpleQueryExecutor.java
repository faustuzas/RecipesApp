package com.faustas.dbms.framework.repositories;

import com.faustas.dbms.framework.annotations.Service;
import com.faustas.dbms.framework.annotations.Sql;

import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * This query executor is used with annotation @Sql to execute plain queries,
 * like create view, refresh it and etc.
 */
@Service
public class SimpleQueryExecutor extends QueryExecutor {

    @Override
    public Object execute(Connection connection, Method method, Object[] args) throws SQLException {
        Sql sqlAnnotation = method.getAnnotation(Sql.class);

        return executeQuery(connection, sqlAnnotation.value(), constructNamedArgs(method, args));
    }

    @Override
    Object executeStatement(PreparedStatement statement) throws SQLException {
        return statement.execute();
    }
}

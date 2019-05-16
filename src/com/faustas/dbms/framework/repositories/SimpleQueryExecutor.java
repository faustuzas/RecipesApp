package com.faustas.dbms.framework.repositories;

import com.faustas.dbms.framework.annotations.Service;
import com.faustas.dbms.framework.annotations.Sql;
import com.faustas.dbms.framework.connections.DatabaseConnectionPool;

import java.lang.reflect.Method;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * This query executor is used with annotation @Sql to execute plain queries,
 * like create view, refresh it and etc.
 */
@Service
public class SimpleQueryExecutor extends QueryExecutor {

    public SimpleQueryExecutor(DatabaseConnectionPool connectionPool) {
        super(connectionPool);
    }

    @Override
    public Object execute(Method method, Object[] args) throws SQLException {
        Sql sqlAnnotation = method.getAnnotation(Sql.class);

        return executeQuery(sqlAnnotation.value(), constructNamedArgs(method, args));
    }

    @Override
    Object executeStatement(PreparedStatement statement) throws SQLException {
        return statement.execute();
    }
}

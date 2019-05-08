package com.faustas.dbms.framework.repositories;

import com.faustas.dbms.framework.annotations.Insert;
import com.faustas.dbms.framework.annotations.Service;
import com.faustas.dbms.framework.connections.DatabaseConnectionPool;

import java.lang.reflect.Method;
import java.sql.PreparedStatement;
import java.sql.SQLException;

@Service
public class InsertQueryExecutor extends QueryExecutor {

    public InsertQueryExecutor(DatabaseConnectionPool connectionPool) {
        super(connectionPool);
    }

    @Override
    Object execute(Method method, Object[] args) throws SQLException {
        Insert insertAnnotation = method.getAnnotation(Insert.class);

        return executeQuery(insertAnnotation.value(), constructNamedArgs(method, args));
    }

    @Override
    Object executeStatement(PreparedStatement statement) throws SQLException {
        return statement.execute();
    }
}

package com.faustas.dbms.framework.repositories;

import com.faustas.dbms.framework.annotations.Insert;
import com.faustas.dbms.framework.annotations.Service;

import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@Service
public class InsertQueryExecutor extends QueryExecutor {

    @Override
    public Object execute(Connection connection, Method method, Object[] args) throws SQLException {
        Insert insertAnnotation = method.getAnnotation(Insert.class);

        QueryResult queryResult = executeQuery(connection, insertAnnotation.value(), constructNamedArgs(method, args));
        ResultSet generatedKeys = queryResult.getStatement().getGeneratedKeys();
        if (generatedKeys.next()) {
            return generatedKeys.getInt("id");
        }

        return null;
    }

    @Override
    Object executeStatement(PreparedStatement statement) throws SQLException {
        return statement.execute();
    }
}

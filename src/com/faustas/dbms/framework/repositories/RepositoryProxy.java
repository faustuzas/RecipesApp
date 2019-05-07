package com.faustas.dbms.framework.repositories;

import com.faustas.dbms.framework.annotations.Select;
import com.faustas.dbms.framework.annotations.Service;
import com.faustas.dbms.framework.connections.ConnectionPool;

import java.io.IOException;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.sql.SQLException;

@Service
public class RepositoryProxy implements InvocationHandler {

    private QueryExecutor selectExecutor;

    public RepositoryProxy(ConnectionPool connectionPool) {
        this.selectExecutor = new SelectQueryExecutor(connectionPool);
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) {
        try {
            if (method.isAnnotationPresent(Select.class)) {
                return selectExecutor.execute(method, args);
            }
        } catch (IOException | SQLException | ReflectiveOperationException e) {
            e.printStackTrace();
        }

        throw new RuntimeException("Make sure method called have needed annotations");
    }
}

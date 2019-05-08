package com.faustas.dbms.framework.repositories;

import com.faustas.dbms.framework.annotations.Select;
import com.faustas.dbms.framework.annotations.Service;
import com.faustas.dbms.framework.annotations.Update;

import java.io.IOException;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.sql.SQLException;

@Service
public class RepositoryProxy implements InvocationHandler {

    private QueryExecutor selectExecutor;

    private UpdateQueryExecutor updateExecutor;

    public RepositoryProxy(SelectQueryExecutor selectExecutor, UpdateQueryExecutor updateExecutor) {
        this.selectExecutor = selectExecutor;
        this.updateExecutor = updateExecutor;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) {
        try {
            if (method.isAnnotationPresent(Select.class)) {
                return selectExecutor.execute(method, args);
            }

            if (method.isAnnotationPresent(Update.class)) {
                return updateExecutor.execute(method, args);
            }
        } catch (IOException | SQLException | ReflectiveOperationException e) {
            e.printStackTrace();
        }

        throw new RuntimeException("Make sure method called have needed annotations");
    }
}

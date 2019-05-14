package com.faustas.dbms.framework.repositories;

import com.faustas.dbms.framework.annotations.*;

import java.io.IOException;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.sql.SQLException;

@Service
public class RepositoryProxy implements InvocationHandler {

    private QueryExecutor selectExecutor;
    private UpdateQueryExecutor updateExecutor;
    private InsertQueryExecutor insertExecutor;
    private DeleteQueryExecutor deleteExecutor;

    public RepositoryProxy(SelectQueryExecutor selectExecutor, UpdateQueryExecutor updateExecutor,
                                 InsertQueryExecutor insertExecutor, DeleteQueryExecutor deleteExecutor) {
        this.selectExecutor = selectExecutor;
        this.updateExecutor = updateExecutor;
        this.insertExecutor = insertExecutor;
        this.deleteExecutor = deleteExecutor;
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

            if (method.isAnnotationPresent(Insert.class)) {
                return insertExecutor.execute(method, args);
            }

            if (method.isAnnotationPresent(Delete.class)) {
                return deleteExecutor.execute(method, args);
            }
        } catch (IOException | SQLException | ReflectiveOperationException e) {
            throw new RuntimeException(e.getMessage());
        }

        throw new RuntimeException("Make sure method called have needed annotations");
    }
}

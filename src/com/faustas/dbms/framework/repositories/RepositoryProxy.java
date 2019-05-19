package com.faustas.dbms.framework.repositories;

import com.faustas.dbms.framework.annotations.*;
import com.faustas.dbms.framework.connections.DatabaseConnectionPool;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.SQLException;

@Service
public class RepositoryProxy implements InvocationHandler {
    
    private DatabaseConnectionPool connectionPool;

    private SelectQueryExecutor selectExecutor;
    private UpdateQueryExecutor updateExecutor;
    private InsertQueryExecutor insertExecutor;
    private DeleteQueryExecutor deleteExecutor;
    private SimpleQueryExecutor simpleExecutor;
    
    private ThreadLocal<Connection> connectionHolder = new ThreadLocal<>();

    public RepositoryProxy(DatabaseConnectionPool connectionPool, SelectQueryExecutor selectExecutor, 
                           UpdateQueryExecutor updateExecutor, InsertQueryExecutor insertExecutor, 
                           DeleteQueryExecutor deleteExecutor, SimpleQueryExecutor simpleExecutor) {
        this.connectionPool = connectionPool;
        this.selectExecutor = selectExecutor;
        this.updateExecutor = updateExecutor;
        this.insertExecutor = insertExecutor;
        this.deleteExecutor = deleteExecutor;
        this.simpleExecutor = simpleExecutor;
    }

    /**
     * Transaction management:
     * 1. Simple execution - take connection, set autocommit true, execute query, free connection
     * 2. Start transaction - check if connection exists in thread local, take connection, set autocommit false, put into thread local
     * 3. Commit - check if connection exists, commit, free connection
     * 4. Rollback - check if connection exists, rollback, free connection
     */
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) {
        Connection connection;
        
        if (checkForTransactionalThings(method)) {
            return true;
        } else {
            try {
                connection = connectionHolder.get();

                if (connection == null) {
                    connection = connectionPool.getConnection();
                    connection.setAutoCommit(true);
                }
            } catch (SQLException e) {
                throw new RuntimeException("Could not acquire Database connection");
            }
        }

        Object nothingExecutedFlag = new Object();

        Object result = nothingExecutedFlag;
        try {
            if (method.isAnnotationPresent(Select.class)) {
                result = selectExecutor.execute(connection, method, args);
            }

            if (method.isAnnotationPresent(Update.class)) {
                result = updateExecutor.execute(connection, method, args);
            }

            if (method.isAnnotationPresent(Insert.class)) {
                result = insertExecutor.execute(connection, method, args);
            }

            if (method.isAnnotationPresent(Delete.class)) {
                result = deleteExecutor.execute(connection, method, args);
            }

            if (method.isAnnotationPresent(Sql.class)) {
                result = simpleExecutor.execute(connection, method, args);
            }

            if (connectionHolder.get() == null) {
                connectionPool.releaseConnection(connection);
            }

            if (nothingExecutedFlag == result) {
                throw new RuntimeException("Make sure method called have needed annotations");
            }

            return result;
        } catch (SQLException | ReflectiveOperationException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    /**
     * @param method which was invoked by the repository
     * @return true if transactional method was executed and method is fulfilled
     */
    boolean checkForTransactionalThings(Method method) {
        Connection connection;
        if (method.getName().equals("startTransaction")) {
            if (connectionHolder.get() != null) {
                throw new RuntimeException("Transaction already started");
            }

            try {
                connection = connectionPool.getConnection();
                connection.setAutoCommit(false);
            } catch (SQLException e) {
                throw new RuntimeException("Could not acquire Database connection");
            }

            connectionHolder.set(connection);

            return true;
        }

        if (method.getName().equals("commit")) {
            connection = connectionHolder.get();
            if (connection == null) {
                throw new RuntimeException("Transaction have not been started");
            }

            try {
                connection.commit();
                connectionPool.releaseConnection(connection);
                connectionHolder.set(null);
            } catch (SQLException e) {
                throw new RuntimeException("Error while committing transaction");
            }

            return true;
        }

        if (method.getName().equals("rollback")) {
            connection = connectionHolder.get();
            if (connection == null) {
                throw new RuntimeException("Transaction have not been started");
            }

            try {
                connection.rollback();
                connectionPool.releaseConnection(connection);
                connectionHolder.set(null);
            } catch (SQLException e) {
                throw new RuntimeException("Error while executing rollback on the transaction");
            }

            return true;
        }

        return false;
    }
}

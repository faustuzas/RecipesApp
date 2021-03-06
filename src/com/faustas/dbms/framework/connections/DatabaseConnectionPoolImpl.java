package com.faustas.dbms.framework.connections;

import com.faustas.dbms.framework.annotations.Service;
import com.faustas.dbms.framework.annotations.Value;
import com.faustas.dbms.framework.interfaces.Shutdownable;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Service
public class DatabaseConnectionPoolImpl implements DatabaseConnectionPool, Shutdownable {
    private static final int INITIAL_POOL_SIZE = 5;
    private static final int MAX_POOL_SIZE = 10;

    private final String url;

    private final String user;

    private final String password;

    private final List<Connection> connectionPool = new ArrayList<>();

    private final List<Connection> usedConnections = new ArrayList<>(INITIAL_POOL_SIZE);

    public DatabaseConnectionPoolImpl(@Value("database.url") String url, @Value("database.user") String user,
                                      @Value("database.password") String password) throws SQLException {
        this.url = url;
        this.user = user;
        this.password = password;

        for (int i = 0; i < INITIAL_POOL_SIZE; ++i) {
            connectionPool.add(createConnection());
        }
    }

    private Connection createConnection() throws SQLException {
        return DriverManager.getConnection(url, user, password);
    }

    @Override
    public Connection getConnection() throws SQLException {
        if (connectionPool.isEmpty()) {
            if (usedConnections.size() < MAX_POOL_SIZE) {
                connectionPool.add(createConnection());
            } else {
                throw new RuntimeException(
                        "Maximum pool size reached, no available connections!");
            }
        }

        Connection connection = connectionPool
                .remove(connectionPool.size() - 1);
        usedConnections.add(connection);
        return connection;
    }

    @Override
    public boolean releaseConnection(Connection connection) {
        connectionPool.add(connection);
        return usedConnections.remove(connection);
    }

    @Override
    public void shutdown() throws SQLException {
        for (int i = 0; i < usedConnections.size(); ++i) {
            releaseConnection(usedConnections.get(0));
        }
        for (Connection c : connectionPool) {
            c.close();
        }
        connectionPool.clear();
    }
}

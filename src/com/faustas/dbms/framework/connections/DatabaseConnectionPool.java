package com.faustas.dbms.framework.connections;

import java.sql.Connection;
import java.sql.SQLException;

public interface DatabaseConnectionPool {

    Connection getConnection() throws SQLException;

    boolean releaseConnection(Connection connection);

    void shutdown() throws SQLException;
}

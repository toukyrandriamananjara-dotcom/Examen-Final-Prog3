package com.collectivity.datasource;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class JdbcDataSource {
    private static final String URL = "jdbc:postgresql://localhost:5432/agricultural_federation";
    private static final String USER = "postgres";
    private static final String PASSWORD = "1234";

    public Connection getConnection() throws SQLException {
        try {
            Class.forName("org.postgresql.Driver");
            return DriverManager.getConnection(URL, USER, PASSWORD);
        } catch (ClassNotFoundException e) {
            throw new SQLException("PostgreSQL driver not found", e);
        }
    }
}
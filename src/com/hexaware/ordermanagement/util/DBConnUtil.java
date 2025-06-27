package com.hexaware.ordermanagement.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnUtil {

    private static final String DB_PROPERTIES_FILE = "db.properties";

    public static Connection getDBConn() {
        Connection conn = null;
        try {
            String connStr = DBPropertyUtil.getConnectionString(DB_PROPERTIES_FILE);
            conn = DriverManager.getConnection(connStr);
        } catch (SQLException e) {
            System.out.println("Error connecting to DB: " + e.getMessage());
        }
        return conn;
    }
}

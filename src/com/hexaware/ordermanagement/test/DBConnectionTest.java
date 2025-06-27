package com.hexaware.ordermanagement.test;

import java.sql.Connection;
import com.hexaware.ordermanagement.util.DBConnUtil;

public class DBConnectionTest {
    public static void main(String[] args) {
        Connection conn = DBConnUtil.getDBConn();
        if (conn != null) {
            System.out.println("Database connection successful.");
        } else {
            System.out.println("Failed to connect to the database.");
        }
    }
}


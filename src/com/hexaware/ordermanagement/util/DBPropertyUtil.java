package com.hexaware.ordermanagement.util;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class DBPropertyUtil {

    public static String getConnectionString(String fileName) {
        Properties props = new Properties();
        String connStr = null;

        try (FileInputStream fis = new FileInputStream(fileName)) {
            props.load(fis);
            String url = props.getProperty("db.url");
            String username = props.getProperty("db.username");
            String password = props.getProperty("db.password");

            connStr = url + "?user=" + username + "&password=" + password;
        } catch (IOException e) {
            System.out.println("Error reading db.properties: " + e.getMessage());
        }

        return connStr;
    }
}

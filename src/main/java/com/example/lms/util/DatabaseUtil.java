package com.example.lms.util;
import java.io.InputStream;
import java.sql.*;
import java.util.Properties;

public class DatabaseUtil {
    private static final Properties props = new Properties();

    static {
        try (InputStream is = DatabaseUtil.class.getClassLoader().getResourceAsStream("db.properties")) {
            props.load(is);
        } catch (Exception e) { e.printStackTrace(); }
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(props.getProperty("db.url"), 
                                         props.getProperty("db.user"), 
                                         props.getProperty("db.password"));
    }
}
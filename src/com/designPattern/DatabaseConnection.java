package com.designPattern;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
	
    private static final String JDBC_URL = "jdbc:mysql://localhost:3306/mscitm";
    private static final String USERNAME = "root";
    private static final String PASSWORD = "";
    private static DatabaseConnection instance;
    
    private DatabaseConnection() {
    	
    }
	
    public static DatabaseConnection getInstance() {
    	if(instance == null) {
    		instance = new DatabaseConnection();
    	}
    	return instance;
    }
    
    public Connection getConnection() throws SQLException{
    	return DriverManager.getConnection(JDBC_URL,USERNAME,PASSWORD);
    }
}

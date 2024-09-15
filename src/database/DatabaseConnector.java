package database;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author My PC
 */
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import javax.swing.JOptionPane;

public class DatabaseConnector {

    // Database URL and credentials
    private static final String DB_URL = "jdbc:mysql://localhost:3306/spice_business";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "";

    public Connection connectToDatabase() {
        Connection conn = null;
        try {
            // Load the JDBC driver
            Class.forName("com.mysql.cj.jdbc.Driver");
            // Attempt to connect to the database
            conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
            JOptionPane.showMessageDialog(null, "Database connection successful!", "Info", JOptionPane.INFORMATION_MESSAGE);
        } catch (ClassNotFoundException e) {
            JOptionPane.showMessageDialog(null, "JDBC Driver not found: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();  // Print stack trace for debugging
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Database connection failed: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();  // Print stack trace for debugging
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "An unexpected error occurred: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();  // Print stack trace for debugging
        }
        return conn;
    }

    public static void main(String[] args) {
        DatabaseConnector connector = new DatabaseConnector();
        connector.connectToDatabase();
    }
}

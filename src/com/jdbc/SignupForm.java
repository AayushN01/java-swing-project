package com.jdbc;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class SignupForm extends JFrame implements ActionListener {
    private JTextField idField;
    private JTextField nameField;
    private JTextField emailField;
    private JTextField passwordField;
    private JButton signupButton;

    // Database connection parameters
    private static final String JDBC_URL = "jdbc:mysql://localhost:3306/mscitm";
    private static final String USERNAME = "root";
    private static final String PASSWORD = "";

    public SignupForm() {
        setTitle("Signup Form");
        setSize(400, 150);
        setLayout(null);
        setVisible(true);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        
        // Create components
        idField = new JTextField(20);
        nameField = new JTextField(20);
        emailField = new JTextField(20);
        passwordField = new JTextField(20);
        signupButton = new JButton("Signup");

        // Set layout
        setLayout(new GridLayout(3, 1, 10, 5)); //rows,columns, horizontal gap, vertical gap

        // Add components to the frame
        add(new JLabel("ID:"));
        add(idField);
        add(new JLabel("Name:"));
        add(nameField);
        add(signupButton);

        // Add action listener to the signup button
        signupButton.addActionListener(this);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String id = idField.getText();
        String name = nameField.getText();

        // Insert user record into the database
        try (Connection connection = DriverManager.getConnection(JDBC_URL, USERNAME, PASSWORD)) {
            String sql = "INSERT INTO mscitm_table (id, name) VALUES (?, ?)";
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setString(1, id);
                statement.setString(2, name);
                statement.executeUpdate();
                JOptionPane.showMessageDialog(this, "User signed up successfully!");
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            SignupForm signupForm = new SignupForm();
            signupForm.setVisible(true);
        });
    }
}
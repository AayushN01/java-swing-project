package com.jdbc;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class SignupForm extends JFrame implements ActionListener {
//    private JTextField idField;
    private JTextField nameField;
    private JTextField emailField;
    private JTextField usernameField;
    private JTextField passwordField;
    private JButton signupButton;

    // Database connection parameters
    private static final String JDBC_URL = "jdbc:mysql://localhost:3306/mscitm";
    private static final String USERNAME = "root";
    private static final String PASSWORD = "";

    public SignupForm() {
        setTitle("Signup Form");
        setSize(500, 200);
        setLayout(null);
        setVisible(true);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        
        // Create components
//        idField = new JTextField(20);
        nameField = new JTextField(20);
        emailField = new JTextField(50);
        usernameField = new JTextField(50);
        passwordField = new JPasswordField(50);
        signupButton = new JButton("Signup");

        // Set layout
        setLayout(new GridLayout(6, 1, 10, 5)); //rows,columns, horizontal gap, vertical gap

        // Add components to the frame
//        add(new JLabel("ID:"));
//        add(idField);
        add(new JLabel("Name:"));
        add(nameField);
        add(new JLabel("Email:"));
        add(emailField);
        add(new JLabel("Username"));
        add(usernameField);
        add(new JLabel("Password"));
        add(passwordField);
        add(signupButton);

        // Add action listener to the signup button
        signupButton.addActionListener(this);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
//        String id = idField.getText();
        String name = nameField.getText();
        String email = emailField.getText();
        String username = usernameField.getText();
        String password = passwordField.getText();

        // Insert user record into the database
        try (Connection connection = DriverManager.getConnection(JDBC_URL, USERNAME, PASSWORD)) {
            String sql = "INSERT INTO users (name,email,username,password) VALUES (?, ?,?,?)";
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
//                statement.setString(1, id);
                statement.setString(1, name);
                statement.setString(2, email);
                statement.setString(3, username);
                statement.setString(4, password);
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
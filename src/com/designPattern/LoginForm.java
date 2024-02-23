package com.designPattern;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.swing.*;

public class LoginForm extends JFrame implements ActionListener{
	
    private JTextField emailField;
    private JPasswordField passwordField;
    private JButton loginButton;
    
    private DatabaseConnection database;
    
    public LoginForm(DatabaseConnection database) {
        this.database = database;
        setTitle("Admin Login");
        setSize(500, 150);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        emailField = new JTextField(50);
        passwordField = new JPasswordField(50);
        loginButton = new JButton("Login");

        setLayout(new GridLayout(3, 1, 10, 5));

        add(new JLabel("Email:"));
        add(emailField);
        add(new JLabel("Password"));
        add(passwordField);
        add(loginButton);

        loginButton.addActionListener(this);
    }
    
    @Override
    public void actionPerformed(ActionEvent e) {
        String email = emailField.getText();
        char[] passwordChars = passwordField.getPassword();
        String password = new String(passwordChars);

        try (Connection connection = database.getConnection()) {
            String sql = "SELECT email, password FROM users WHERE email = ? AND password = ? ";

            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setString(1, email);
                statement.setString(2, password);
                try (ResultSet resultSet = statement.executeQuery()) {
                    if (resultSet.next()) {
                        JOptionPane.showMessageDialog(this, "Admin Login Successful");
                        openAdminDashboard();
                    } else {
                        JOptionPane.showMessageDialog(this, "Invalid email or password. Please try again");
                    }
                }
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
        }
    }
    
    private void openAdminDashboard() {
 	   JFrame dashboard = new JFrame("Dashboard");
 	   dashboard.setSize(900,900);
 	   dashboard.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
 	   dashboard.setLayout(new GridLayout(4,1,10,5));
 	   
 	   JButton booksButton = new JButton("Book List");
 	   JButton studentButton = new JButton("Student List"); 
 	   JButton bookLogButton = new JButton("Book Log");
 	   JButton logoutButton = new JButton("Logout");
 	   
        booksButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
               dashboard.dispose();
               openBooksWindow();
            }
        });
        
        studentButton.addActionListener(new ActionListener() {
     	   @Override
     	   public void actionPerformed(ActionEvent e) {
     		   dashboard.dispose();
     		   openStudentsWindow();
     	   }
        });
        
        bookLogButton.addActionListener(new ActionListener() {
     	   @Override
     	   public void actionPerformed(ActionEvent e) {
     		   dashboard.dispose();
     		   openBookLogWindow();
     	   }
        });
        
        logoutButton.addActionListener(new ActionListener() {
     	   @Override
     	   public void actionPerformed(ActionEvent e)
     	   {
     		   dashboard.dispose();
     		   LoginForm.this.setVisible(true);
     	   }
        });
        

        dashboard.add(booksButton);
        dashboard.add(studentButton);
        dashboard.add(bookLogButton);
        dashboard.add(logoutButton);
        
        dashboard.setVisible(true);
        this.setVisible(false);
 	   
 	   
    }
    
    private void openBooksWindow() {
        BookWindow bookWindow = new BookWindow(database);
        bookWindow.setVisible(true);
        this.setVisible(false);
    }
    
    private void openStudentsWindow() {
        StudentWindow studentWindow = new StudentWindow(database);
        studentWindow.setVisible(true);
        this.setVisible(false);
    }
    
    private void openBookLogWindow() {
        BookLog bookLog = new BookLog(database);
        bookLog.setVisible(true);
        this.setVisible(false);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            DatabaseConnection database = DatabaseConnection.getInstance();
            LoginForm loginForm = new LoginForm(database);
            loginForm.setVisible(true);
            loginForm.setLayout(null);
        });
    }

}

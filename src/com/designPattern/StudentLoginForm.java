package com.designPattern;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.swing.*;

public class StudentLoginForm extends JFrame implements ActionListener{
	
	private JTextField idField,emailField;
	private JPasswordField passwordField;
	private JButton loginButton;
	
    private DatabaseConnection database;
    
    public StudentLoginForm(DatabaseConnection database) {
    	this.database = database;
        setTitle("Student Login Form");
        setSize(500, 150);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        idField = new JTextField();
        emailField = new JTextField(50);
        passwordField = new JPasswordField(50);
        loginButton = new JButton("Login");

        setLayout(new GridLayout(4, 2, 10, 10));

        JLabel idLabel = new JLabel("Id:");
        idLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        add(idLabel);
        add(idField);

        JLabel emailLabel = new JLabel("Email:");
        emailLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        add(emailLabel);
        add(emailField);

        JLabel passwordLabel = new JLabel("Password");
        passwordLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        add(passwordLabel);
        add(passwordField);

        add(loginButton);

        loginButton.addActionListener(this);
    }
    @Override
    public void actionPerformed(ActionEvent e) {
    	
      String id = idField.getText();
      String email = emailField.getText();
      char[] passwordChars = passwordField.getPassword();
      String password = new String(passwordChars);

      // Insert user record into the database
      try (Connection connection = database.getConnection()) {
          String sql = "SELECT email, password FROM students WHERE email = ? AND password = ? " ;
         
          try (PreparedStatement statement = connection.prepareStatement(sql)) {
              statement.setString(1, email);
              statement.setString(2, password);
              try(ResultSet resultSet = statement.executeQuery()){
            	  if(resultSet.next()) {
            		  JOptionPane.showMessageDialog(this, "Login Successful");
            		  openStudentBookWindow();
            	  }else {
            		  JOptionPane.showMessageDialog(this, "Invalid email or password. Please try again");
            	  }
              }
          }
      } catch (SQLException ex) {
          JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
      }
  }
    
    private void openStudentBookWindow()
    {
  	   StudentBookWindow studentBookWindow = new StudentBookWindow(database);
  	   studentBookWindow.setVisible(true);
  	   this.setVisible(false);
    }

	public static void main(String[] args) {
		// TODO Auto-generated method stub
        SwingUtilities.invokeLater(() -> {
            DatabaseConnection database = DatabaseConnection.getInstance();
            StudentLoginForm sloginForm = new StudentLoginForm(database);
            sloginForm.setVisible(true);
            sloginForm.setLayout(null);
        });
	}

}

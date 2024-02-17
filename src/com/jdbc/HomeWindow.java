package com.jdbc;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;

public class HomeWindow extends JFrame implements ActionListener{
	
    private static final String JDBC_URL = "jdbc:mysql://localhost:3306/mscitm";
    private static final String USERNAME = "root";
    private static final String PASSWORD = "";
       
	
    public HomeWindow() {
        setTitle("Library Management System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 1200);
        setLayout(new BorderLayout());
        
        JLabel titleLabel = new JLabel("Library Management System", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        add(titleLabel, BorderLayout.NORTH);
        
        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton adminButton = new JButton("Admin Login");
        JButton studentButton = new JButton("Student Login");

        adminButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showAdminLogin();
            }
        });
        
        studentButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showStudentLogin();
            }
        });
        
        buttonPanel.add(adminButton);
        buttonPanel.add(studentButton);

        add(buttonPanel, BorderLayout.CENTER);

        setVisible(true); 
 }
    
    private void showAdminLogin() {
 	   LoginForm adminLoginForm = new LoginForm();
 	   adminLoginForm.setVisible(true);
 	   this.setVisible(false);
    }
    
    private void showStudentLogin() {
 	   StudentLoginForm studentLoginForm = new StudentLoginForm();
 	  studentLoginForm.setVisible(true);
 	   this.setVisible(false);
    }
    

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new HomeWindow();
        });
    }
}

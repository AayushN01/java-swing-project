package com.jdbc;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class BookLog extends JFrame implements ActionListener{
	
    private DefaultTableModel tableModel;

    private static final String JDBC_URL = "jdbc:mysql://localhost:3306/mscitm";
    private static final String USERNAME = "root";
    private static final String PASSWORD = "";
    
    public BookLog() {
        setTitle("Book Log");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1500, 1500); // Adjust the size as needed
        setLayout(new BorderLayout());

        String[] columnNames = {"Transaction ID", "Book Name", "Student Name", "Borrowed Date", "Returned Date"};
        tableModel = new DefaultTableModel(columnNames, 0);
        JTable table = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(table);
        add(scrollPane, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout());

        JButton viewStudentsButton = new JButton("View Students");
        JButton viewBooksButton = new JButton("View Books");
        JButton logoutButton = new JButton("Logout");

        viewStudentsButton.addActionListener(this);
        viewBooksButton.addActionListener(this);
        logoutButton.addActionListener(this);

        buttonPanel.add(viewStudentsButton);
        buttonPanel.add(viewBooksButton);
        buttonPanel.add(logoutButton);

        add(buttonPanel, BorderLayout.NORTH);

        fetchBookLogData();

        setVisible(true);
    }
    
    private void fetchBookLogData() {
        try (Connection connection = DriverManager.getConnection(JDBC_URL, USERNAME, PASSWORD)) {
            String sql = "SELECT bl.id, bl.book_title, bl.student_id, s.name, bl.borrowed_date, bl.returned_date " +
                    "FROM borrowed_books bl " +
                    "JOIN students s ON bl.student_id = s.id";
            try (PreparedStatement statement = connection.prepareStatement(sql);
                 ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    int transactionId = resultSet.getInt("id");
                    String bookName = resultSet.getString("book_title");
                    String studentName = resultSet.getString("name");
                    String borrowedDate = resultSet.getString("borrowed_date");
                    String returnedDate = resultSet.getString("returned_date");

                    Object[] rowData = {transactionId, bookName, studentName, borrowedDate, returnedDate};
                    tableModel.addRow(rowData);
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error fetching book log data from the database: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
 
    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() instanceof JButton) {
            JButton sourceButton = (JButton) e.getSource();
            if (sourceButton.getText().equals("View Students")) {
                openStudentsWindow();
            } else if (sourceButton.getText().equals("View Books")) {
                openBooksWindow();
            } else if (sourceButton.getText().equals("Logout")) {
                logout();
            }
        }
    }

    private void openStudentsWindow() {
        StudentWindow studentWindow = new StudentWindow();
        studentWindow.setVisible(true);
        this.setVisible(false);
    }

    private void openBooksWindow() {
        BookWindow bookWindow = new BookWindow();
        bookWindow.setVisible(true);
        this.setVisible(false);
    }
    
    private void logout()
    {          
    	BookLog.this.setVisible(false);
	   LoginForm adminLoginForm = new LoginForm();
	   adminLoginForm.setVisible(true);
    	
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new BookLog();
        });
    }
}

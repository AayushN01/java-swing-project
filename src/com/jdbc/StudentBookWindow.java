package com.jdbc;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class StudentBookWindow extends JFrame implements ActionListener{
	
    private JComboBox<String> bookDropdown, genreDropdown;
    private JTextField borrowedDateField, returnedDateField;
    private DefaultTableModel allBooksTableModel, borrowedBooksTableModel;
    
    private static final String JDBC_URL = "jdbc:mysql://localhost:3306/mscitm";
    private static final String USERNAME = "root";
    private static final String PASSWORD = "";
    
    public StudentBookWindow() {
        setTitle("Student Book Borrowing");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1500, 1500);
        setLayout(new BorderLayout());

        String[] allBooksColumnNames = {"Book Name", "Author", "Genre", "Published Date", "Quantity"};
        allBooksTableModel = new DefaultTableModel(allBooksColumnNames, 0);
        JTable allBooksTable = new JTable(allBooksTableModel);
        JScrollPane allBooksScrollPane = new JScrollPane(allBooksTable);
        allBooksScrollPane.setPreferredSize(new Dimension(900, 300));
        add(allBooksScrollPane, BorderLayout.CENTER);
        
        fetchAllBooksData();

        
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
 
        genreDropdown = new JComboBox<>();
        populateGenreDropdown();
        searchPanel.add(new JLabel("Select Genre:"));
        searchPanel.add(genreDropdown);
        
        JButton searchButton = new JButton("Search");
        searchButton.addActionListener(this);
        searchPanel.add(searchButton);
        
 	   JButton logoutButton = new JButton("Logout");
       searchPanel.add(logoutButton);

       logoutButton.addActionListener(new ActionListener() {
    	   @Override
    	   public void actionPerformed(ActionEvent e)
    	   {	
    	 	   StudentLoginForm studentLoginForm = new StudentLoginForm();
    	 	   studentLoginForm.setVisible(true);
    		   StudentBookWindow.this.setVisible(false);
    		   
    	   }
       });
       
        
        add(searchPanel, BorderLayout.NORTH);
        
        String[] borrowedBooksColumnNames = {"Book Name", "Borrowed Date", "Returned Date"};
        borrowedBooksTableModel = new DefaultTableModel(borrowedBooksColumnNames, 0);
        JTable borrowedBooksTable = new JTable(borrowedBooksTableModel);
        JScrollPane borrowedBooksScrollPane = new JScrollPane(borrowedBooksTable);
        borrowedBooksScrollPane.setPreferredSize(new Dimension(900, 200));
        add(borrowedBooksScrollPane, BorderLayout.SOUTH);
        
        JPanel formPanel = new JPanel(new GridLayout(4, 2));
        formPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        formPanel.add(new JLabel("Select Book:"));
        bookDropdown = new JComboBox<>();
        formPanel.add(bookDropdown);
        
        formPanel.add(new JLabel("Borrowed Date:"));
        borrowedDateField = new JTextField();
        formPanel.add(borrowedDateField);
        
        formPanel.add(new JLabel("Returned Date:"));
        returnedDateField = new JTextField();
        formPanel.add(returnedDateField);
        
        JButton borrowButton = new JButton("Borrow Book");
        borrowButton.addActionListener(this);
        formPanel.add(borrowButton);
        
        JButton returnButton = new JButton("Return Book");
        returnButton.addActionListener(this);
        formPanel.add(returnButton);
		
        add(formPanel, BorderLayout.WEST);

        populateBookDropdown();
        
        
        setVisible(true);
    }
    
    private void populateGenreDropdown() {
        try (Connection connection = DriverManager.getConnection(JDBC_URL, USERNAME, PASSWORD)) {
            String sql = "SELECT DISTINCT genre FROM books";
            try (PreparedStatement statement = connection.prepareStatement(sql);
                 ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    String genre = resultSet.getString("genre");
                    genreDropdown.addItem(genre);
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error fetching genres from the database: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void populateBookDropdown() {
        try (Connection connection = DriverManager.getConnection(JDBC_URL, USERNAME, PASSWORD)) {
            String sql = "SELECT title FROM books";
            try (PreparedStatement statement = connection.prepareStatement(sql);
                 ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    String bookTitle = resultSet.getString("title");
                    bookDropdown.addItem(bookTitle);
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error fetching book list: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
	private void fetchAllBooksData() {
		try (Connection connection = DriverManager.getConnection(JDBC_URL, USERNAME, PASSWORD)) {
            String sql = "SELECT * FROM books";
            try (PreparedStatement statement = connection.prepareStatement(sql);
                 ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    String bookName = resultSet.getString("title");
                    String author = resultSet.getString("author");
                    String genre = resultSet.getString("genre");
                    String publishedDate = resultSet.getString("publishedDate");
                    String quantity = resultSet.getString("quantity");

                    Object[] rowData = {bookName, author, genre, publishedDate, quantity};
                    allBooksTableModel.addRow(rowData);
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error fetching data from the database: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
	}
    
    private void fetchStudentBookData(String genre) {
        // Implement code to fetch and display student's book data from the database based on genre
        // You need to adapt this method according to your database schema and query
        // Clear existing data in the borrowedBooksTableModel before adding new data
        borrowedBooksTableModel.setRowCount(0);
    }
	    
    private void borrowBook() {
        String selectedBook = (String) bookDropdown.getSelectedItem();
        String borrowedDateString = borrowedDateField.getText();
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
            LocalDate borrowedDate = LocalDate.parse(borrowedDateString, formatter);
            try (Connection connection = DriverManager.getConnection(JDBC_URL, USERNAME, PASSWORD)) {
                String insertSql = "INSERT INTO borrowed_books (student_id, book_title, borrowed_date) VALUES (?, ?, ?)";
                try (PreparedStatement insertStatement = connection.prepareStatement(insertSql)) {

                    int studentId = 1; 
                    insertStatement.setInt(1, studentId);
                    insertStatement.setString(2, selectedBook);
                    insertStatement.setObject(3, borrowedDate);

                    insertStatement.executeUpdate();
                }

                Object[] rowData = {selectedBook, borrowedDate, null};
                borrowedBooksTableModel.addRow(rowData);

                String updateQuantitySql = "UPDATE books SET quantity = quantity - 1 WHERE title = ?";
                try (PreparedStatement updateQuantityStatement = connection.prepareStatement(updateQuantitySql)) {
                    updateQuantityStatement.setString(1, selectedBook);
                    updateQuantityStatement.executeUpdate();
                }

            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Error borrowing book: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }catch (DateTimeParseException e) {
            JOptionPane.showMessageDialog(this, "Invalid date format. Please use DD-MM-YYYY.", "Error", JOptionPane.ERROR_MESSAGE);
        }
       
    }
	    
    private void returnBook() {
        String selectedBook = (String) bookDropdown.getSelectedItem();
        String returnedDateString = returnedDateField.getText();

        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
            LocalDate returnedDate = LocalDate.parse(returnedDateString, formatter);
            
            try (Connection connection = DriverManager.getConnection(JDBC_URL, USERNAME, PASSWORD)) {
                
                String updateReturnedDateSql = "UPDATE borrowed_books SET returned_date = ? WHERE student_id = ? AND book_title = ? AND returned_date IS NULL";
                try (PreparedStatement updateReturnedDateStatement = connection.prepareStatement(updateReturnedDateSql)) {
                    int studentId = 1; 
                    updateReturnedDateStatement.setObject(1, returnedDate);
                    updateReturnedDateStatement.setInt(2, studentId);
                    updateReturnedDateStatement.setString(3, selectedBook);

                    int updatedRows = updateReturnedDateStatement.executeUpdate();
                    if (updatedRows > 0) {
                        for (int i = 0; i < borrowedBooksTableModel.getRowCount(); i++) {
                            if (selectedBook.equals(borrowedBooksTableModel.getValueAt(i, 0))) {
                                borrowedBooksTableModel.setValueAt(returnedDate, i, 2);
                                break;
                            }
                        }

                        String updateQuantitySql = "UPDATE books SET quantity = quantity + 1 WHERE title = ?";
                        try (PreparedStatement updateQuantityStatement = connection.prepareStatement(updateQuantitySql)) {
                            updateQuantityStatement.setString(1, selectedBook);
                            updateQuantityStatement.executeUpdate();
                        }
                    } else {
                        JOptionPane.showMessageDialog(this, "Book not found or already returned.", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }

            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Error returning book: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (DateTimeParseException e) {
            JOptionPane.showMessageDialog(this, "Invalid date format. Please use DD-MM-YYYY.", "Error", JOptionPane.ERROR_MESSAGE);
        }

    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if ("Borrow Book".equals(e.getActionCommand())) {
            borrowBook();
        } else if ("Return Book".equals(e.getActionCommand())) {
            returnBook();
        } else if ("Search".equals(e.getActionCommand())) {
            String selectedGenre = (String) genreDropdown.getSelectedItem();
            searchBooksByGenre(selectedGenre);
        }
    }

    private void searchBooksByGenre(String selectedGenre) {
        // Clear existing data in the table
        allBooksTableModel.setRowCount(0);

        try (Connection connection = DriverManager.getConnection(JDBC_URL, USERNAME, PASSWORD)) {
            String sql = "SELECT * FROM books WHERE genre = ?";
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setString(1, selectedGenre);
                try (ResultSet resultSet = statement.executeQuery()) {
                    while (resultSet.next()) {
                        String bookName = resultSet.getString("title");
                        String author = resultSet.getString("author");
                        String genre = resultSet.getString("genre");
                        String publishedDate = resultSet.getString("publishedDate");
                        String quantity = resultSet.getString("quantity");

                        Object[] rowData = {bookName, author, genre, publishedDate, quantity};
                        allBooksTableModel.addRow(rowData);
                    }
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error searching books by genre: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }



    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            StudentBookWindow studentBookWindow = new StudentBookWindow();
            studentBookWindow.setVisible(true);
        });
    }

}

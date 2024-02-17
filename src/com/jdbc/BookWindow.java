package com.jdbc;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class BookWindow extends JFrame implements ActionListener{
	
	private DefaultTableModel tableModel;
	private JTextField bookNameField, authorField, genreField, publishedDateField, quantityField;
	
    private static final String JDBC_URL = "jdbc:mysql://localhost:3306/mscitm";
    private static final String USERNAME = "root";
    private static final String PASSWORD = "";
	
	public BookWindow() {
		setTitle("Book Inventory");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setSize(1500,1500);
		setLayout(new BorderLayout());
		
		String[] columnNames = {"Book Name","Author","Genre","Published Date","Quantity"};
		tableModel = new DefaultTableModel(columnNames,0);
		JTable table = new JTable(tableModel);
		JScrollPane scrollPane = new JScrollPane(table);
		add(scrollPane,BorderLayout.WEST);
		
		fetchBookData();

		

		
		JPanel formPanel = new JPanel(new GridLayout(6,2));
		formPanel.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
		formPanel.add(new JLabel("Book Name"));
		bookNameField = new JTextField();
		formPanel.add(bookNameField);
		formPanel.add(new JLabel("Author"));
		authorField = new JTextField();
		formPanel.add(authorField);
		formPanel.add(new JLabel("Genre"));
		genreField = new JTextField();
		formPanel.add(genreField);
		formPanel.add(new JLabel("Published Date"));
		publishedDateField = new JTextField();
		formPanel.add(publishedDateField);
		formPanel.add(new JLabel("Quantity"));
		quantityField = new JTextField();
		formPanel.add(quantityField);
		
		JButton addButton  = new JButton("Add Book");
		addButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				addBook();
			}
		});
		
		formPanel.add(addButton);
		add(formPanel,BorderLayout.CENTER);
		
		setVisible(true);
	}
	
	private void initializeTable(){
		String[] columnNames = {"Book Name","Author","Genre","Published Date","Quantity"};
		tableModel = new DefaultTableModel(columnNames,0);
		JTable table = new JTable(tableModel);
		JScrollPane scrollPane = new JScrollPane(table);
		add(scrollPane,BorderLayout.WEST);
		
	}
	
	private void fetchBookData() {
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
                    tableModel.addRow(rowData);
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error fetching data from the database: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
	}
	
	private void addBook()
	{
		String bookName = bookNameField.getText();
		String author = authorField.getText();
		String genre = genreField.getText();
		String publishedDate = publishedDateField.getText();
		String quantity = quantityField.getText();
		
		if(bookName.isEmpty())
		{
			JOptionPane.showMessageDialog(this, "Book name is required.","Error",JOptionPane.ERROR_MESSAGE);
			return;
		}
		
	        try (Connection connection = DriverManager.getConnection(JDBC_URL, USERNAME, PASSWORD)) {
   	            String sql = "INSERT INTO books (title,author,genre,publishedDate,quantity) VALUES (?, ?,?,?,?)";
   	            try (PreparedStatement statement = connection.prepareStatement(sql)) {
//   	                statement.setString(1, id);
   	                statement.setString(1, bookName);
   	                statement.setString(2, author);
   	                statement.setString(3, genre);
   	                statement.setString(4, publishedDate);
   	                statement.setString(5, quantity); 
   	                statement.executeUpdate();
   	                
   	                JOptionPane.showMessageDialog(this,"Book has been added.");
   	            }
   	        } catch (SQLException ex) {
   	            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
   	        }
		
        Object[] rowData = {bookName, author, genre, publishedDate,quantity};
        tableModel.addRow(rowData);
        
        bookNameField.setText("");
        authorField.setText("");
        genreField.setText("");
        publishedDateField.setText("");
        quantityField.setText("");
	}
	
	

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		SwingUtilities.invokeLater(()->{
			BookWindow bookWindow = new BookWindow();
			bookWindow.setVisible(true);
			bookWindow.setLayout(null);
		});
	}

}

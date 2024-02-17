package com.jdbc;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class BookWindow extends JFrame implements ActionListener{
	
	private DefaultTableModel tableModel;
	private JTextField bookNameField, authorField, genreField, publishedDateField, quantityField;
	private JComboBox<String> genreDropdown;
	
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
		scrollPane.setPreferredSize(new Dimension(900, 600)); 
		add(scrollPane,BorderLayout.WEST);
		
		fetchBookData();
		
		JPanel formPanel = new JPanel(new GridLayout(8,2));
		formPanel.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
		 formPanel.setPreferredSize(new Dimension(300, 600)); 
		
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
		
        JPanel genrePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        genrePanel.add(new JLabel("Select Genre:"));
        genreDropdown = new JComboBox<>();
        genrePanel.add(genreDropdown);
        
        JButton searchButton = new JButton("Search by Genre");
        searchButton.addActionListener(this);
        genrePanel.add(searchButton);
        populateGenreDropdown();
        
        JButton studentsButton = new JButton("View Students");
        studentsButton.addActionListener(this);
        genrePanel.add(studentsButton);
        studentsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
               openStudentsWindow();
            }
        });
        
        JButton logsButton = new JButton("View Logs");
        logsButton.addActionListener(this);
        genrePanel.add(logsButton);
        logsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
               openBookLogsWindow();
            }
        });
        JButton logoutButton = new JButton("Logout");
        logoutButton.addActionListener(this);
        genrePanel.add(logoutButton);
        logoutButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
               BookWindow.this.setVisible(false);
          	   LoginForm adminLoginForm = new LoginForm();
         	   adminLoginForm.setVisible(true);
            }
        });
      
        add(genrePanel, BorderLayout.NORTH);

		
		setVisible(true);
	}
	
//	private void initializeTable(){
//		String[] columnNames = {"Book Name","Author","Genre","Published Date","Quantity"};
//		tableModel = new DefaultTableModel(columnNames,0);
//		JTable table = new JTable(tableModel);
//		JScrollPane scrollPane = new JScrollPane(table);
//		add(scrollPane,BorderLayout.WEST);
//		
//	}
	
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
    
    private void searchBooksByGenre(String selectedGenre) {
        // Clear existing data in the table
        tableModel.setRowCount(0);

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
                        tableModel.addRow(rowData);
                    }
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error searching books by genre: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
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
	
    @Override
    public void actionPerformed(ActionEvent e) {
        if ("Add Book".equals(e.getActionCommand())) {
            addBook();
        } else if ("Search by Genre".equals(e.getActionCommand())) {
            String selectedGenre = (String) genreDropdown.getSelectedItem();
            if (selectedGenre != null) {
                searchBooksByGenre(selectedGenre);
            }
        }
    }
	
    private void openStudentsWindow()
    {
  	   StudentWindow studentWindow = new StudentWindow();
  	 studentWindow.setVisible(true);
  	   this.setVisible(false);
    }
	
    private void openBookLogsWindow()
    {
    	BookLog bookLog = new BookLog();
    	bookLog.setVisible(true);
    	this.setVisible(false);
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

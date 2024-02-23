package com.designPattern;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class BookGenre {
	
	private static BookGenre instance;
	
	//private constructor to prevent instantiation
	private BookGenre() {
		
	}
	
	public static synchronized BookGenre getInstance()
	{
		if(instance == null)
		{
			instance = new BookGenre();
		}
		
		return instance;
	}
	
    public void fetchAllBooksData(DatabaseConnection database, JTable table, DefaultTableModel tableModel) {
        tableModel.setRowCount(0); // Initialize the tableModel before using it
        try (Connection connection = database.getConnection()) {
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
            JOptionPane.showMessageDialog(null, "Error fetching data from the database: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
	
    public void populateGenreDropdown(DatabaseConnection database, JComboBox<String> genreDropdown) {
        try (Connection connection = database.getConnection()) {
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
            JOptionPane.showMessageDialog(null, "Error fetching genres from the database: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    public void searchBooksByGenre(DatabaseConnection database, JTable table, DefaultTableModel tableModel, String selectedGenre) {
        tableModel.setRowCount(0);

        try (Connection connection = database.getConnection()) {
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
            JOptionPane.showMessageDialog(null, "Error searching books by genre: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    
	
}

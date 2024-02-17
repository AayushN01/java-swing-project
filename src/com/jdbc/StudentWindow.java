package com.jdbc;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;

public class StudentWindow extends JFrame implements ActionListener{
	
	private DefaultTableModel tableModel;
	private JTextField idField, nameField, emailField, facultyField;
	private JPasswordField passwordField;
	private JComboBox<String> facultyDropdown;
	
    private static final String JDBC_URL = "jdbc:mysql://localhost:3306/mscitm";
    private static final String USERNAME = "root";
    private static final String PASSWORD = "";
    
    public StudentWindow() {
		setTitle("Student List");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setSize(1500,1500);
		setLayout(new BorderLayout());
		
		String[] columnNames = {"ID","Name","Email","Faculty"};
		tableModel = new DefaultTableModel(columnNames,0);
		JTable table = new JTable(tableModel);
		JScrollPane scrollPane = new JScrollPane(table);
		add(scrollPane,BorderLayout.WEST);
		
		fetchStudentData();
		
		JPanel formPanel = new JPanel(new GridLayout(8,2));
		formPanel.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
		
		formPanel.add(new JLabel("ID Number"));
		idField = new JTextField();
		formPanel.add(idField);
		
		formPanel.add(new JLabel("Name"));
		nameField = new JTextField();
		formPanel.add(nameField);
		
		formPanel.add(new JLabel("Email"));
		emailField = new JTextField();
		formPanel.add(emailField);
		
		formPanel.add(new JLabel("Password"));
        passwordField = new JPasswordField();
		formPanel.add(passwordField);
				
		formPanel.add(new JLabel("Faculty"));
		facultyField = new JTextField();
		formPanel.add(facultyField);
		
		
		JButton addButton  = new JButton("Add Student");
		addButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				addStudent();
			}
		});
		
		formPanel.add(addButton);
		add(formPanel,BorderLayout.CENTER);
		
        JPanel facultyPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        facultyPanel.add(new JLabel("Select Faculty:"));
        facultyDropdown = new JComboBox<>();
        facultyPanel.add(facultyDropdown);
        
        JButton searchButton = new JButton("Search by Faculty");
        searchButton.addActionListener(this);
        facultyPanel.add(searchButton);
        populateFacultyDropdown();
        
        JButton booksButton = new JButton("View Books");
        booksButton.addActionListener(this);
        facultyPanel.add(booksButton);
        booksButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
               openBooksWindow();
            }
        });
        
        JButton logsButton = new JButton("View Logs");
        logsButton.addActionListener(this);
        facultyPanel.add(logsButton);
        logsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
               openBookLogsWindow();
            }
        });
        
        
        JButton logoutButton = new JButton("Logout");
        logoutButton.addActionListener(this);
        facultyPanel.add(logoutButton);
        logoutButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                StudentWindow.this.setVisible(false);
           	   LoginForm adminLoginForm = new LoginForm();
          	   adminLoginForm.setVisible(true);
            }
        });

        add(facultyPanel, BorderLayout.NORTH);

		
		setVisible(true);
	}
    
	private void fetchStudentData() {
		try (Connection connection = DriverManager.getConnection(JDBC_URL, USERNAME, PASSWORD)) {
            String sql = "SELECT * FROM students";
            try (PreparedStatement statement = connection.prepareStatement(sql);
                 ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                	Integer id = resultSet.getInt("id");
                    String name = resultSet.getString("name");
                    String email = resultSet.getString("email");
                    String faculty = resultSet.getString("faculty");

                    Object[] rowData = {id, name, email, faculty};
                    tableModel.addRow(rowData);
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error fetching data from the database: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
	}
	
    private void populateFacultyDropdown() {
        try (Connection connection = DriverManager.getConnection(JDBC_URL, USERNAME, PASSWORD)) {
            String sql = "SELECT DISTINCT faculty FROM students";
            try (PreparedStatement statement = connection.prepareStatement(sql);
                 ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    String faculty = resultSet.getString("faculty");
                    facultyDropdown.addItem(faculty);
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error fetching genres from the database: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void searchStudentsByFaculty(String selectedFaculty) {
        // Clear existing data in the table
        tableModel.setRowCount(0);

        try (Connection connection = DriverManager.getConnection(JDBC_URL, USERNAME, PASSWORD)) {
            String sql = "SELECT * FROM students WHERE faculty = ?";
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setString(1, selectedFaculty);
                try (ResultSet resultSet = statement.executeQuery()) {
                    while (resultSet.next()) {
                        String id = resultSet.getString("id");
                        String name = resultSet.getString("name");
                        String email = resultSet.getString("email");
                        String faculty = resultSet.getString("faculty");

                        Object[] rowData = {id, name, email, faculty};
                        tableModel.addRow(rowData);
                    }
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error searching books by genre: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
	private void addStudent()
	{
		String id = idField.getText();
		String name = nameField.getText();
		String email = emailField.getText();
	    char[] passwordChars = passwordField.getPassword();
	    String password = new String(passwordChars);
		String faculty = facultyField.getText();
		
		if(name.isEmpty() || email.isEmpty() || password.isEmpty() || faculty.isEmpty())
		{
			JOptionPane.showMessageDialog(this, "All fields are required.","Error",JOptionPane.ERROR_MESSAGE);
			return;
		}
		
	       try (Connection connection = DriverManager.getConnection(JDBC_URL, USERNAME, PASSWORD)) {
   	            String sql = "INSERT INTO students (id,name,email,password,faculty) VALUES (?, ?,?,?,?)";
   	            try (PreparedStatement statement = connection.prepareStatement(sql)) {
//   	                statement.setString(1, id);
   	                statement.setString(1, id);
   	                statement.setString(2, name);
   	                statement.setString(3, email);
   	                statement.setString(4, password);
   	                statement.setString(5, faculty); 
   	                statement.executeUpdate();
   	                
   	                JOptionPane.showMessageDialog(this,"Student has been added.");
   	            }
   	        } catch (SQLException ex) {
   	            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
   	        }
		
        Object[] rowData = {id, name, email, faculty};
        tableModel.addRow(rowData);
        
        idField.setText("");
        nameField.setText("");
        emailField.setText("");
        facultyField.setText("");
	}
	
    @Override
    public void actionPerformed(ActionEvent e) {
        if ("Add Student".equals(e.getActionCommand())) {
            addStudent();
        } else if ("Search by Faculty".equals(e.getActionCommand())) {
            String selectedFaculty = (String) facultyDropdown.getSelectedItem();
            if (selectedFaculty != null) {
            	searchStudentsByFaculty(selectedFaculty);
            }
        }
    }
	
    private void openBooksWindow()
    {
  	   BookWindow bookWindow = new BookWindow();
  	   bookWindow.setVisible(true);
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
			StudentWindow studentWindow = new StudentWindow();
			studentWindow.setVisible(true);
			studentWindow.setLayout(null);
		});
	}

}

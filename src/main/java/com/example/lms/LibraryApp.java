package com.example.lms;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import java.sql.*;

public class LibraryApp extends Application {

    private TableView<Book> table = new TableView<>();
    private TextField titleField = new TextField();
    private TextField authorField = new TextField();

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {

        primaryStage.setTitle("Library Management System");

        // --- 1. UI Elements (Input Fields) ---
        titleField.setPromptText("Book Title");
        authorField.setPromptText("Author");
        Button addButton = new Button("Add Book");
        Button refreshButton = new Button("Refresh Table");

        // --- 2. Table Columns ---
        TableColumn<Book, Integer> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));

        TableColumn<Book, String> titleCol = new TableColumn<>("Title");
        titleCol.setCellValueFactory(new PropertyValueFactory<>("title"));

        TableColumn<Book, String> authorCol = new TableColumn<>("Author");
        authorCol.setCellValueFactory(new PropertyValueFactory<>("author"));

        table.getColumns().addAll(idCol, titleCol, authorCol);

        // --- 3. Button Actions ---
        addButton.setOnAction(e -> addBook());
        refreshButton.setOnAction(e -> loadData());

        // --- 4. Layout ---
        HBox inputLayout = new HBox(10, titleField, authorField, addButton, refreshButton);
        inputLayout.setPadding(new Insets(10));

        VBox mainLayout = new VBox(10, inputLayout, table);
        mainLayout.setPadding(new Insets(10));

        primaryStage.setScene(new Scene(mainLayout, 600, 400));
        scene.getStylesheets().add(getClass().getResource("/dashboard.css").toExternalForm());
        primaryStage.show();
        
        loadData(); // Load data on startup
    }

    // Method to insert data into MariaDB
    private void addBook() {
        String sql = "INSERT INTO books (title, author) VALUES (?, ?)";
        try (Connection conn = DatabaseHandler.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, titleField.getText());
            pstmt.setString(2, authorField.getText());
            pstmt.executeUpdate();
            
            titleField.clear();
            authorField.clear();
            loadData(); // Refresh table
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Method to fetch data from MariaDB
    private void loadData() {
        ObservableList<Book> data = FXCollections.observableArrayList();
        String sql = "SELECT * FROM books";
        
        try (Connection conn = DatabaseHandler.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                data.add(new Book(rs.getInt("book_id"), rs.getString("title"), rs.getString("author")));
            }
            table.setItems(data);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Simple Book Model Class
    public static class Book {
        private int id;
        private String title;
        private String author;

        public Book(int id, String title, String author) {
            this.id = id;
            this.title = title;
            this.author = author;
        }
        public int getId() { return id; }
        public String getTitle() { return title; }
        public String getAuthor() { return author; }
    }
}
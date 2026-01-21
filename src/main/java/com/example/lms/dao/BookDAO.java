package com.example.lms.dao;

import com.example.lms.models.Book;
import com.example.lms.util.DatabaseUtil;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BookDAO {
    
    public List<Book> getAllBooks() {
        List<Book> books = new ArrayList<>();
        String sql = "SELECT * FROM books";
        try (Connection conn = DatabaseUtil.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                books.add(new Book(rs.getInt("book_id"), rs.getString("title"), 
                          rs.getString("author"), rs.getString("status")));
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return books;
    }

    public void addBook(String title, String author) {
        String sql = "INSERT INTO books (title, author, status) VALUES (?, ?, 'Available')";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStaterrrment(sql)) {
            pstmt.setString(1, title);
            pstmt.setString(2, author);
            pstmt.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }
}
package com.example.lms.dao;

import com.example.lms.models.Book;
import com.example.lms.util.DatabaseUtil;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BookDAO {
    
    // Get all books
    public List<Book> getAllBooks() {
        List<Book> books = new ArrayList<>();
        String sql = "SELECT * FROM books ORDER BY title";
        try (Connection conn = DatabaseUtil.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                books.add(extractBookFromResultSet(rs));
            }
        } catch (SQLException e) { 
            e.printStackTrace(); 
        }
        return books;
    }

    // Add a new book
    public boolean addBook(Book book) {
        String sql = "INSERT INTO books (isbn, title, author, genre, publication_year, total_quantity, available_quantity, status) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, book.getIsbn());
            pstmt.setString(2, book.getTitle());
            pstmt.setString(3, book.getAuthor());
            pstmt.setString(4, book.getGenre());
            if (book.getPublicationYear() != null) {
                pstmt.setInt(5, book.getPublicationYear());
            } else {
                pstmt.setNull(5, Types.INTEGER);
            }
            pstmt.setInt(6, book.getTotalQuantity());
            pstmt.setInt(7, book.getAvailableQuantity());
            pstmt.setString(8, book.getStatus());
            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) { 
            e.printStackTrace(); 
            return false;
        }
    }

    // Update existing book
    public boolean updateBook(Book book) {
        String sql = "UPDATE books SET isbn=?, title=?, author=?, genre=?, publication_year=?, " +
                     "total_quantity=?, available_quantity=?, status=? WHERE book_id=?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, book.getIsbn());
            pstmt.setString(2, book.getTitle());
            pstmt.setString(3, book.getAuthor());
            pstmt.setString(4, book.getGenre());
            if (book.getPublicationYear() != null) {
                pstmt.setInt(5, book.getPublicationYear());
            } else {
                pstmt.setNull(5, Types.INTEGER);
            }
            pstmt.setInt(6, book.getTotalQuantity());
            pstmt.setInt(7, book.getAvailableQuantity());
            pstmt.setString(8, book.getStatus());
            pstmt.setInt(9, book.getId());
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) { 
            e.printStackTrace(); 
            return false;
        }
    }

    // Delete a book
    public boolean deleteBook(int bookId) {
        // First check if book has ANY transactions (active or historical)
        String checkSql = "SELECT COUNT(*) FROM transactions WHERE book_id=?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement checkStmt = conn.prepareStatement(checkSql)) {
            checkStmt.setInt(1, bookId);
            ResultSet rs = checkStmt.executeQuery();
            if (rs.next() && rs.getInt(1) > 0) {
                int count = rs.getInt(1);
                System.err.println("Cannot delete book: " + count + " transaction(s) exist in history");
                return false;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }

        // Delete the book
        String sql = "DELETE FROM books WHERE book_id=?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, bookId);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) { 
            e.printStackTrace(); 
            return false;
        }
    }

    // Search books by title, author, or ISBN
    public List<Book> searchBooks(String query) {
        List<Book> books = new ArrayList<>();
        String sql = "SELECT * FROM books WHERE title LIKE ? OR author LIKE ? OR isbn LIKE ? ORDER BY title";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            String searchPattern = "%" + query + "%";
            pstmt.setString(1, searchPattern);
            pstmt.setString(2, searchPattern);
            pstmt.setString(3, searchPattern);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                books.add(extractBookFromResultSet(rs));
            }
        } catch (SQLException e) { 
            e.printStackTrace(); 
        }
        return books;
    }

    // Get book by ISBN
    public Book getBookByISBN(String isbn) {
        String sql = "SELECT * FROM books WHERE isbn=?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, isbn);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return extractBookFromResultSet(rs);
            }
        } catch (SQLException e) { 
            e.printStackTrace(); 
        }
        return null;
    }

    // Get book by ID
    public Book getBookById(int bookId) {
        String sql = "SELECT * FROM books WHERE book_id=?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, bookId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return extractBookFromResultSet(rs);
            }
        } catch (SQLException e) { 
            e.printStackTrace(); 
        }
        return null;
    }

    // Update book availability
    public boolean updateAvailability(int bookId, int change) {
        String sql = "UPDATE books SET available_quantity = available_quantity + ? WHERE book_id=?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, change);
            pstmt.setInt(2, bookId);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) { 
            e.printStackTrace(); 
            return false;
        }
    }

    // Get available books only
    public List<Book> getAvailableBooks() {
        List<Book> books = new ArrayList<>();
        String sql = "SELECT * FROM books WHERE available_quantity > 0 ORDER BY title";
        try (Connection conn = DatabaseUtil.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                books.add(extractBookFromResultSet(rs));
            }
        } catch (SQLException e) { 
            e.printStackTrace(); 
        }
        return books;
    }

    // Helper method to extract Book from ResultSet
    private Book extractBookFromResultSet(ResultSet rs) throws SQLException {
        return new Book(
            rs.getInt("book_id"),
            rs.getString("isbn"),
            rs.getString("title"),
            rs.getString("author"),
            rs.getString("genre"),
            rs.getObject("publication_year", Integer.class),
            rs.getInt("total_quantity"),
            rs.getInt("available_quantity"),
            rs.getString("status")
        );
    }
}
package com.example.lms.dao;

import com.example.lms.models.Transaction;
import com.example.lms.util.DatabaseUtil;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class TransactionDAO {
    
    private static final int DUE_DAYS = 14; // Default due period
    
    // Issue a book to a member
    public boolean issueBook(int bookId, int memberId) {
        Connection conn = null;
        try {
            conn = DatabaseUtil.getConnection();
            conn.setAutoCommit(false); // Start transaction
            
            // Check if book is available
            String checkSql = "SELECT available_quantity FROM books WHERE book_id=?";
            PreparedStatement checkStmt = conn.prepareStatement(checkSql);
            checkStmt.setInt(1, bookId);
            ResultSet rs = checkStmt.executeQuery();
            
            if (!rs.next() || rs.getInt("available_quantity") <= 0) {
                conn.rollback();
                System.err.println("Book not available");
                return false;
            }
            
            // Update book availability
            String updateBookSql = "UPDATE books SET available_quantity = available_quantity - 1 WHERE book_id=?";
            PreparedStatement updateStmt = conn.prepareStatement(updateBookSql);
            updateStmt.setInt(1, bookId);
            updateStmt.executeUpdate();
            
            // Create transaction record
            LocalDate issueDate = LocalDate.now();
            LocalDate dueDate = issueDate.plusDays(DUE_DAYS);
            
            String insertSql = "INSERT INTO transactions (book_id, member_id, issue_date, due_date, status) VALUES (?, ?, ?, ?, 'Issued')";
            PreparedStatement insertStmt = conn.prepareStatement(insertSql);
            insertStmt.setInt(1, bookId);
            insertStmt.setInt(2, memberId);
            insertStmt.setDate(3, Date.valueOf(issueDate));
            insertStmt.setDate(4, Date.valueOf(dueDate));
            insertStmt.executeUpdate();
            
            conn.commit();
            return true;
            
        } catch (SQLException e) {
            try {
                if (conn != null) conn.rollback();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            e.printStackTrace();
            return false;
        } finally {
            try {
                if (conn != null) {
                    conn.setAutoCommit(true);
                    conn.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
    
    // Return a book
    public boolean returnBook(int transactionId) {
        Connection conn = null;
        try {
            conn = DatabaseUtil.getConnection();
            conn.setAutoCommit(false);
            
            // Get transaction details
            String getTxnSql = "SELECT book_id, status FROM transactions WHERE transaction_id=?";
            PreparedStatement getTxnStmt = conn.prepareStatement(getTxnSql);
            getTxnStmt.setInt(1, transactionId);
            ResultSet rs = getTxnStmt.executeQuery();
            
            if (!rs.next() || !rs.getString("status").equals("Issued")) {
                conn.rollback();
                System.err.println("Transaction not found or already returned");
                return false;
            }
            
            int bookId = rs.getInt("book_id");
            
            // Update transaction
            String updateTxnSql = "UPDATE transactions SET return_date=?, status='Returned' WHERE transaction_id=?";
            PreparedStatement updateTxnStmt = conn.prepareStatement(updateTxnSql);
            updateTxnStmt.setDate(1, Date.valueOf(LocalDate.now()));
            updateTxnStmt.setInt(2, transactionId);
            updateTxnStmt.executeUpdate();
            
            // Update book availability
            String updateBookSql = "UPDATE books SET available_quantity = available_quantity + 1 WHERE book_id=?";
            PreparedStatement updateBookStmt = conn.prepareStatement(updateBookSql);
            updateBookStmt.setInt(1, bookId);
            updateBookStmt.executeUpdate();
            
            conn.commit();
            return true;
            
        } catch (SQLException e) {
            try {
                if (conn != null) conn.rollback();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            e.printStackTrace();
            return false;
        } finally {
            try {
                if (conn != null) {
                    conn.setAutoCommit(true);
                    conn.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
    
    // Get all active (issued) transactions
    public List<Transaction> getActiveTransactions() {
        List<Transaction> transactions = new ArrayList<>();
        String sql = "SELECT t.*, b.title as book_title, m.name as member_name " +
                     "FROM transactions t " +
                     "JOIN books b ON t.book_id = b.book_id " +
                     "JOIN members m ON t.member_id = m.member_id " +
                     "WHERE t.status='Issued' " +
                     "ORDER BY t.due_date";
        try (Connection conn = DatabaseUtil.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                transactions.add(extractTransactionFromResultSet(rs, true));
            }
        } catch (SQLException e) { 
            e.printStackTrace(); 
        }
        return transactions;
    }
    
    // Get all transactions (including returned)
    public List<Transaction> getAllTransactions() {
        List<Transaction> transactions = new ArrayList<>();
        String sql = "SELECT t.*, b.title as book_title, m.name as member_name " +
                     "FROM transactions t " +
                     "JOIN books b ON t.book_id = b.book_id " +
                     "JOIN members m ON t.member_id = m.member_id " +
                     "ORDER BY t.issue_date DESC";
        try (Connection conn = DatabaseUtil.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                transactions.add(extractTransactionFromResultSet(rs, true));
            }
        } catch (SQLException e) { 
            e.printStackTrace(); 
        }
        return transactions;
    }
    
    // Get transactions by member
    public List<Transaction> getTransactionsByMember(int memberId) {
        List<Transaction> transactions = new ArrayList<>();
        String sql = "SELECT t.*, b.title as book_title, m.name as member_name " +
                     "FROM transactions t " +
                     "JOIN books b ON t.book_id = b.book_id " +
                     "JOIN members m ON t.member_id = m.member_id " +
                     "WHERE t.member_id=? " +
                     "ORDER BY t.issue_date DESC";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, memberId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                transactions.add(extractTransactionFromResultSet(rs, true));
            }
        } catch (SQLException e) { 
            e.printStackTrace(); 
        }
        return transactions;
    }
    
    // Get overdue transactions
    public List<Transaction> getOverdueTransactions() {
        List<Transaction> transactions = new ArrayList<>();
        String sql = "SELECT t.*, b.title as book_title, m.name as member_name " +
                     "FROM transactions t " +
                     "JOIN books b ON t.book_id = b.book_id " +
                     "JOIN members m ON t.member_id = m.member_id " +
                     "WHERE t.status='Issued' AND t.due_date < CURRENT_DATE " +
                     "ORDER BY t.due_date";
        try (Connection conn = DatabaseUtil.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                transactions.add(extractTransactionFromResultSet(rs, true));
            }
        } catch (SQLException e) { 
            e.printStackTrace(); 
        }
        return transactions;
    }
    
    // Helper method to extract Transaction from ResultSet
    private Transaction extractTransactionFromResultSet(ResultSet rs, boolean includeJoinedData) throws SQLException {
        Date returnDate = rs.getDate("return_date");
        Transaction txn = new Transaction(
            rs.getInt("transaction_id"),
            rs.getInt("book_id"),
            rs.getInt("member_id"),
            rs.getDate("issue_date").toLocalDate(),
            rs.getDate("due_date").toLocalDate(),
            returnDate != null ? returnDate.toLocalDate() : null,
            rs.getString("status")
        );
        
        if (includeJoinedData) {
            txn.setBookTitle(rs.getString("book_title"));
            txn.setMemberName(rs.getString("member_name"));
        }
        
        return txn;
    }
}

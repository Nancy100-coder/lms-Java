package com.example.lms.dao;

import com.example.lms.util.DatabaseUtil;

import java.sql.*;

public class LibraryDAO {
    
    // Issue Book Logic (Transaction)
    public boolean issueBook(int bookId, int memberId) {
        String insertTrans = "INSERT INTO transactions (book_id, member_id, issue_date) VALUES (?, ?, CURDATE())";
        String updateBook = "UPDATE books SET status = 'Issued' WHERE book_id = ?";

        try (Connection conn = DatabaseUtil.getConnection()) {
            conn.setAutoCommit(false); // Start Transaction
            try (PreparedStatement ps1 = conn.prepareStatement(insertTrans);
                 PreparedStatement ps2 = conn.prepareStatement(updateBook)) {
                
                ps1.setInt(1, bookId);
                ps1.setInt(2, memberId);
                ps1.executeUpdate();

                ps2.setInt(1, bookId);
                ps2.executeUpdate();

                conn.commit(); // Save both changes
                return true;
            } catch (SQLException e) {
                conn.rollback();
                return false;
            }
        } catch (SQLException e) { return false; }
    }
}
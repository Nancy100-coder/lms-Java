package com.example.lms.dao;

import com.example.lms.models.Member;
import com.example.lms.util.DatabaseUtil;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class MemberDAO {
    
    // Get all members
    public List<Member> getAllMembers() {
        List<Member> members = new ArrayList<>();
        String sql = "SELECT * FROM members ORDER BY name";
        try (Connection conn = DatabaseUtil.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                members.add(extractMemberFromResultSet(rs));
            }
        } catch (SQLException e) { 
            e.printStackTrace(); 
        }
        return members;
    }

    // Add a new member
    public boolean addMember(Member member) {
        String sql = "INSERT INTO members (name, email, phone, address, registration_date, status) " +
                     "VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, member.getName());
            pstmt.setString(2, member.getEmail());
            pstmt.setString(3, member.getPhone());
            pstmt.setString(4, member.getAddress());
            pstmt.setDate(5, Date.valueOf(member.getRegistrationDate()));
            pstmt.setString(6, member.getStatus());
            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) { 
            e.printStackTrace(); 
            return false;
        }
    }

    // Update existing member
    public boolean updateMember(Member member) {
        String sql = "UPDATE members SET name=?, email=?, phone=?, address=?, status=? WHERE member_id=?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, member.getName());
            pstmt.setString(2, member.getEmail());
            pstmt.setString(3, member.getPhone());
            pstmt.setString(4, member.getAddress());
            pstmt.setString(5, member.getStatus());
            pstmt.setInt(6, member.getMemberId());
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) { 
            e.printStackTrace(); 
            return false;
        }
    }

    // Delete a member
    public boolean deleteMember(int memberId) {
        // First check if member has active transactions
        String checkSql = "SELECT COUNT(*) FROM transactions WHERE member_id=? AND status='Issued'";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement checkStmt = conn.prepareStatement(checkSql)) {
            checkStmt.setInt(1, memberId);
            ResultSet rs = checkStmt.executeQuery();
            if (rs.next() && rs.getInt(1) > 0) {
                System.err.println("Cannot delete member with active transactions");
                return false;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }

        // Delete the member
        String sql = "DELETE FROM members WHERE member_id=?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, memberId);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) { 
            e.printStackTrace(); 
            return false;
        }
    }

    // Search members by name, email, or phone
    public List<Member> searchMembers(String query) {
        List<Member> members = new ArrayList<>();
        String sql = "SELECT * FROM members WHERE name LIKE ? OR email LIKE ? OR phone LIKE ? ORDER BY name";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            String searchPattern = "%" + query + "%";
            pstmt.setString(1, searchPattern);
            pstmt.setString(2, searchPattern);
            pstmt.setString(3, searchPattern);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                members.add(extractMemberFromResultSet(rs));
            }
        } catch (SQLException e) { 
            e.printStackTrace(); 
        }
        return members;
    }

    // Get member by email
    public Member getMemberByEmail(String email) {
        String sql = "SELECT * FROM members WHERE email=?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, email);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return extractMemberFromResultSet(rs);
            }
        } catch (SQLException e) { 
            e.printStackTrace(); 
        }
        return null;
    }

    // Get member by ID
    public Member getMemberById(int memberId) {
        String sql = "SELECT * FROM members WHERE member_id=?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, memberId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return extractMemberFromResultSet(rs);
            }
        } catch (SQLException e) { 
            e.printStackTrace(); 
        }
        return null;
    }

    // Get active members only
    public List<Member> getActiveMembers() {
        List<Member> members = new ArrayList<>();
        String sql = "SELECT * FROM members WHERE status='Active' ORDER BY name";
        try (Connection conn = DatabaseUtil.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                members.add(extractMemberFromResultSet(rs));
            }
        } catch (SQLException e) { 
            e.printStackTrace(); 
        }
        return members;
    }

    // Helper method to extract Member from ResultSet
    private Member extractMemberFromResultSet(ResultSet rs) throws SQLException {
        Date regDate = rs.getDate("registration_date");
        return new Member(
            rs.getInt("member_id"),
            rs.getString("name"),
            rs.getString("email"),
            rs.getString("phone"),
            rs.getString("address"),
            regDate != null ? regDate.toLocalDate() : LocalDate.now(),
            rs.getString("status")
        );
    }
}

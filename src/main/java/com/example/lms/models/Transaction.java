package com.example.lms.models;

import java.time.LocalDate;

public class Transaction {
    private int transactionId;
    private int bookId;
    private int memberId;
    private LocalDate issueDate;
    private LocalDate dueDate;
    private LocalDate returnDate;
    private String status;
    
    // For display purposes
    private String bookTitle;
    private String memberName;

    // Full constructor
    public Transaction(int transactionId, int bookId, int memberId, 
                      LocalDate issueDate, LocalDate dueDate, LocalDate returnDate, String status) {
        this.transactionId = transactionId;
        this.bookId = bookId;
        this.memberId = memberId;
        this.issueDate = issueDate;
        this.dueDate = dueDate;
        this.returnDate = returnDate;
        this.status = status;
    }

    // Constructor for new transactions
    public Transaction(int bookId, int memberId, LocalDate issueDate, LocalDate dueDate) {
        this.bookId = bookId;
        this.memberId = memberId;
        this.issueDate = issueDate;
        this.dueDate = dueDate;
        this.status = "Issued";
    }

    // Getters
    public int getTransactionId() { return transactionId; }
    public int getBookId() { return bookId; }
    public int getMemberId() { return memberId; }
    public LocalDate getIssueDate() { return issueDate; }
    public LocalDate getDueDate() { return dueDate; }
    public LocalDate getReturnDate() { return returnDate; }
    public String getStatus() { return status; }
    public String getBookTitle() { return bookTitle; }
    public String getMemberName() { return memberName; }

    // Setters
    public void setTransactionId(int transactionId) { this.transactionId = transactionId; }
    public void setBookId(int bookId) { this.bookId = bookId; }
    public void setMemberId(int memberId) { this.memberId = memberId; }
    public void setIssueDate(LocalDate issueDate) { this.issueDate = issueDate; }
    public void setDueDate(LocalDate dueDate) { this.dueDate = dueDate; }
    public void setReturnDate(LocalDate returnDate) { this.returnDate = returnDate; }
    public void setStatus(String status) { this.status = status; }
    public void setBookTitle(String bookTitle) { this.bookTitle = bookTitle; }
    public void setMemberName(String memberName) { this.memberName = memberName; }

    // Business logic methods
    public boolean isOverdue() {
        return status.equals("Issued") && LocalDate.now().isAfter(dueDate);
    }

    public long getDaysOverdue() {
        if (isOverdue()) {
            return java.time.temporal.ChronoUnit.DAYS.between(dueDate, LocalDate.now());
        }
        return 0;
    }
}

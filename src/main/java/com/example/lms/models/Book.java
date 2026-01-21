package com.example.lms.models;

public class Book {
    private int id;
    private String isbn;
    private String title;
    private String author;
    private String genre;
    private Integer publicationYear;
    private int totalQuantity;
    private int availableQuantity;
    private String status;

    // Full constructor
    public Book(int id, String isbn, String title, String author, String genre, 
                Integer publicationYear, int totalQuantity, int availableQuantity, String status) {
        this.id = id;
        this.isbn = isbn;
        this.title = title;
        this.author = author;
        this.genre = genre;
        this.publicationYear = publicationYear;
        this.totalQuantity = totalQuantity;
        this.availableQuantity = availableQuantity;
        this.status = status;
    }

    // Constructor for adding new books
    public Book(String isbn, String title, String author, String genre, 
                Integer publicationYear, int totalQuantity) {
        this.isbn = isbn;
        this.title = title;
        this.author = author;
        this.genre = genre;
        this.publicationYear = publicationYear;
        this.totalQuantity = totalQuantity;
        this.availableQuantity = totalQuantity;
        this.status = "Available";
    }

    // Getters
    public int getId() { return id; }
    public String getIsbn() { return isbn; }
    public String getTitle() { return title; }
    public String getAuthor() { return author; }
    public String getGenre() { return genre; }
    public Integer getPublicationYear() { return publicationYear; }
    public int getTotalQuantity() { return totalQuantity; }
    public int getAvailableQuantity() { return availableQuantity; }
    public String getStatus() { return status; }

    // Setters
    public void setId(int id) { this.id = id; }
    public void setIsbn(String isbn) { this.isbn = isbn; }
    public void setTitle(String title) { this.title = title; }
    public void setAuthor(String author) { this.author = author; }
    public void setGenre(String genre) { this.genre = genre; }
    public void setPublicationYear(Integer publicationYear) { this.publicationYear = publicationYear; }
    public void setTotalQuantity(int totalQuantity) { this.totalQuantity = totalQuantity; }
    public void setAvailableQuantity(int availableQuantity) { this.availableQuantity = availableQuantity; }
    public void setStatus(String status) { this.status = status; }

    @Override
    public String toString() {
        return title + " by " + author + (isbn != null ? " (ISBN: " + isbn + ")" : "");
    }
}
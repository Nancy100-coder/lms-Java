-- =====================================================
-- LIBRARY MANAGEMENT SYSTEM - COMPLETE DATABASE SCHEMA
-- =====================================================
-- Project: Library Management System (LMS)
-- Database: MariaDB / MySQL
-- Author: Nancy Gathua
-- Date: January 2026
-- =====================================================

-- =====================================================
-- STEP 1: CREATE DATABASE
-- =====================================================

-- Create the database (run once)
CREATE DATABASE IF NOT EXISTS library_db
CHARACTER SET utf8mb4
COLLATE utf8mb4_unicode_ci;

-- Select the database
USE library_db;

-- =====================================================
-- STEP 2: DROP EXISTING TABLES (if starting fresh)
-- =====================================================
-- Uncomment these lines if you want to start fresh
-- DROP TABLE IF EXISTS transactions;
-- DROP TABLE IF EXISTS members;
-- DROP TABLE IF EXISTS books;

-- =====================================================
-- STEP 3: CREATE BOOKS TABLE
-- =====================================================

CREATE TABLE IF NOT EXISTS books (
    -- Primary Key
    book_id INT AUTO_INCREMENT PRIMARY KEY,
    
    -- Book Identification
    isbn VARCHAR(20) UNIQUE COMMENT 'International Standard Book Number',
    title VARCHAR(255) NOT NULL COMMENT 'Book title',
    author VARCHAR(255) NOT NULL COMMENT 'Author name',
    
    -- Book Details
    genre VARCHAR(100) COMMENT 'Category/Genre',
    publication_year INT COMMENT 'Year published',
    
    -- Quantity Management
    total_quantity INT DEFAULT 1 COMMENT 'Total copies owned by library',
    available_quantity INT DEFAULT 1 COMMENT 'Copies currently available for issue',
    
    -- Status
    status VARCHAR(50) DEFAULT 'Available' COMMENT 'Book status',
    
    -- Indexes for search performance
    INDEX idx_title (title),
    INDEX idx_author (author),
    INDEX idx_isbn (isbn),
    INDEX idx_genre (genre)
    
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =====================================================
-- STEP 4: CREATE MEMBERS TABLE
-- =====================================================

CREATE TABLE IF NOT EXISTS members (
    -- Primary Key
    member_id INT AUTO_INCREMENT PRIMARY KEY,
    
    -- Personal Information
    name VARCHAR(255) NOT NULL COMMENT 'Member full name',
    email VARCHAR(255) NOT NULL UNIQUE COMMENT 'Email address (unique)',
    phone VARCHAR(20) COMMENT 'Phone number',
    address TEXT COMMENT 'Physical address',
    
    -- Registration Details
    registration_date DATE DEFAULT (CURRENT_DATE) COMMENT 'Date of registration',
    status VARCHAR(20) DEFAULT 'Active' COMMENT 'Active or Inactive',
    
    -- Indexes for search performance
    INDEX idx_name (name),
    INDEX idx_email (email),
    INDEX idx_status (status)
    
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =====================================================
-- STEP 5: CREATE TRANSACTIONS TABLE
-- =====================================================

CREATE TABLE IF NOT EXISTS transactions (
    -- Primary Key
    transaction_id INT AUTO_INCREMENT PRIMARY KEY,
    
    -- Foreign Keys
    book_id INT NOT NULL COMMENT 'Reference to books table',
    member_id INT NOT NULL COMMENT 'Reference to members table',
    
    -- Transaction Dates
    issue_date DATE NOT NULL COMMENT 'Date book was issued',
    due_date DATE NOT NULL COMMENT 'Date book should be returned',
    return_date DATE COMMENT 'Actual return date (NULL if not returned)',
    
    -- Status
    status VARCHAR(20) DEFAULT 'Issued' COMMENT 'Issued or Returned',
    
    -- Foreign Key Constraints
    CONSTRAINT fk_transaction_book 
        FOREIGN KEY (book_id) REFERENCES books(book_id)
        ON DELETE RESTRICT ON UPDATE CASCADE,
    
    CONSTRAINT fk_transaction_member 
        FOREIGN KEY (member_id) REFERENCES members(member_id)
        ON DELETE RESTRICT ON UPDATE CASCADE,
    
    -- Indexes for performance
    INDEX idx_status (status),
    INDEX idx_book_id (book_id),
    INDEX idx_member_id (member_id),
    INDEX idx_issue_date (issue_date),
    INDEX idx_due_date (due_date)
    
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =====================================================
-- STEP 6: INSERT SAMPLE BOOKS DATA
-- =====================================================

INSERT INTO books (isbn, title, author, genre, publication_year, total_quantity, available_quantity, status) VALUES
-- Classic Literature
('978-0-7432-7356-5', 'The Great Gatsby', 'F. Scott Fitzgerald', 'Classic Fiction', 1925, 3, 3, 'Available'),
('978-0-452-28423-4', '1984', 'George Orwell', 'Dystopian Fiction', 1949, 4, 4, 'Available'),
('978-0-06-112008-4', 'To Kill a Mockingbird', 'Harper Lee', 'Classic Fiction', 1960, 2, 2, 'Available'),
('978-0-14-028329-7', 'Animal Farm', 'George Orwell', 'Political Satire', 1945, 2, 2, 'Available'),
('978-0-14-017739-8', 'Of Mice and Men', 'John Steinbeck', 'Classic Fiction', 1937, 3, 3, 'Available'),

-- Science Fiction
('978-0-06-093546-7', 'Brave New World', 'Aldous Huxley', 'Science Fiction', 1932, 2, 2, 'Available'),
('978-0-441-17271-9', 'Dune', 'Frank Herbert', 'Science Fiction', 1965, 2, 2, 'Available'),
('978-0-553-38256-7', 'Foundation', 'Isaac Asimov', 'Science Fiction', 1951, 1, 1, 'Available'),

-- Modern Fiction
('978-0-7432-7357-2', 'The Kite Runner', 'Khaled Hosseini', 'Contemporary Fiction', 2003, 2, 2, 'Available'),
('978-0-06-093440-8', 'Angels & Demons', 'Dan Brown', 'Thriller', 2000, 3, 3, 'Available'),
('978-0-385-33348-1', 'The Da Vinci Code', 'Dan Brown', 'Thriller', 2003, 4, 4, 'Available'),

-- Non-Fiction
('978-0-06-112241-5', 'Sapiens: A Brief History', 'Yuval Noah Harari', 'Non-Fiction', 2011, 2, 2, 'Available'),
('978-0-7432-6951-3', 'Freakonomics', 'Steven Levitt', 'Economics', 2005, 1, 1, 'Available'),

-- Technology
('978-0-596-51774-8', 'JavaScript: The Good Parts', 'Douglas Crockford', 'Programming', 2008, 2, 2, 'Available'),
('978-0-13-468599-1', 'Clean Code', 'Robert C. Martin', 'Programming', 2008, 3, 3, 'Available')

ON DUPLICATE KEY UPDATE title = title;  -- Prevents errors on re-run

-- =====================================================
-- STEP 7: INSERT SAMPLE MEMBERS DATA
-- =====================================================

INSERT INTO members (name, email, phone, address, registration_date, status) VALUES
('John Kamau', 'john.kamau@email.com', '+254712345678', 'Nairobi, Westlands', '2025-01-15', 'Active'),
('Jane Wanjiku', 'jane.wanjiku@email.com', '+254723456789', 'Nairobi, Karen', '2025-02-20', 'Active'),
('Peter Ochieng', 'peter.ochieng@email.com', '+254734567890', 'Kisumu, Milimani', '2025-03-10', 'Active'),
('Mary Muthoni', 'mary.muthoni@email.com', '+254745678901', 'Nakuru, Central', '2025-04-05', 'Active'),
('David Kiprop', 'david.kiprop@email.com', '+254756789012', 'Eldoret, Town', '2025-05-12', 'Active'),
('Sarah Akinyi', 'sarah.akinyi@email.com', '+254767890123', 'Mombasa, Nyali', '2025-06-18', 'Active'),
('James Mwangi', 'james.mwangi@email.com', '+254778901234', 'Thika, CBD', '2025-07-22', 'Active'),
('Lucy Njeri', 'lucy.njeri@email.com', '+254789012345', 'Nairobi, Lavington', '2025-08-30', 'Active'),
('Michael Otieno', 'michael.otieno@email.com', '+254790123456', 'Kisii, Town Center', '2025-09-14', 'Active'),
('Grace Wambui', 'grace.wambui@email.com', '+254701234567', 'Nyeri, CBD', '2025-10-25', 'Active')

ON DUPLICATE KEY UPDATE name = name;  -- Prevents errors on re-run

-- =====================================================
-- STEP 8: INSERT SAMPLE TRANSACTIONS (OPTIONAL)
-- =====================================================

-- Issue some books to members for demo purposes
INSERT INTO transactions (book_id, member_id, issue_date, due_date, return_date, status) VALUES
-- Active transactions (not returned)
(1, 1, DATE_SUB(CURRENT_DATE, INTERVAL 5 DAY), DATE_ADD(CURRENT_DATE, INTERVAL 9 DAY), NULL, 'Issued'),
(2, 2, DATE_SUB(CURRENT_DATE, INTERVAL 10 DAY), DATE_ADD(CURRENT_DATE, INTERVAL 4 DAY), NULL, 'Issued'),
(5, 3, DATE_SUB(CURRENT_DATE, INTERVAL 3 DAY), DATE_ADD(CURRENT_DATE, INTERVAL 11 DAY), NULL, 'Issued'),

-- Overdue transaction (for demo)
(6, 4, DATE_SUB(CURRENT_DATE, INTERVAL 20 DAY), DATE_SUB(CURRENT_DATE, INTERVAL 6 DAY), NULL, 'Issued'),

-- Returned transactions (completed)
(3, 1, DATE_SUB(CURRENT_DATE, INTERVAL 30 DAY), DATE_SUB(CURRENT_DATE, INTERVAL 16 DAY), DATE_SUB(CURRENT_DATE, INTERVAL 18 DAY), 'Returned'),
(4, 5, DATE_SUB(CURRENT_DATE, INTERVAL 25 DAY), DATE_SUB(CURRENT_DATE, INTERVAL 11 DAY), DATE_SUB(CURRENT_DATE, INTERVAL 12 DAY), 'Returned');

-- Update book availability based on issued books
UPDATE books SET available_quantity = total_quantity - 1 WHERE book_id IN (1, 2, 5, 6);

-- =====================================================
-- STEP 9: USEFUL VIEWS (OPTIONAL)
-- =====================================================

-- View: Active Transactions with Book and Member Details
CREATE OR REPLACE VIEW active_loans AS
SELECT 
    t.transaction_id,
    b.title AS book_title,
    b.isbn,
    m.name AS member_name,
    m.email AS member_email,
    t.issue_date,
    t.due_date,
    DATEDIFF(CURRENT_DATE, t.due_date) AS days_overdue,
    CASE 
        WHEN CURRENT_DATE > t.due_date THEN 'OVERDUE'
        ELSE 'Active'
    END AS loan_status
FROM transactions t
JOIN books b ON t.book_id = b.book_id
JOIN members m ON t.member_id = m.member_id
WHERE t.status = 'Issued'
ORDER BY t.due_date;

-- View: Book Availability Summary
CREATE OR REPLACE VIEW book_availability AS
SELECT 
    book_id,
    isbn,
    title,
    author,
    total_quantity,
    available_quantity,
    (total_quantity - available_quantity) AS currently_issued,
    CASE 
        WHEN available_quantity > 0 THEN 'Available'
        ELSE 'All Copies Issued'
    END AS availability_status
FROM books
ORDER BY title;

-- View: Member Borrowing History
CREATE OR REPLACE VIEW member_history AS
SELECT 
    m.member_id,
    m.name,
    m.email,
    COUNT(t.transaction_id) AS total_borrows,
    SUM(CASE WHEN t.status = 'Issued' THEN 1 ELSE 0 END) AS current_loans,
    SUM(CASE WHEN t.status = 'Returned' THEN 1 ELSE 0 END) AS returned
FROM members m
LEFT JOIN transactions t ON m.member_id = t.member_id
GROUP BY m.member_id, m.name, m.email
ORDER BY m.name;

-- =====================================================
-- STEP 10: USEFUL STORED PROCEDURES (OPTIONAL)
-- =====================================================

DELIMITER //

-- Procedure: Issue a Book
CREATE PROCEDURE IF NOT EXISTS issue_book(
    IN p_book_id INT,
    IN p_member_id INT,
    IN p_due_days INT
)
BEGIN
    DECLARE v_available INT;
    
    -- Check availability
    SELECT available_quantity INTO v_available 
    FROM books WHERE book_id = p_book_id;
    
    IF v_available > 0 THEN
        -- Start transaction
        START TRANSACTION;
        
        -- Decrease availability
        UPDATE books 
        SET available_quantity = available_quantity - 1 
        WHERE book_id = p_book_id;
        
        -- Create transaction record
        INSERT INTO transactions (book_id, member_id, issue_date, due_date, status)
        VALUES (p_book_id, p_member_id, CURRENT_DATE, DATE_ADD(CURRENT_DATE, INTERVAL p_due_days DAY), 'Issued');
        
        COMMIT;
        SELECT 'Book issued successfully' AS result;
    ELSE
        SELECT 'Book not available' AS result;
    END IF;
END //

-- Procedure: Return a Book
CREATE PROCEDURE IF NOT EXISTS return_book(
    IN p_transaction_id INT
)
BEGIN
    DECLARE v_book_id INT;
    DECLARE v_status VARCHAR(20);
    
    -- Get transaction details
    SELECT book_id, status INTO v_book_id, v_status
    FROM transactions WHERE transaction_id = p_transaction_id;
    
    IF v_status = 'Issued' THEN
        START TRANSACTION;
        
        -- Update transaction
        UPDATE transactions 
        SET return_date = CURRENT_DATE, status = 'Returned'
        WHERE transaction_id = p_transaction_id;
        
        -- Increase availability
        UPDATE books 
        SET available_quantity = available_quantity + 1
        WHERE book_id = v_book_id;
        
        COMMIT;
        SELECT 'Book returned successfully' AS result;
    ELSE
        SELECT 'Transaction not found or already returned' AS result;
    END IF;
END //

DELIMITER ;

-- =====================================================
-- STEP 11: VERIFICATION QUERIES
-- =====================================================

-- Check all tables exist
SELECT 'Checking tables...' AS Status;
SHOW TABLES;

-- Check books count
SELECT 'Books in database:' AS Info, COUNT(*) AS Count FROM books;

-- Check members count
SELECT 'Members in database:' AS Info, COUNT(*) AS Count FROM members;

-- Check transactions count
SELECT 'Transactions in database:' AS Info, COUNT(*) AS Count FROM transactions;

-- Show sample data
SELECT '--- SAMPLE BOOKS ---' AS Info;
SELECT book_id, isbn, title, author, available_quantity, total_quantity 
FROM books LIMIT 5;

SELECT '--- SAMPLE MEMBERS ---' AS Info;
SELECT member_id, name, email, phone, status 
FROM members LIMIT 5;

SELECT '--- ACTIVE LOANS ---' AS Info;
SELECT * FROM active_loans;

SELECT '--- OVERDUE BOOKS ---' AS Info;
SELECT * FROM active_loans WHERE loan_status = 'OVERDUE';

-- =====================================================
-- DATABASE SETUP COMPLETE!
-- =====================================================
-- 
-- Summary:
-- ✓ Created database: library_db
-- ✓ Created table: books (15 sample books)
-- ✓ Created table: members (10 sample members)
-- ✓ Created table: transactions (6 sample transactions)
-- ✓ Created views: active_loans, book_availability, member_history
-- ✓ Created procedures: issue_book, return_book
--
-- To use:
-- 1. mariadb -u root -p < library_schema.sql
-- 2. Run your Java application
--
-- =====================================================

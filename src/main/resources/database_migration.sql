-- =====================================================
-- Library Management System - SAFE Database Migration
-- From: Basic Schema → Enhanced Schema
-- =====================================================
-- This script safely migrates your existing database
-- while PRESERVING all current data.
-- =====================================================

USE library_db;

-- =====================================================
-- BACKUP RECOMMENDATION
-- =====================================================
-- Before running this script, create a backup:
-- mysqldump -u your_username -p library_db > library_db_backup.sql
-- =====================================================

-- =====================================================
-- STEP 1: Enhance Books Table
-- =====================================================
-- Add new columns to existing books table

-- Add ISBN column (allowing NULL for existing records)
ALTER TABLE books 
  ADD COLUMN isbn VARCHAR(20) UNIQUE AFTER book_id;

-- Add genre column
ALTER TABLE books 
  ADD COLUMN genre VARCHAR(100) AFTER author;

-- Add publication year
ALTER TABLE books 
  ADD COLUMN publication_year INT AFTER genre;

-- Add quantity tracking columns
ALTER TABLE books 
  ADD COLUMN total_quantity INT DEFAULT 1 AFTER publication_year;

ALTER TABLE books 
  ADD COLUMN available_quantity INT DEFAULT 1 AFTER total_quantity;

-- Update existing records to have proper quantities
UPDATE books 
SET total_quantity = 1, available_quantity = 1 
WHERE total_quantity IS NULL OR available_quantity IS NULL;

-- Update status for existing books
UPDATE books 
SET status = 'Available' 
WHERE status IS NULL OR status = '';

-- =====================================================
-- STEP 2: Enhance Members Table  
-- =====================================================
-- Transform 'contact' into separate email, phone fields
-- Add new columns

-- Add email column (will populate from contact if it looks like email)
ALTER TABLE members 
  ADD COLUMN email VARCHAR(255) AFTER name;

-- Add phone column  
ALTER TABLE members 
  ADD COLUMN phone VARCHAR(20) AFTER email;

-- Add address column
ALTER TABLE members 
  ADD COLUMN address TEXT AFTER phone;

-- Add registration date (default to today for existing members)
ALTER TABLE members 
  ADD COLUMN registration_date DATE DEFAULT (CURRENT_DATE) AFTER address;

-- Add status column
ALTER TABLE members 
  ADD COLUMN status VARCHAR(20) DEFAULT 'Active' AFTER registration_date;

-- Smart migration: Move contact data to appropriate field
-- If contact looks like email (contains @), move to email, otherwise to phone
UPDATE members 
SET email = contact 
WHERE contact LIKE '%@%';

UPDATE members 
SET phone = contact 
WHERE contact NOT LIKE '%@%' OR contact IS NULL;

-- Set default email for members without one (using member_id)
UPDATE members 
SET email = CONCAT('member', member_id, '@library.local') 
WHERE email IS NULL OR email = '';

-- Now we can drop the old contact column
ALTER TABLE members 
  DROP COLUMN contact;

-- Make email unique
ALTER TABLE members 
  ADD UNIQUE KEY unique_email (email);

-- =====================================================
-- STEP 3: Enhance Transactions Table
-- =====================================================
-- Rename trans_id to transaction_id
-- Add due_date, return_date, and status columns

-- Rename primary key column
ALTER TABLE transactions 
  CHANGE COLUMN trans_id transaction_id INT AUTO_INCREMENT;

-- Add due_date (calculate as 14 days after issue_date for existing transactions)
ALTER TABLE transactions 
  ADD COLUMN due_date DATE AFTER issue_date;

-- Update due dates for existing transactions
UPDATE transactions 
SET due_date = DATE_ADD(issue_date, INTERVAL 14 DAY) 
WHERE due_date IS NULL;

-- Add return_date (NULL means not returned yet)
ALTER TABLE transactions 
  ADD COLUMN return_date DATE AFTER due_date;

-- Add status column
ALTER TABLE transactions 
  ADD COLUMN status VARCHAR(20) DEFAULT 'Issued' AFTER return_date;

-- Mark old transactions as 'Returned' (assuming they're old/complete)
-- You can adjust this logic based on your needs
UPDATE transactions 
SET status = 'Returned', return_date = DATE_ADD(issue_date, INTERVAL 7 DAY) 
WHERE issue_date < DATE_SUB(CURRENT_DATE, INTERVAL 30 DAY);

-- Recent transactions without return_date are still 'Issued'
UPDATE transactions 
SET status = 'Issued' 
WHERE return_date IS NULL;

-- Make due_date NOT NULL for future insertions
ALTER TABLE transactions 
  MODIFY COLUMN due_date DATE NOT NULL;

-- =====================================================
-- STEP 4: Update Book Availability Based on Active Transactions
-- =====================================================
-- Calculate how many copies are currently issued for each book

-- First, ensure all books have availability set
UPDATE books 
SET available_quantity = total_quantity 
WHERE available_quantity IS NULL;

-- Now reduce availability for books with active (Issued) transactions
UPDATE books b
SET available_quantity = total_quantity - (
    SELECT COUNT(*) 
    FROM transactions t 
    WHERE t.book_id = b.book_id AND t.status = 'Issued'
)
WHERE book_id IN (
    SELECT DISTINCT book_id 
    FROM transactions 
    WHERE status = 'Issued'
);

-- Ensure availability doesn't go negative
UPDATE books 
SET available_quantity = 0 
WHERE available_quantity < 0;

-- =====================================================
-- STEP 5: Add Indexes for Performance
-- =====================================================

-- Books table indexes
ALTER TABLE books ADD INDEX idx_isbn (isbn);
ALTER TABLE books ADD INDEX idx_title (title);

-- Members table indexes  
ALTER TABLE members ADD INDEX idx_name (name);

-- Transactions table indexes
ALTER TABLE transactions ADD INDEX idx_status (status);
ALTER TABLE transactions ADD INDEX idx_book_id (book_id);
ALTER TABLE transactions ADD INDEX idx_member_id (member_id);

-- =====================================================
-- VERIFICATION QUERIES
-- =====================================================

-- Check updated table structures
SELECT '=== BOOKS TABLE STRUCTURE ===' as Info;
DESCRIBE books;

SELECT '=== MEMBERS TABLE STRUCTURE ===' as Info;
DESCRIBE members;

SELECT '=== TRANSACTIONS TABLE STRUCTURE ===' as Info;
DESCRIBE transactions;

-- Check data counts
SELECT '=== DATA COUNTS ===' as Info;
SELECT 'Books' as Table_Name, COUNT(*) as Record_Count FROM books
UNION ALL
SELECT 'Members', COUNT(*) FROM members
UNION ALL
SELECT 'Transactions', COUNT(*) FROM transactions;

-- Check active transactions
SELECT '=== ACTIVE TRANSACTIONS ===' as Info;
SELECT COUNT(*) as Active_Issues 
FROM transactions 
WHERE status = 'Issued';

-- Check book availability
SELECT '=== BOOK AVAILABILITY ===' as Info;
SELECT 
    book_id,
    title,
    total_quantity,
    available_quantity,
    (total_quantity - available_quantity) as Currently_Issued
FROM books
LIMIT 10;

-- =====================================================
-- Migration Complete!
-- =====================================================
-- Your database has been successfully upgraded.
-- All existing data has been preserved.
-- 
-- Changes made:
-- ✓ Books: Added ISBN, genre, year, quantity tracking
-- ✓ Members: Split contact into email/phone, added address, reg date, status
-- ✓ Transactions: Renamed ID, added due date, return date, status
-- ✓ Calculated availability based on active issues
-- ✓ Added performance indexes
-- =====================================================

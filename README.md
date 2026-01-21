# 📚 Library Management System (LMS)

A comprehensive desktop library management application built with **JavaFX** and **MariaDB**, featuring complete book inventory management, member registration, and a smart book issue/return system with automated availability tracking.

---

## 🎯 Overview

This LMS provides librarians with a modern, user-friendly interface to manage all library operations:

- **Book Management**: Add, edit, delete, and search books with ISBN, genre, and quantity tracking
- **Member Management**: Register and manage library members with contact details
- **Issue/Return System**: Check out books with automatic due date calculation and overdue tracking
- **Real-Time Search**: Instant filtering across books and members
- **Data Validation**: Prevents duplicate ISBNs/emails and ensures referential integrity

---

## 🛠️ Tech Stack

| Component | Technology |
|-----------|------------|
| **Language** | Java 21 |
| **UI Framework** | JavaFX 21 |
| **Database** | MariaDB / MySQL |
| **Build Tool** | Gradle 8.13 |
| **Architecture** | MVC with DAO Pattern |
| **JDBC Driver** | MariaDB Connector/J 3.5.7 |

---

## 📁 Project Structure

```
lms-Java/
├── src/main/
│   ├── java/com/example/lms/
│   │   ├── models/              # Data models (POJOs)
│   │   │   ├── Book.java        # Book entity
│   │   │   ├── Member.java      # Member entity
│   │   │   └── Transaction.java # Transaction entity
│   │   │
│   │   ├── dao/                 # Data Access Objects
│   │   │   ├── BookDAO.java     # Book CRUD operations
│   │   │   ├── MemberDAO.java   # Member CRUD operations
│   │   │   └── TransactionDAO.java # Transaction operations
│   │   │
│   │   ├── util/                # Utilities
│   │   │   └── DatabaseUtil.java # Database connection handler
│   │   │
│   │   ├── LibraryApp.java      # Main application (UI + Controllers)
│   │   └── Launcher.java        # Application entry point
│   │
│   └── resources/
│       ├── db.properties        # Database configuration (gitignored)
│       ├── database_migration.sql # Database schema migration
│       └── dashboard.css        # UI styling
│
├── build.gradle.kts             # Gradle build configuration
└── README.md                    # This file
```

---

## 🗄️ Database Schema

### Tables Overview

#### `books`
Stores library book inventory with quantity tracking.

| Column | Type | Description |
|--------|------|-------------|
| `book_id` | INT (PK) | Auto-increment primary key |
| `isbn` | VARCHAR(20) | Unique book identifier |
| `title` | VARCHAR(100) | Book title |
| `author` | VARCHAR(100) | Author name |
| `genre` | VARCHAR(100) | Book category/genre |
| `publication_year` | INT | Year published |
| `total_quantity` | INT | Total copies owned |
| `available_quantity` | INT | Copies currently available |
| `status` | VARCHAR(20) | Book status (Available/etc) |

#### `members`
Stores registered library member information.

| Column | Type | Description |
|--------|------|-------------|
| `member_id` | INT (PK) | Auto-increment primary key |
| `name` | VARCHAR(255) | Member full name |
| `email` | VARCHAR(255) | Email (unique) |
| `phone` | VARCHAR(20) | Phone number |
| `address` | TEXT | Physical address |
| `registration_date` | DATE | Registration date |
| `status` | VARCHAR(20) | Active/Inactive status |

#### `transactions`
Tracks book issue and return operations.

| Column | Type | Description |
|--------|------|-------------|
| `transaction_id` | INT (PK) | Auto-increment primary key |
| `book_id` | INT (FK) | References books.book_id |
| `member_id` | INT (FK) | References members.member_id |
| `issue_date` | DATE | Date book was issued |
| `due_date` | DATE | Date book should be returned |
| `return_date` | DATE | Actual return date (NULL if not returned) |
| `status` | VARCHAR(20) | Issued/Returned |

**Foreign Keys:**
- `transactions.book_id` → `books.book_id` (ON DELETE RESTRICT)
- `transactions.member_id` → `members.member_id` (ON DELETE RESTRICT)

---

## 🚀 Getting Started

### Prerequisites

1. **Java 21** - [Download](https://www.oracle.com/java/technologies/downloads/#java21)
2. **MariaDB/MySQL** - [Download](https://mariadb.org/download/)
3. **Git** (optional) - For version control

### Initial Setup

#### 1. Clone the Repository
```bash
git clone https://github.com/Nancy100-coder/lms-Java.git
cd lms-Java
```

#### 2. Set Up Database

**Create database configuration:**
```bash
# Create db.properties file
cat > src/main/resources/db.properties << EOF
db.url=jdbc:mariadb://localhost:3306/library_db
db.user=your_username
db.password=your_password
EOF
```

**Run database migration:**
```bash
# If starting fresh:
mariadb -u root/username -p < src/main/resources/database_migration.sql

# Or use an existing database (see migration guide)
```

#### 3. Build the Project
```bash
./gradlew build
```

#### 4. Run the Application
```bash
./gradlew run
```

---

## 👨‍💻 Development Guide

### Architecture Pattern

The application follows **MVC architecture** with **DAO pattern**:

```
┌─────────────┐
│   View      │ ← JavaFX UI (LibraryApp.java)
│  (UI Layer) │
└──────┬──────┘
       │
┌──────▼──────┐
│ Controller  │ ← Event handlers in LibraryApp
│   (Logic)   │
└──────┬──────┘
       │
┌──────▼──────┐
│    DAO      │ ← BookDAO, MemberDAO, TransactionDAO
│ (Data Layer)│
└──────┬──────┘
       │
┌──────▼──────┐
│  Database   │ ← MariaDB
│   (MySQL)   │
└─────────────┘
```

### Code Organization Principles

1. **Models** (`models/`) - Pure data classes (POJOs)
   - No business logic
   - Only getters/setters and constructors
   - Represent database entities

2. **DAOs** (`dao/`) - Database operations only
   - All SQL queries here
   - Return model objects
   - Handle database connections
   - No UI logic

3. **UI/Controllers** (`LibraryApp.java`) - User interface
   - JavaFX components
   - Event handlers
   - Calls DAO methods
   - Updates UI based on data

4. **Utilities** (`util/`) - Shared helpers
   - Database connection management
   - Configuration loading

---

## 🔧 Adding New Features

### Example: Adding a New Entity (e.g., Publishers)

#### Step 1: Create Database Table
```sql
CREATE TABLE publishers (
    publisher_id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    country VARCHAR(100),
    website VARCHAR(255)
);

-- Add foreign key to books table
ALTER TABLE books ADD COLUMN publisher_id INT;
ALTER TABLE books ADD FOREIGN KEY (publisher_id) REFERENCES publishers(publisher_id);
```

#### Step 2: Create Model Class
Create `src/main/java/com/example/lms/models/Publisher.java`:
```java
package com.example.lms.models;

public class Publisher {
    private int publisherId;
    private String name;
    private String country;
    private String website;
    
    // Constructors
    public Publisher(int publisherId, String name, String country, String website) {
        this.publisherId = publisherId;
        this.name = name;
        this.country = country;
        this.website = website;
    }
    
    // Getters and setters
    public int getPublisherId() { return publisherId; }
    public void setPublisherId(int publisherId) { this.publisherId = publisherId; }
    // ... etc
}
```

#### Step 3: Create DAO Class
Create `src/main/java/com/example/lms/dao/PublisherDAO.java`:
```java
package com.example.lms.dao;

import com.example.lms.models.Publisher;
import com.example.lms.util.DatabaseUtil;
import java.sql.*;
import java.util.*;

public class PublisherDAO {
    public List<Publisher> getAllPublishers() {
        List<Publisher> publishers = new ArrayList<>();
        String sql = "SELECT * FROM publishers";
        try (Connection conn = DatabaseUtil.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                publishers.add(new Publisher(
                    rs.getInt("publisher_id"),
                    rs.getString("name"),
                    rs.getString("country"),
                    rs.getString("website")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return publishers;
    }
    
    public boolean addPublisher(Publisher publisher) {
        String sql = "INSERT INTO publishers (name, country, website) VALUES (?, ?, ?)";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, publisher.getName());
            pstmt.setString(2, publisher.getCountry());
            pstmt.setString(3, publisher.getWebsite());
            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    // Add updatePublisher(), deletePublisher(), etc.
}
```

#### Step 4: Add UI Tab
In `LibraryApp.java`, add a new tab:
```java
Tab publishersTab = new Tab("Publishers", createPublishersTab());
publishersTab.setClosable(false);
tabPane.getTabs().add(publishersTab);

private VBox createPublishersTab() {
    // Similar structure to createBooksTab()
    // Add TableView, search field, buttons, etc.
}
```

#### Step 5: Update Book Model & DAO
Add publisher relationship to Book:
```java
// In Book.java
private int publisherId;
public int getPublisherId() { return publisherId; }
public void setPublisherId(int publisherId) { this.publisherId = publisherId; }

// In BookDAO.java - update SQL queries
String sql = "INSERT INTO books (..., publisher_id) VALUES (..., ?)";
```

---

## 🧪 Testing Guidelines

### Manual Testing Checklist

**Books Tab:**
- [ ] Add book with all fields filled
- [ ] Add book with duplicate ISBN (should fail)
- [ ] Edit book details
- [ ] Delete book with no active transactions
- [ ] Try deleting book with active transaction (should fail)
- [ ] Search books by title, author, ISBN

**Members Tab:**
- [ ] Register new member
- [ ] Register member with duplicate email (should fail)
- [ ] Edit member details
- [ ] Toggle member status (Active ↔ Inactive)
- [ ] Search members by name, email, phone

**Transactions Tab:**
- [ ] Issue a book to a member
- [ ] Try issuing unavailable book (should fail)
- [ ] Return a book
- [ ] Verify overdue highlighting (red background)
- [ ] Check due date calculation (should be +14 days)

### Database Integrity Tests

```sql
-- Verify foreign key constraints work
DELETE FROM books WHERE book_id = 1; -- Should fail if has transactions

-- Verify unique constraints
INSERT INTO books (isbn, title, author) VALUES ('978-0-7432-7356-5', 'Test', 'Test'); 
-- Should fail if ISBN exists

-- Check availability tracking
SELECT book_id, total_quantity, available_quantity,
       (total_quantity - available_quantity) as issued
FROM books;
```

---

## 🔐 Configuration

### Database Configuration (`db.properties`)

```properties
db.url=jdbc:mariadb://localhost:3306/library_db
db.user=library_admin
db.password=secure_password_here
```

> **Security Note:** The `db.properties` file is gitignored. Each developer must create their own copy with local database credentials.

### JavaFX Configuration (`build.gradle.kts`)

```kotlin
javafx {
    version = "21.0.6"
    modules = listOf("javafx.controls", "javafx.fxml")
}
```

To add more JavaFX modules (e.g., for charts):
```kotlin
modules = listOf("javafx.controls", "javafx.fxml", "javafx.charts")
```

---

## 📊 Future Enhancement Ideas

### Priority 1 - High Impact
- [ ] **User Authentication**: Add login system with user roles (Admin, Librarian, Member)
- [ ] **Fine Calculation**: Auto-calculate late fees for overdue books
- [ ] **Email Notifications**: Send reminders for due/overdue books
- [ ] **Barcode Scanning**: Integrate barcode reader for ISBN entry
- [ ] **Export Reports**: Generate PDF/Excel reports for transactions

### Priority 2 - Medium Impact
- [ ] **Book Reservations**: Allow members to reserve checked-out books
- [ ] **Reading History**: Track member borrowing patterns
- [ ] **Book Reviews/Ratings**: Let members rate and review books
- [ ] **Multi-branch Support**: Manage multiple library branches
- [ ] **Advanced Search**: Filter by genre, year, availability

### Priority 3 - Nice to Have
- [ ] **Dashboard Analytics**: Charts showing popular books, active members
- [ ] **Book Recommendation**: Suggest books based on borrowing history
- [ ] **Integration with ISBN API**: Auto-fetch book details from ISBN
- [ ] **Mobile App**: Companion app for members to view their loans
- [ ] **RFID Support**: RFID tags for faster book check-in/out

---

---

## 📝 Frequently Asked Questions (FAQ)

### Q: Why can't I delete books that were previously issued?

**A:** Books with transaction history (even returned books) are preserved for **audit trail and reporting purposes**. This is intentional and follows library management best practices.

**What to do instead:**
- Use **Edit** to update book details
- Set `total_quantity = 0` to remove from active circulation
- Books with 0 available quantity won't appear in issue dropdown

### Q: How do I change the due date period from 14 days?

**A:** Edit `TransactionDAO.java`:
```java
private static final int DUE_DAYS = 21;  // Change to your desired days
```

### Q: Can members have multiple active book loans?

**A:** Yes! There's no limit by default. To add a limit, modify `TransactionDAO.issueBook()` to check active transaction count per member.

### Q: How do I handle lost or damaged books?

**A:** Current options:
1. Don't return the book (keeps it as overdue)
2. Return it and manually adjust quantity in Edit Book
3. **Future enhancement**: Add "Lost" or "Damaged" transaction status

### Q: Why do overdue books show in red?

**A:** Visual highlighting helps librarians quickly identify which transactions need follow-up. The table row factory in `LibraryApp.java` applies red background to overdue items.

---

## ⚠️ Known Limitations & Design Decisions

### Data Deletion Policy
- **Books**: Cannot be deleted if they have ANY transaction history (for audit purposes)
- **Members**: Cannot be deleted if they have ANY transaction history
- **Transactions**: Cannot be deleted (permanent audit trail)

**Rationale**: Preserves historical data for reporting, analytics, and compliance.

### Concurrency
- **Not multi-user safe**: Designed for single-user/single-instance deployment
- **No locking mechanism**: Two users could issue the same last copy simultaneously
- **Future enhancement**: Add database-level locking or optimistic concurrency control

### Validation
- **ISBN**: Uniqueness enforced, but no format validation (accepts any string)
- **Email**: Uniqueness enforced, but minimal format validation
- **Phone**: No validation (accepts any format)

### Search
- **Case-insensitive**: Works, but may have performance issues with large datasets (no indexes on search fields beyond primary keys)
- **Partial matching**: Uses SQL `LIKE` - not optimal for very large datasets

### Reports
- **No built-in reports**: Currently view-only tables
- **No export**: Can't export to PDF/Excel (future enhancement)

---

## 💼 Business Rules

### Book Availability
- Automatically decreases when book is issued
- Automatically increases when book is returned
- Books with `available_quantity = 0` cannot be issued

### Transaction Lifecycle
```
[Issue Book] → Status: "Issued"
    ↓
[14 days pass]
    ↓
[If not returned] → Becomes "Overdue" (visual only, status stays "Issued")
    ↓
[Return Book] → Status: "Returned", return_date set
```

### Member Status
- **Active**: Can issue books
- **Inactive**: Field exists but not currently enforced in issue logic
- **Future**: Could prevent inactive members from borrowing

---

## 🐛 Common Issues & Solutions

### Issue: Database Connection Fails

**Error:** `SQLException: Access denied for user`

**Solution:**
1. Verify credentials in `db.properties`
2. Ensure MariaDB is running: `sudo systemctl status mariadb`
3. Test connection: `mariadb -u root/username -p`

---

### Issue: JavaFX Module Not Found

**Error:** `Error: JavaFX runtime components are missing`

**Solution:**
- Ensure Java 21 is installed
- Rebuild project: `./gradlew clean build`
- Check `build.gradle.kts` has correct JavaFX version

---

### Issue: Duplicate Key Error on Insert

**Error:** `Duplicate entry 'XXX' for key 'isbn'`

**Solution:**
- This is expected behavior (prevents duplicate ISBNs)
- Check if book already exists before adding
- Use search to find existing book

---

## 🤝 Contributing

### Branch Strategy

```
main          ← Production-ready code
├── develop   ← Integration branch
    ├── feature/book-reservation
    ├── feature/email-notifications
    └── bugfix/search-performance
```

### Commit Message Convention

```
feat: Add book reservation system
fix: Correct due date calculation
docs: Update README with testing guide
refactor: Simplify BookDAO query methods
style: Format code according to style guide
```

### Code Review Checklist

- [ ] Code follows existing architecture (MVC + DAO)
- [ ] No hardcoded credentials or sensitive data
- [ ] Database queries use PreparedStatement (prevent SQL injection)
- [ ] UI changes tested on different screen sizes
- [ ] No compilation warnings
- [ ] Comments added for complex logic
- [ ] README updated if public API changes

---

## 📞 Support & Contact

**Project Maintainer:** Nancy Gathua  
**Email:** nancygathua28@gmail.com]  
**Issue Tracker:** [GitHub Issues](https://github.com/Nancy100-coder/lms-Java/issues)

---

## 🙏 Acknowledgments

- JavaFX community for excellent UI framework
- MariaDB team for reliable database engine
- Contributors and testers

---

## 📚 Additional Resources

### Documentation
- [JavaFX Documentation](https://openjfx.io/)
- [MariaDB Connector/J](https://mariadb.com/kb/en/about-mariadb-connector-j/)
- [Gradle User Guide](https://docs.gradle.org/)

### Learning Resources
- [JavaFX Tutorial](https://docs.oracle.com/javafx/2/)
- [JDBC Best Practices](https://www.baeldung.com/jdbc)
- [DAO Pattern Explained](https://www.baeldung.com/java-dao-pattern)

---

**Happy Coding! 🚀**
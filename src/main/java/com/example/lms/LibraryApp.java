package com.example.lms;

import com.example.lms.dao.BookDAO;
import com.example.lms.dao.MemberDAO;
import com.example.lms.dao.TransactionDAO;
import com.example.lms.models.Book;
import com.example.lms.models.Member;
import com.example.lms.models.Transaction;

import javafx.application.Application;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

public class LibraryApp extends Application {

    // DAOs
    private final BookDAO bookDAO = new BookDAO();
    private final MemberDAO memberDAO = new MemberDAO();
    private final TransactionDAO transactionDAO = new TransactionDAO();

    // Observable Lists
    private ObservableList<Book> bookList = FXCollections.observableArrayList();
    private ObservableList<Member> memberList = FXCollections.observableArrayList();
    private ObservableList<Transaction> transactionList = FXCollections.observableArrayList();

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("📚 Library Management System");

        TabPane tabPane = new TabPane();
        tabPane.setStyle("-fx-background-color: #FAFAFA;");
        
        // Create tabs with icons
        Tab booksTab = new Tab("📚 Books", createBooksTab());
        Tab membersTab = new Tab("👥 Members", createMembersTab());
        Tab transactionsTab = new Tab("📖 Transactions", createTransactionsTab());
        
        booksTab.setClosable(false);
        membersTab.setClosable(false);
        transactionsTab.setClosable(false);
        
        tabPane.getTabs().addAll(booksTab, membersTab, transactionsTab);

        Scene scene = new Scene(tabPane, 1200, 750);
        scene.getStylesheets().add(getClass().getResource("/dashboard.css").toExternalForm());
        scene.setFill(Color.web("#FAFAFA"));
        primaryStage.setScene(scene);
        primaryStage.show();

        // Load initial data
        loadBooks();
        loadMembers();
        loadTransactions();
    }

    // ========================================
    // BOOKS TAB
    // ========================================
    
    private VBox createBooksTab() {
        VBox container = new VBox(18);
        container.setPadding(new Insets(20));
        container.setStyle("-fx-background-color: #FAFAFA;");

        // Title with modern styling
        Label titleLabel = new Label("📚 Book Management");
        titleLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: #212121;");

        // Search field with modern styling
        TextField searchField = new TextField();
        searchField.setPromptText("🔍 Search by title, author, or ISBN...");
        searchField.setPrefWidth(400);
        searchField.setStyle("-fx-font-size: 14px; -fx-pref-height: 40px;");
        searchField.textProperty().addListener((obs, old, newVal) -> {
            if (newVal.isEmpty()) {
                loadBooks();
            } else {
                bookList.setAll(bookDAO.searchBooks(newVal));
            }
        });

        // Table
        TableView<Book> table = new TableView<>();
        table.setItems(bookList);

        TableColumn<Book, Integer> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(data -> new SimpleIntegerProperty(data.getValue().getId()).asObject());
        idCol.setPrefWidth(50);

        TableColumn<Book, String> isbnCol = new TableColumn<>("ISBN");
        isbnCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getIsbn()));
        isbnCol.setPrefWidth(130);

        TableColumn<Book, String> titleCol = new TableColumn<>("Title");
        titleCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getTitle()));
        titleCol.setPrefWidth(200);

        TableColumn<Book, String> authorCol = new TableColumn<>("Author");
        authorCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getAuthor()));
        authorCol.setPrefWidth(150);

        TableColumn<Book, String> genreCol = new TableColumn<>("Genre");
        genreCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getGenre()));
        genreCol.setPrefWidth(120);

        TableColumn<Book, Integer> yearCol = new TableColumn<>("Year");
        yearCol.setCellValueFactory(data -> {
            Integer year = data.getValue().getPublicationYear();
            return new SimpleIntegerProperty(year != null ? year : 0).asObject();
        });
        yearCol.setPrefWidth(70);

        TableColumn<Book, Integer> totalQtyCol = new TableColumn<>("Total Qty");
        totalQtyCol.setCellValueFactory(data -> new SimpleIntegerProperty(data.getValue().getTotalQuantity()).asObject());
        totalQtyCol.setPrefWidth(80);

        TableColumn<Book, Integer> availQtyCol = new TableColumn<>("Available");
        availQtyCol.setCellValueFactory(data -> new SimpleIntegerProperty(data.getValue().getAvailableQuantity()).asObject());
        availQtyCol.setPrefWidth(80);

        table.getColumns().addAll(idCol, isbnCol, titleCol, authorCol, genreCol, yearCol, totalQtyCol, availQtyCol);

        // Buttons with modern styling
        Button addBtn = createStyledButton("➕ Add Book", "#4CAF50");
        Button editBtn = createStyledButton("✏️ Edit Selected", "#FF9800");
        Button deleteBtn = createStyledButton("🗑️ Delete Selected", "#F44336");
        Button refreshBtn = createStyledButton("🔄 Refresh", "#2196F3");

        addBtn.setOnAction(e -> showAddBookDialog());
        editBtn.setOnAction(e -> {
            Book selected = table.getSelectionModel().getSelectedItem();
            if (selected != null) showEditBookDialog(selected);
            else showAlert("No Selection", "Please select a book to edit.");
        });
        deleteBtn.setOnAction(e -> {
            Book selected = table.getSelectionModel().getSelectedItem();
            if (selected != null) deleteBook(selected);
            else showAlert("No Selection", "Please select a book to delete.");
        });
        refreshBtn.setOnAction(e -> loadBooks());

        HBox buttonBox = new HBox(12, addBtn, editBtn, deleteBtn, refreshBtn);
        buttonBox.setAlignment(Pos.CENTER_LEFT);
        buttonBox.setPadding(new Insets(10, 0, 0, 0));

        HBox searchBox = new HBox(15, searchField);
        searchBox.setAlignment(Pos.CENTER_LEFT);
        searchBox.setStyle("-fx-background-color: white; -fx-background-radius: 8px; -fx-padding: 15px; " +
                          "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 8, 0, 0, 2);");

        container.getChildren().addAll(titleLabel, searchBox, table, buttonBox);
        VBox.setVgrow(table, Priority.ALWAYS);

        return container;
    }

    private void showAddBookDialog() {
        Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.setTitle("Add New Book");

        GridPane grid = new GridPane();
        grid.setPadding(new Insets(20));
        grid.setHgap(10);
        grid.setVgap(10);

        TextField isbnField = new TextField();
        TextField titleField = new TextField();
        TextField authorField = new TextField();
        TextField genreField = new TextField();
        TextField yearField = new TextField();
        TextField qtyField = new TextField();
        qtyField.setText("1");

        grid.add(new Label("ISBN:"), 0, 0);
        grid.add(isbnField, 1, 0);
        grid.add(new Label("Title:"), 0, 1);
        grid.add(titleField, 1, 1);
        grid.add(new Label("Author:"), 0, 2);
        grid.add(authorField, 1, 2);
        grid.add(new Label("Genre:"), 0, 3);
        grid.add(genreField, 1, 3);
        grid.add(new Label("Year:"), 0, 4);
        grid.add(yearField, 1, 4);
        grid.add(new Label("Quantity:"), 0, 5);
        grid.add(qtyField, 1, 5);

        Button saveBtn = new Button("Save");
        Button cancelBtn = new Button("Cancel");

        saveBtn.setOnAction(e -> {
            try {
                Integer year = yearField.getText().isEmpty() ? null : Integer.parseInt(yearField.getText());
                int qty = Integer.parseInt(qtyField.getText());
                
                Book book = new Book(isbnField.getText(), titleField.getText(), 
                                     authorField.getText(), genreField.getText(), year, qty);
                if (bookDAO.addBook(book)) {
                    showAlert("Success", "Book added successfully!");
                    loadBooks();
                    dialog.close();
                } else {
                    showAlert("Error", "Failed to add book. ISBN might already exist.");
                }
            } catch (NumberFormatException ex) {
                showAlert("Invalid Input", "Please enter valid numbers for Year and Quantity.");
            }
        });

        cancelBtn.setOnAction(e -> dialog.close());

        HBox buttonBox = new HBox(10, saveBtn, cancelBtn);
        buttonBox.setAlignment(Pos.CENTER);
        grid.add(buttonBox, 0, 6, 2, 1);

        dialog.setScene(new Scene(grid, 400, 300));
        dialog.showAndWait();
    }

    private void showEditBookDialog(Book book) {
        Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.setTitle("Edit Book");

        GridPane grid = new GridPane();
        grid.setPadding(new Insets(20));
        grid.setHgap(10);
        grid.setVgap(10);

        TextField isbnField = new TextField(book.getIsbn());
        TextField titleField = new TextField(book.getTitle());
        TextField authorField = new TextField(book.getAuthor());
        TextField genreField = new TextField(book.getGenre());
        TextField yearField = new TextField(book.getPublicationYear() != null ? book.getPublicationYear().toString() : "");
        TextField qtyField = new TextField(String.valueOf(book.getTotalQuantity()));

        grid.add(new Label("ISBN:"), 0, 0);
        grid.add(isbnField, 1, 0);
        grid.add(new Label("Title:"), 0, 1);
        grid.add(titleField, 1, 1);
        grid.add(new Label("Author:"), 0, 2);
        grid.add(authorField, 1, 2);
        grid.add(new Label("Genre:"), 0, 3);
        grid.add(genreField, 1, 3);
        grid.add(new Label("Year:"), 0, 4);
        grid.add(yearField, 1, 4);
        grid.add(new Label("Total Quantity:"), 0, 5);
        grid.add(qtyField, 1, 5);

        Button saveBtn = new Button("Save Changes");
        Button cancelBtn = new Button("Cancel");

        saveBtn.setOnAction(e -> {
            try {
                book.setIsbn(isbnField.getText());
                book.setTitle(titleField.getText());
                book.setAuthor(authorField.getText());
                book.setGenre(genreField.getText());
                book.setPublicationYear(yearField.getText().isEmpty() ? null : Integer.parseInt(yearField.getText()));
                book.setTotalQuantity(Integer.parseInt(qtyField.getText()));
                
                if (bookDAO.updateBook(book)) {
                    showAlert("Success", "Book updated successfully!");
                    loadBooks();
                    dialog.close();
                } else {
                    showAlert("Error", "Failed to update book.");
                }
            } catch (NumberFormatException ex) {
                showAlert("Invalid Input", "Please enter valid numbers.");
            }
        });

        cancelBtn.setOnAction(e -> dialog.close());

        HBox buttonBox = new HBox(10, saveBtn, cancelBtn);
        buttonBox.setAlignment(Pos.CENTER);
        grid.add(buttonBox, 0, 6, 2, 1);

        dialog.setScene(new Scene(grid, 400, 300));
        dialog.showAndWait();
    }

    private void deleteBook(Book book) {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirm Delete");
        confirm.setHeaderText("Delete " + book.getTitle() + "?");
        confirm.setContentText("This action cannot be undone.");
        
        Optional<ButtonType> result = confirm.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            if (bookDAO.deleteBook(book.getId())) {
                showAlert("Success", "Book deleted successfully!");
                loadBooks();
            } else {
                showAlert("Cannot Delete Book", 
                    "This book cannot be deleted because it has transaction history.\n\n" +
                    "Books that have been issued (even if returned) are preserved\n" +
                    "in the database for audit and reporting purposes.\n\n" +
                    "You can edit the book details instead.");
            }
        }
    }

    // ========================================
    // MEMBERS TAB
    // ========================================
    
    private VBox createMembersTab() {
        VBox container = new VBox(15);
        container.setPadding(new Insets(15));

        Label titleLabel = new Label("👥 Member Management");
        titleLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");

        // Search field
        TextField searchField = new TextField();
        searchField.setPromptText("Search by name, email, or phone...");
        searchField.setPrefWidth(300);
        searchField.textProperty().addListener((obs, old, newVal) -> {
            if (newVal.isEmpty()) {
                loadMembers();
            } else {
                memberList.setAll(memberDAO.searchMembers(newVal));
            }
        });

        // Table
        TableView<Member> table = new TableView<>();
        table.setItems(memberList);

        TableColumn<Member, Integer> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(data -> new SimpleIntegerProperty(data.getValue().getMemberId()).asObject());
        idCol.setPrefWidth(50);

        TableColumn<Member, String> nameCol = new TableColumn<>("Name");
        nameCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getName()));
        nameCol.setPrefWidth(180);

        TableColumn<Member, String> emailCol = new TableColumn<>("Email");
        emailCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getEmail()));
        emailCol.setPrefWidth(200);

        TableColumn<Member, String> phoneCol = new TableColumn<>("Phone");
        phoneCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getPhone()));
        phoneCol.setPrefWidth(130);

        TableColumn<Member, String> regDateCol = new TableColumn<>("Registration Date");
        regDateCol.setCellValueFactory(data -> new SimpleStringProperty(
            data.getValue().getRegistrationDate().format(DateTimeFormatter.ISO_LOCAL_DATE)
        ));
        regDateCol.setPrefWidth(130);

        TableColumn<Member, String> statusCol = new TableColumn<>("Status");
        statusCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getStatus()));
        statusCol.setPrefWidth(100);

        table.getColumns().addAll(idCol, nameCol, emailCol, phoneCol, regDateCol, statusCol);

        // Buttons
        Button addBtn = new Button("➕ Register Member");
        Button editBtn = new Button("✏️ Edit Selected");
        Button toggleStatusBtn = new Button("🔄 Toggle Status");
        Button refreshBtn = new Button("🔄 Refresh");

        addBtn.setOnAction(e -> showAddMemberDialog());
        editBtn.setOnAction(e -> {
            Member selected = table.getSelectionModel().getSelectedItem();
            if (selected != null) showEditMemberDialog(selected);
            else showAlert("No Selection", "Please select a member to edit.");
        });
        toggleStatusBtn.setOnAction(e -> {
            Member selected = table.getSelectionModel().getSelectedItem();
            if (selected != null) toggleMemberStatus(selected);
            else showAlert("No Selection", "Please select a member.");
        });
        refreshBtn.setOnAction(e -> loadMembers());

        HBox buttonBox = new HBox(10, addBtn, editBtn, toggleStatusBtn, refreshBtn);
        buttonBox.setAlignment(Pos.CENTER_LEFT);

        HBox searchBox = new HBox(10, new Label("Search:"), searchField);
        searchBox.setAlignment(Pos.CENTER_LEFT);

        container.getChildren().addAll(titleLabel, searchBox, table, buttonBox);
        VBox.setVgrow(table, Priority.ALWAYS);

        return container;
    }

    private void showAddMemberDialog() {
        Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.setTitle("Register New Member");

        GridPane grid = new GridPane();
        grid.setPadding(new Insets(20));
        grid.setHgap(10);
        grid.setVgap(10);

        TextField nameField = new TextField();
        TextField emailField = new TextField();
        TextField phoneField = new TextField();
        TextArea addressArea = new TextArea();
        addressArea.setPrefRowCount(3);

        grid.add(new Label("Name:"), 0, 0);
        grid.add(nameField, 1, 0);
        grid.add(new Label("Email:"), 0, 1);
        grid.add(emailField, 1, 1);
        grid.add(new Label("Phone:"), 0, 2);
        grid.add(phoneField, 1, 2);
        grid.add(new Label("Address:"), 0, 3);
        grid.add(addressArea, 1, 3);

        Button saveBtn = new Button("Register");
        Button cancelBtn = new Button("Cancel");

        saveBtn.setOnAction(e -> {
            Member member = new Member(nameField.getText(), emailField.getText(), 
                                       phoneField.getText(), addressArea.getText());
            if (memberDAO.addMember(member)) {
                showAlert("Success", "Member registered successfully!");
                loadMembers();
                dialog.close();
            } else {
                showAlert("Error", "Failed to register member. Email might already exist.");
            }
        });

        cancelBtn.setOnAction(e -> dialog.close());

        HBox buttonBox = new HBox(10, saveBtn, cancelBtn);
        buttonBox.setAlignment(Pos.CENTER);
        grid.add(buttonBox, 0, 4, 2, 1);

        dialog.setScene(new Scene(grid, 400, 280));
        dialog.showAndWait();
    }

    private void showEditMemberDialog(Member member) {
        Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.setTitle("Edit Member");

        GridPane grid = new GridPane();
        grid.setPadding(new Insets(20));
        grid.setHgap(10);
        grid.setVgap(10);

        TextField nameField = new TextField(member.getName());
        TextField emailField = new TextField(member.getEmail());
        TextField phoneField = new TextField(member.getPhone());
        TextArea addressArea = new TextArea(member.getAddress());
        addressArea.setPrefRowCount(3);

        grid.add(new Label("Name:"), 0, 0);
        grid.add(nameField, 1, 0);
        grid.add(new Label("Email:"), 0, 1);
        grid.add(emailField, 1, 1);
        grid.add(new Label("Phone:"), 0, 2);
        grid.add(phoneField, 1, 2);
        grid.add(new Label("Address:"), 0, 3);
        grid.add(addressArea, 1, 3);

        Button saveBtn = new Button("Save Changes");
        Button cancelBtn = new Button("Cancel");

        saveBtn.setOnAction(e -> {
            member.setName(nameField.getText());
            member.setEmail(emailField.getText());
            member.setPhone(phoneField.getText());
            member.setAddress(addressArea.getText());
            
            if (memberDAO.updateMember(member)) {
                showAlert("Success", "Member updated successfully!");
                loadMembers();
                dialog.close();
            } else {
                showAlert("Error", "Failed to update member.");
            }
        });

        cancelBtn.setOnAction(e -> dialog.close());

        HBox buttonBox = new HBox(10, saveBtn, cancelBtn);
        buttonBox.setAlignment(Pos.CENTER);
        grid.add(buttonBox, 0, 4, 2, 1);

        dialog.setScene(new Scene(grid, 400, 280));
        dialog.showAndWait();
    }

    private void toggleMemberStatus(Member member) {
        String newStatus = member.getStatus().equals("Active") ? "Inactive" : "Active";
        member.setStatus(newStatus);
        if (memberDAO.updateMember(member)) {
            showAlert("Success", "Member status updated to " + newStatus);
            loadMembers();
        }
    }

    // ========================================
    // TRANSACTIONS TAB
    // ========================================
    
    private VBox createTransactionsTab() {
        VBox container = new VBox(15);
        container.setPadding(new Insets(15));

        Label titleLabel = new Label("📖 Issue & Return Books");
        titleLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");

        // Issue Section
        Label issueLabel = new Label("Issue Book:");
        issueLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");

        ComboBox<Member> memberCombo = new ComboBox<>();
        memberCombo.setPromptText("Select Member");
        memberCombo.setPrefWidth(250);

        ComboBox<Book> bookCombo = new ComboBox<>();
        bookCombo.setPromptText("Select Book");
        bookCombo.setPrefWidth(350);

        Button issueBtn = new Button("📤 Issue Book");
        issueBtn.setOnAction(e -> {
            Member member = memberCombo.getValue();
            Book book = bookCombo.getValue();
            if (member == null || book == null) {
                showAlert("Incomplete", "Please select both member and book.");
                return;
            }
            if (book.getAvailableQuantity() <= 0) {
                showAlert("Unavailable", "This book is currently not available.");
                return;
            }
            if (transactionDAO.issueBook(book.getId(), member.getMemberId())) {
                showAlert("Success", String.format("Book '%s' issued to %s", book.getTitle(), member.getName()));
                loadTransactions();
                loadBooks(); // Refresh availability
                bookCombo.getItems().clear();
                bookCombo.getItems().addAll(bookDAO.getAvailableBooks());
            } else {
                showAlert("Error", "Failed to issue book.");
            }
        });

        HBox issueBox = new HBox(10, memberCombo, bookCombo, issueBtn);
        issueBox.setAlignment(Pos.CENTER_LEFT);

        // Active Transactions Table
        Label activeLabel = new Label("Active Transactions:");
        activeLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");

        TableView<Transaction> activeTable = new TableView<>();
        activeTable.setItems(transactionList);
        activeTable.setRowFactory(tv -> new TableRow<Transaction>() {
            @Override
            protected void updateItem(Transaction txn, boolean empty) {
                super.updateItem(txn, empty);
                if (empty || txn == null) {
                    setStyle("");
                } else if (txn.isOverdue()) {
                    setStyle("-fx-background-color: #ffcccc;");
                } else {
                    setStyle("");
                }
            }
        });

        TableColumn<Transaction, Integer> txnIdCol = new TableColumn<>("Txn ID");
        txnIdCol.setCellValueFactory(data -> new SimpleIntegerProperty(data.getValue().getTransactionId()).asObject());
        txnIdCol.setPrefWidth(60);

        TableColumn<Transaction, String> bookTitleCol = new TableColumn<>("Book Title");
        bookTitleCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getBookTitle()));
        bookTitleCol.setPrefWidth(220);

        TableColumn<Transaction, String> memberNameCol = new TableColumn<>("Member Name");
        memberNameCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getMemberName()));
        memberNameCol.setPrefWidth(180);

        TableColumn<Transaction, String> issueDateCol = new TableColumn<>("Issue Date");
        issueDateCol.setCellValueFactory(data -> new SimpleStringProperty (
            data.getValue().getIssueDate().format(DateTimeFormatter.ISO_LOCAL_DATE)
        ));
        issueDateCol.setPrefWidth(100);

        TableColumn<Transaction, String> dueDateCol = new TableColumn<>("Due Date");
        dueDateCol.setCellValueFactory(data -> new SimpleStringProperty(
            data.getValue().getDueDate().format(DateTimeFormatter.ISO_LOCAL_DATE)
        ));
        dueDateCol.setPrefWidth(100);

        TableColumn<Transaction, String> statusColTxn = new TableColumn<>("Status");
        statusColTxn.setCellValueFactory(data -> {
            Transaction txn = data.getValue();
            if (txn.isOverdue()) {
                return new SimpleStringProperty("⚠️ OVERDUE (" + txn.getDaysOverdue() + " days)");
            }
            return new SimpleStringProperty(txn.getStatus());
        });
        statusColTxn.setPrefWidth(150);

        activeTable.getColumns().addAll(txnIdCol, bookTitleCol, memberNameCol, issueDateCol, dueDateCol, statusColTxn);

        Button returnBtn = new Button("📥 Return Selected Book");
        Button refreshTxnBtn = new Button("🔄 Refresh");

        returnBtn.setOnAction(e -> {
            Transaction selected = activeTable.getSelectionModel().getSelectedItem();
            if (selected != null && selected.getStatus().equals("Issued")) {
                if (transactionDAO.returnBook(selected.getTransactionId())) {
                    showAlert("Success", "Book returned successfully!");
                    loadTransactions();
                    loadBooks();
                    bookCombo.getItems().clear();
                    bookCombo.getItems().addAll(bookDAO.getAvailableBooks());
                } else {
                    showAlert("Error", "Failed to return book.");
                }
            } else {
                showAlert("No Selection", "Please select an issued transaction.");
            }
        });

        refreshTxnBtn.setOnAction(e -> {
            loadTransactions();
            loadMembers();
            loadBooks();
            memberCombo.getItems().clear();
            memberCombo.getItems().addAll(memberDAO.getActiveMembers());
            bookCombo.getItems().clear();
            bookCombo.getItems().addAll(bookDAO.getAvailableBooks());
        });

        HBox txnButtonBox = new HBox(10, returnBtn, refreshTxnBtn);
        txnButtonBox.setAlignment(Pos.CENTER_LEFT);

        container.getChildren().addAll(
            titleLabel,
            new Separator(),
            issueLabel, issueBox,
            new Separator(),
            activeLabel, activeTable, txnButtonBox
        );

        VBox.setVgrow(activeTable, Priority.ALWAYS);

        // Initialize combos
        memberCombo.getItems().addAll(memberDAO.getActiveMembers());
        bookCombo.getItems().addAll(bookDAO.getAvailableBooks());

        return container;
    }

    // ========================================
    // DATA LOADING
    // ========================================
    
    private void loadBooks() {
        bookList.setAll(bookDAO.getAllBooks());
    }

    private void loadMembers() {
        memberList.setAll(memberDAO.getAllMembers());
    }

    private void loadTransactions() {
        transactionList.setAll(transactionDAO.getActiveTransactions());
    }

    // ========================================
    // HELPERS
    // ========================================
    
    // Helper method to create modern styled buttons
    private Button createStyledButton(String text, String color) {
        Button button = new Button(text);
        button.setStyle(String.format(
            "-fx-background-color: %s; " +
            "-fx-text-fill: white; " +
            "-fx-font-size: 13px; " +
            "-fx-font-weight: 500; " +
            "-fx-background-radius: 6px; " +
            "-fx-padding: 10px 20px; " +
            "-fx-cursor: hand; " +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.15), 4, 0, 0, 2);",
            color
        ));
        
        // Hover effect
        button.setOnMouseEntered(e -> {
            button.setStyle(String.format(
                "-fx-background-color: derive(%s, -10%%); " +
                "-fx-text-fill: white; " +
                "-fx-font-size: 13px; " +
                "-fx-font-weight: 500; " +
                "-fx-background-radius: 6px; " +
                "-fx-padding: 10px 20px; " +
                "-fx-cursor: hand; " +
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.25), 6, 0, 0, 3);",
                color
            ));
        });
        
        button.setOnMouseExited(e -> {
            button.setStyle(String.format(
                "-fx-background-color: %s; " +
                "-fx-text-fill: white; " +
                "-fx-font-size: 13px; " +
                "-fx-font-weight: 500; " +
                "-fx-background-radius: 6px; " +
                "-fx-padding: 10px 20px; " +
                "-fx-cursor: hand; " +
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.15), 4, 0, 0, 2);",
                color
            ));
        });
        
        return button;
    }
    
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
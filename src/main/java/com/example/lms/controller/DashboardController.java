package com.example.lms.controller;

import com.example.lms.dao.BookDAO;
import com.example.lms.models.Book;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.scene.control.*;

public class DashboardController {
    private BookDAO bookDAO = new BookDAO();
    private ObservableList<Book> masterData = FXCollections.observableArrayList();

    public void initSearch(TextField searchField, TableView<Book> table) {
        // 1. Load data from DAO
        masterData.addAll(bookDAO.getAllBooks());

        // 2. Wrap the ObservableList in a FilteredList
        FilteredList<Book> filteredData = new FilteredList<>(masterData, p -> true);

        // 3. Set the filter Predicate whenever the search text changes
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredData.setPredicate(book -> {
                if (newValue == null || newValue.isEmpty()) return true;
                String lowerCaseFilter = newValue.toLowerCase();
                
                if (book.getTitle().toLowerCase().contains(lowerCaseFilter)) return true;
                if (book.getAuthor().toLowerCase().contains(lowerCaseFilter)) return true;
                return false; 
            });
        });
        table.setItems(filteredData);
    }
}
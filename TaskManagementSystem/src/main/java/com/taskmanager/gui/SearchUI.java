package com.taskmanager.gui;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.*;
import com.taskmanager.controllers.TaskController;
import com.taskmanager.models.Task;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

public class SearchUI {
    public static Pane getView() {
        VBox layout = new VBox(10);

        TextField titleField = new TextField();
        titleField.setPromptText("Search by Title...");
        
        TextField categoryField = new TextField();
        categoryField.setPromptText("Search by Category...");
        
        TextField priorityField = new TextField();
        priorityField.setPromptText("Search by Priority...");

        ObservableList<Task> searchResultsList = FXCollections.observableArrayList();
        TableView<Task> searchResults = new TableView<>(searchResultsList);
        searchResults.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        TableColumn<Task, String> titleColumn = new TableColumn<>("Title");
        titleColumn.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getTitle()));

        TableColumn<Task, String> categoryColumn = new TableColumn<>("Category");
        categoryColumn.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getCategory()));

        TableColumn<Task, String> priorityColumn = new TableColumn<>("Priority");
        priorityColumn.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getPriority()));

        TableColumn<Task, LocalDate> dueDateColumn = new TableColumn<>("Due Date");
        dueDateColumn.setCellValueFactory(data -> new javafx.beans.property.SimpleObjectProperty<>(data.getValue().getDueDate()));

        searchResults.getColumns().addAll(titleColumn, categoryColumn, priorityColumn, dueDateColumn);

        Runnable searchLogic = () -> {
            String titleText = titleField.getText().trim().toLowerCase();
            String categoryText = categoryField.getText().trim().toLowerCase();
            String priorityText = priorityField.getText().trim().toLowerCase();

            try {
                List<Task> tasks = TaskController.loadTasks();
                List<Task> filteredTasks = tasks.stream()
                        .filter(task -> (titleText.isEmpty() || task.getTitle().toLowerCase().contains(titleText)) &&
                                        (categoryText.isEmpty() || task.getCategory().toLowerCase().contains(categoryText)) &&
                                        (priorityText.isEmpty() || task.getPriority().toLowerCase().contains(priorityText)))
                        .collect(Collectors.toList());

                searchResultsList.setAll(filteredTasks);
            } catch (IOException e) {
                e.printStackTrace();
            }
        };

        titleField.setOnKeyPressed(event -> {if (event.getCode() == KeyCode.ENTER) {searchLogic.run();}});
        categoryField.setOnKeyPressed(event -> {if (event.getCode() == KeyCode.ENTER) {searchLogic.run();}});
        priorityField.setOnKeyPressed(event -> {if (event.getCode() == KeyCode.ENTER) {searchLogic.run();}});

        layout.getChildren().addAll(new Label("Search Tasks"), titleField, categoryField, priorityField, searchResults);
        layout.setStyle("-fx-padding: 10;");

        return layout;
    }
}
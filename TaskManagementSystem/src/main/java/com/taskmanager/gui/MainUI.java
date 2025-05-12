package com.taskmanager.gui;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import com.taskmanager.controllers.*;
import com.taskmanager.models.Task;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.time.LocalDate;
import java.util.List;

public class MainUI extends Application {
    private static final String MEDIALAB_FOLDER = "medialab";
    private static final String BACKUP_FOLDER = "medialab/backup";

    public void start(Stage primaryStage) {
        // Top Section
        Label totalTasksLabel = new Label("Total Tasks: 0");
        Label completedTasksLabel = new Label("Completed Tasks: 0");
        Label delayedTasksLabel = new Label("Delayed Tasks: 0");
        Label dueSoonTasksLabel = new Label("Tasks Due Within 7 Days: 0");

        updateTaskStatistics(totalTasksLabel, completedTasksLabel, delayedTasksLabel, dueSoonTasksLabel);

        HBox statsBox = new HBox(20, totalTasksLabel, completedTasksLabel, delayedTasksLabel, dueSoonTasksLabel);
        statsBox.setPadding(new Insets(10));
        statsBox.setStyle("-fx-border-color: gray; -fx-border-width: 1; -fx-padding: 10;");

        // Bottom Section
        TabPane tabPane = new TabPane();

        Tab taskTab = new Tab("Tasks", TaskUI.getView());
        Tab categoryTab = new Tab("Categories", CategoryUI.getView());
        Tab priorityTab = new Tab("Priorities", PriorityUI.getView());
        Tab reminderTab = new Tab("Reminders", ReminderUI.getView());
        Tab searchTab = new Tab("Search", SearchUI.getView());

        tabPane.getTabs().addAll(taskTab, categoryTab, priorityTab, reminderTab, searchTab);

        Button refreshButton = new Button("Refresh");
        refreshButton.setOnAction(event -> {
            updateTaskStatistics(totalTasksLabel, completedTasksLabel, delayedTasksLabel, dueSoonTasksLabel);
            taskTab.setContent(TaskUI.getView());
            categoryTab.setContent(CategoryUI.getView());
            priorityTab.setContent(PriorityUI.getView());
            reminderTab.setContent(ReminderUI.getView());
            searchTab.setContent(SearchUI.getView());

            List<Task> tasks;
			try {
				tasks = TaskController.loadTasks();
				
				int delayedTasks = (int) tasks.stream().filter(task -> Task.STATUS_DELAYED.equals(task.getStatus())).count();
	            if (delayedTasks > 0) {
	                showDelayedTasksPopup(delayedTasks); 
	            }
			} catch (IOException e) {
				e.printStackTrace();
			}
        });

        Button backupButton = new Button("Backup");
        backupButton.setOnAction(event -> backupData());

        Button restoreButton = new Button("Restore");
        restoreButton.setOnAction(event -> restoreData());

        ToolBar toolBar = new ToolBar();
        toolBar.getItems().addAll(refreshButton, backupButton, restoreButton);

        VBox bottomSection = new VBox(toolBar, tabPane);

        // Main Layout
        VBox mainLayout = new VBox(statsBox, bottomSection);
        VBox.setVgrow(tabPane, Priority.ALWAYS);

        Scene scene = new Scene(mainLayout, 800, 600);
        primaryStage.setScene(scene);
        primaryStage.setTitle("MediaLab Assistant");
        primaryStage.show();
        
        taskTab.setOnSelectionChanged(event -> {
            if (taskTab.isSelected()) {
                taskTab.setContent(TaskUI.getView());  
            }
        });

        categoryTab.setOnSelectionChanged(event -> {
            if (categoryTab.isSelected()) {
                categoryTab.setContent(CategoryUI.getView());  
            }
        });

        priorityTab.setOnSelectionChanged(event -> {
            if (priorityTab.isSelected()) {
                priorityTab.setContent(PriorityUI.getView());  
            }
        });

        reminderTab.setOnSelectionChanged(event -> {
            if (reminderTab.isSelected()) {
                reminderTab.setContent(ReminderUI.getView());  
            }
        });

        searchTab.setOnSelectionChanged(event -> {
            if (searchTab.isSelected()) {
                searchTab.setContent(SearchUI.getView());  
            }
        });
    }

    private void updateTaskStatistics(Label totalLabel, Label completedLabel, Label delayedLabel, Label dueSoonLabel) {
        try {
            List<Task> tasks = TaskController.loadTasks();
            int totalTasks = tasks.size();
            int completedTasks = (int) tasks.stream().filter(task -> Task.STATUS_COMPLETED.equals(task.getStatus())).count();
            int delayedTasks = (int) tasks.stream().filter(task -> Task.STATUS_DELAYED.equals(task.getStatus())).count();
            int dueSoonTasks = (int) tasks.stream()
                    .filter(task -> !Task.STATUS_COMPLETED.equals(task.getStatus()) &&
                            !task.getDueDate().isBefore(LocalDate.now()) &&  
                            task.getDueDate().isBefore(LocalDate.now().plusDays(8)))  
                    .count();

            totalLabel.setText("Total Tasks: " + totalTasks);
            completedLabel.setText("Completed Tasks: " + completedTasks);
            delayedLabel.setText("Delayed Tasks: " + delayedTasks);
            dueSoonLabel.setText("Tasks Due Within 7 Days: " + dueSoonTasks);
        } catch (IOException e) {
            showError("Error loading tasks: " + e.getMessage());
        }
    }

    private void backupData() {
        try {
            Files.createDirectories(Paths.get(BACKUP_FOLDER)); 

            File medialabDir = new File(MEDIALAB_FOLDER);
            if (!medialabDir.exists() || !medialabDir.isDirectory()) {
                showError("Medialab directory not found.");
                return;
            }

            File[] jsonFiles = medialabDir.listFiles((dir, name) -> name.endsWith(".json"));
            if (jsonFiles == null || jsonFiles.length == 0) {
                showError("No JSON files found to back up.");
                return;
            }

            for (File jsonFile : jsonFiles) {
                Path sourcePath = jsonFile.toPath();
                Path backupPath = Paths.get(BACKUP_FOLDER, jsonFile.getName().replace(".json", "_backup.json"));
                Files.copy(sourcePath, backupPath, StandardCopyOption.REPLACE_EXISTING);
            }

            showInfo("Backup completed successfully.");
        } catch (IOException e) {
            showError("Error during backup: " + e.getMessage());
        }
    }

    private void restoreData() {
        try {
            File backupDir = new File(BACKUP_FOLDER);
            if (!backupDir.exists() || !backupDir.isDirectory()) {
                showError("Backup directory not found.");
                return;
            }

            File[] backupFiles = backupDir.listFiles((dir, name) -> name.endsWith("_backup.json"));
            if (backupFiles == null || backupFiles.length == 0) {
                showError("No backup files found.");
                return;
            }

            Files.createDirectories(Paths.get(MEDIALAB_FOLDER)); 

            for (File backupFile : backupFiles) {
                Path backupPath = backupFile.toPath();
                Path originalPath = Paths.get(MEDIALAB_FOLDER, backupFile.getName().replace("_backup", ""));
                Files.copy(backupPath, originalPath, StandardCopyOption.REPLACE_EXISTING);
            }

            showInfo("Restore completed successfully.");
        } catch (IOException e) {
            showError("Error during restore: " + e.getMessage());
        }
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showInfo(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Information");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private static void showDelayedTasksPopup(long delayedCount) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Delayed Tasks");
        alert.setHeaderText("You have " + delayedCount + " delayed tasks.");
        alert.showAndWait();
    }
    
    public static void main(String[] args) {
        launch(args);
    }
}
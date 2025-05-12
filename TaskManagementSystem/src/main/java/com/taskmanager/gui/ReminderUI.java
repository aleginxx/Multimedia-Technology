package com.taskmanager.gui;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import com.taskmanager.controllers.ReminderController;
import com.taskmanager.controllers.TaskController;
import com.taskmanager.models.Reminder;
import com.taskmanager.models.Task;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

public class ReminderUI {
    public static Pane getView() {
        VBox layout = new VBox(10);

        ObservableList<Reminder> reminderList = FXCollections.observableArrayList();
        TableView<Reminder> reminderTable = new TableView<>(reminderList);
        reminderTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        TableColumn<Reminder, String> taskTitleColumn = new TableColumn<>("Task Title");
        taskTitleColumn.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getTaskTitle()));

        TableColumn<Reminder, LocalDate> reminderDateColumn = new TableColumn<>("Reminder Date");
        reminderDateColumn.setCellValueFactory(data -> new javafx.beans.property.SimpleObjectProperty<>(data.getValue().getReminderDate()));

        reminderTable.getColumns().addAll(taskTitleColumn, reminderDateColumn);

        try {
            List<Reminder> reminders = ReminderController.loadReminders();
            reminderList.addAll(reminders);
        } catch (IOException e) {
            e.printStackTrace();
        }

        ObservableList<String> taskTitles = FXCollections.observableArrayList();
        try {
            List<Task> tasks = TaskController.loadTasks();
            for (Task task : tasks) {
                taskTitles.add(task.getTitle());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        Button addReminderButton = new Button("Add Reminder");
        Button editReminderButton = new Button("Edit Reminder");
        Button deleteReminderButton = new Button("Delete Reminder");

        // Add Reminder
        addReminderButton.setOnAction(event -> {
            Reminder newReminder = showReminderDialog(null, taskTitles);
            if (newReminder != null) {
                try {
                    ReminderController.addReminder(newReminder);
                    reminderList.add(newReminder);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        // Edit Reminder
        editReminderButton.setOnAction(event -> {
            Reminder selectedReminder = reminderTable.getSelectionModel().getSelectedItem();
            if (selectedReminder != null) {
                Reminder updatedReminder = showReminderDialog(selectedReminder, taskTitles);
                if (updatedReminder != null) {
                    try {
                        ReminderController.updateReminder(updatedReminder);
                        reminderTable.refresh();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            } else {
                showError("Please select a reminder to edit.");
            }
        });

        // Delete Reminder
        deleteReminderButton.setOnAction(event -> {
            Reminder selectedReminder = reminderTable.getSelectionModel().getSelectedItem();
            if (selectedReminder != null) {
                try {
                    ReminderController.deleteReminder(selectedReminder);
                    reminderList.remove(selectedReminder);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                showError("Please select a reminder to delete.");
            }
        });

        layout.getChildren().addAll(reminderTable, new HBox(10, addReminderButton, editReminderButton, deleteReminderButton));
        layout.setStyle("-fx-padding: 10;");

        return layout;
    }

    private static Reminder showReminderDialog(Reminder reminderToEdit, ObservableList<String> taskTitles) {
        ComboBox<String> taskDropdown = new ComboBox<>(taskTitles);
        taskDropdown.setValue(reminderToEdit != null ? reminderToEdit.getTaskTitle() : null);

        ComboBox<String> reminderTypeDropdown = new ComboBox<>();
        reminderTypeDropdown.getItems().addAll("1 Day Before", "1 Week Before", "1 Month Before", "Specific Date");
        reminderTypeDropdown.setValue(reminderToEdit != null ? "Specific Date" : "Specific Date");

        DatePicker reminderDatePicker = new DatePicker(reminderToEdit != null ? reminderToEdit.getReminderDate() : LocalDate.now());
        reminderDatePicker.setDisable(!reminderTypeDropdown.getValue().equals("Specific Date"));

        Dialog<Reminder> dialog = new Dialog<>();
        dialog.setTitle(reminderToEdit == null ? "Add Reminder" : "Edit Reminder");
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        VBox content = new VBox(10);
        content.getChildren().addAll(
                new Label("Task:"), taskDropdown,
                new Label("Reminder Type:"), reminderTypeDropdown,
                new Label("Reminder Date:"), reminderDatePicker
        );

        dialog.getDialogPane().setContent(content);

        reminderTypeDropdown.setOnAction(e -> {
            reminderDatePicker.setDisable(!reminderTypeDropdown.getValue().equals("Specific Date"));
            if (!reminderDatePicker.isDisable()) {
                reminderDatePicker.setValue(LocalDate.now());
            }
        });

        dialog.setResultConverter(button -> {
            if (button == ButtonType.OK) {
                String taskTitle = taskDropdown.getValue();
                LocalDate reminderDate = reminderDatePicker.getValue();

                if (taskTitle == null || taskTitle.isEmpty()) {
                    showError("Please select a task.");
                    return null;
                }

                try {
                    Task task = TaskController.getTaskByTitle(taskTitle);
                    if (task == null || task.getStatus().equals(Task.STATUS_COMPLETED)) {
                        showError("This task is completed or does not exist.");
                        return null;
                    }

                    // Set reminder date based on reminder type
                    if ("1 Day Before".equals(reminderTypeDropdown.getValue())) {
                        reminderDate = task.getDueDate().minusDays(1);
                    } else if ("1 Week Before".equals(reminderTypeDropdown.getValue())) {
                        reminderDate = task.getDueDate().minusWeeks(1);
                    } else if ("1 Month Before".equals(reminderTypeDropdown.getValue())) {
                        reminderDate = task.getDueDate().minusMonths(1);
                    }

                    // Check if reminder date is feasible
                    if (reminderDate.isAfter(task.getDueDate())) {
                        showError("Reminder date cannot exceed the task's due date.");
                        return null;
                    }

                    // Check if reminder date is before the task's start date (if applicable)
                    if (reminderDate.isBefore(LocalDate.now())) {
                        showError("Reminder date cannot be in the past.");
                        return null;
                    }

                    // If editing, update the reminder with the new task title and date
                    if (reminderToEdit != null) {
                        reminderToEdit.setTaskTitle(taskTitle);
                        reminderToEdit.setReminderDate(reminderDate);
                        return reminderToEdit;
                    }

                    // If adding a new reminder, create and return it
                    return new Reminder(taskTitle, reminderDate);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return null;
        });

        return dialog.showAndWait().orElse(null);
    }

    private static void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
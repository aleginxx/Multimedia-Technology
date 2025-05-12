package com.taskmanager.gui;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.control.TreeItem;
import com.taskmanager.controllers.*;
import com.taskmanager.models.Task;

import java.io.IOException;
import java.time.LocalDate;
import java.util.*;

public class TaskUI {
    public static Pane getView() {
        VBox layout = new VBox(10);

        ObservableList<Task> taskList = FXCollections.observableArrayList();

        TreeTableView<Task> taskTable = new TreeTableView<>();
        taskTable.setColumnResizePolicy(TreeTableView.CONSTRAINED_RESIZE_POLICY);  

        TreeTableColumn<Task, String> titleColumn = new TreeTableColumn<>("Title");
        titleColumn.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getValue().getTitle()));

        TreeTableColumn<Task, String> descriptionColumn = new TreeTableColumn<>("Description");
        descriptionColumn.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getValue().getDescription()));

        TreeTableColumn<Task, String> categoryColumn = new TreeTableColumn<>("Category");
        categoryColumn.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getValue().getCategory()));

        TreeTableColumn<Task, String> priorityColumn = new TreeTableColumn<>("Priority");
        priorityColumn.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getValue().getPriority()));

        TreeTableColumn<Task, LocalDate> dueDateColumn = new TreeTableColumn<>("Due Date");
        dueDateColumn.setCellValueFactory(data -> new javafx.beans.property.SimpleObjectProperty<>(data.getValue().getValue().getDueDate()));

        TreeTableColumn<Task, String> statusColumn = new TreeTableColumn<>("Status");
        statusColumn.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getValue().getStatus()));

        taskTable.getColumns().addAll(titleColumn, descriptionColumn, categoryColumn, priorityColumn, dueDateColumn, statusColumn);

        try {
            List<Task> tasks = TaskController.loadTasks();
            // Group tasks by category
            Map<String, List<Task>> tasksByCategory = new HashMap<>();
            for (Task task : tasks) {
                tasksByCategory.computeIfAbsent(task.getCategory(), k -> new ArrayList<>()).add(task);
            }

            List<String> sortedCategories = new ArrayList<>(tasksByCategory.keySet());
            Collections.sort(sortedCategories);

            ObservableList<TreeItem<Task>> rootItems = FXCollections.observableArrayList();
            for (String category : sortedCategories) {
                TreeItem<Task> categoryItem = new TreeItem<>(new Task(category, "Category: " + category, category, "Default", LocalDate.now())); // Create dummy task for category
                categoryItem.setExpanded(true);
                for (Task task : tasksByCategory.get(category)) {
                    TreeItem<Task> taskItem = new TreeItem<>(task);
                    categoryItem.getChildren().add(taskItem);
                }
                rootItems.add(categoryItem);
            }

            taskTable.setRoot(new TreeItem<>());
            taskTable.setShowRoot(false);  
            taskTable.getRoot().getChildren().addAll(rootItems);
        } catch (IOException e) {
            e.printStackTrace();
        }

        ObservableList<String> categories = FXCollections.observableArrayList();
        try {
            categories.addAll(CategoryController.loadCategoryNames());
        } catch (IOException e) {
            e.printStackTrace();
        }

        ObservableList<String> priorities = FXCollections.observableArrayList();
        try {
            priorities.addAll(PriorityController.loadPriorityNames());
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Buttons
        Button addTaskButton = new Button("Add Task");
        Button editTaskButton = new Button("Edit Task");
        Button deleteTaskButton = new Button("Delete Task");

        // Add Task
        addTaskButton.setOnAction(event -> {
            TextInputDialog titleDialog = new TextInputDialog("New Task");
            titleDialog.setHeaderText("Enter Task Title");
            String title = titleDialog.showAndWait().orElse("New Task");

            TextInputDialog descriptionDialog = new TextInputDialog("Description");
            descriptionDialog.setHeaderText("Enter Task Description");
            String description = descriptionDialog.showAndWait().orElse("Default");

            ComboBox<String> categoryDropdown = new ComboBox<>(categories);
            Dialog<String> categoryDialog = new Dialog<>();
            categoryDialog.setTitle("Select Task Category");
            categoryDialog.getDialogPane().setContent(categoryDropdown);
            categoryDialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

            categoryDialog.setResultConverter(button -> {
                if (button == ButtonType.OK) {
                    return categoryDropdown.getValue();
                }
                return "Default";
            });
            String category = categoryDialog.showAndWait().orElse("Default");     
            
            ComboBox<String> priorityDropdown = new ComboBox<>(priorities);
            priorityDropdown.setValue("Default"); 
            Dialog<String> priorityDialog = new Dialog<>();
            priorityDialog.setTitle("Select Task Priority");
            priorityDialog.getDialogPane().setContent(priorityDropdown);
            priorityDialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
            
            priorityDialog.setResultConverter(button -> {
                if (button == ButtonType.OK) {
                    return priorityDropdown.getValue();
                }
                return "Default";
            });
            String priority = priorityDialog.showAndWait().orElse("Default");     

            DatePicker dueDatePicker = new DatePicker(LocalDate.now());
            Dialog<LocalDate> dueDateDialog = new Dialog<>();
            dueDateDialog.setTitle("Select Due Date");
            dueDateDialog.getDialogPane().setContent(dueDatePicker);
            dueDateDialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

            dueDateDialog.setResultConverter(button -> {
                if (button == ButtonType.OK) {
                    return dueDatePicker.getValue();
                }
                return null;
            });

            LocalDate dueDate = dueDateDialog.showAndWait().orElse(LocalDate.now());

            Task newTask = new Task(title, description, category, priority, dueDate);
            try {
                TaskController.addTask(newTask);
                loadTasksIntoTable(taskTable); 
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        // Edit Task
        editTaskButton.setOnAction(event -> {
            Task selectedTask = taskTable.getSelectionModel().getSelectedItem().getValue();
            if (selectedTask != null) {
            	String taskId = selectedTask.getTaskId();
                String originalTitle = selectedTask.getTitle();

                TextInputDialog titleDialog = new TextInputDialog(selectedTask.getTitle());
                titleDialog.setHeaderText("Edit Task Title");
                String newTitle = titleDialog.showAndWait().orElse(selectedTask.getTitle());

                TextInputDialog descriptionDialog = new TextInputDialog(selectedTask.getDescription());
                descriptionDialog.setHeaderText("Edit Task Description");
                String newDescription = descriptionDialog.showAndWait().orElse(selectedTask.getDescription());

                ComboBox<String> categoryDropdown = new ComboBox<>(categories);
                categoryDropdown.setValue(selectedTask.getCategory());
                Dialog<String> categoryDialog = new Dialog<>();
                categoryDialog.setTitle("Select Task Category");
                categoryDialog.getDialogPane().setContent(categoryDropdown);
                categoryDialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

                categoryDialog.setResultConverter(button -> {
                    if (button == ButtonType.OK) {
                        return categoryDropdown.getValue();
                    }
                    return selectedTask.getCategory();
                });
                String newCategory = categoryDialog.showAndWait().orElse(selectedTask.getCategory());

                ComboBox<String> priorityDropdown = new ComboBox<>(priorities);
                priorityDropdown.setValue(selectedTask.getPriority());
                Dialog<String> priorityDialog = new Dialog<>();
                priorityDialog.setTitle("Select Task Priority");
                priorityDialog.getDialogPane().setContent(priorityDropdown);
                priorityDialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

                priorityDialog.setResultConverter(button -> {
                    if (button == ButtonType.OK) {
                        return priorityDropdown.getValue();
                    }
                    return selectedTask.getPriority();
                });
                String newPriority = priorityDialog.showAndWait().orElse(selectedTask.getPriority());

                DatePicker dueDatePicker = new DatePicker(selectedTask.getDueDate());
                Dialog<LocalDate> dueDateDialog = new Dialog<>();
                dueDateDialog.setTitle("Select Task Due Date");
                dueDateDialog.getDialogPane().setContent(dueDatePicker);
                dueDateDialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

                dueDateDialog.setResultConverter(button -> {
                    if (button == ButtonType.OK) {
                        return dueDatePicker.getValue();
                    }
                    return selectedTask.getDueDate();
                });
                LocalDate newDueDate = dueDateDialog.showAndWait().orElse(selectedTask.getDueDate());

                ComboBox<String> statusDropdown = new ComboBox<>();
                statusDropdown.getItems().addAll(Task.STATUS_OPEN, Task.STATUS_IN_PROGRESS, Task.STATUS_POSTPONED, Task.STATUS_COMPLETED);
                statusDropdown.setValue(selectedTask.getStatus());

                Dialog<String> statusDialog = new Dialog<>();
                statusDialog.setTitle("Edit Task Status");
                statusDialog.getDialogPane().setContent(statusDropdown);
                statusDialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

                statusDialog.setResultConverter(button -> {
                    if (button == ButtonType.OK) {
                        return statusDropdown.getValue();
                    }
                    return selectedTask.getStatus();
                });

                String newStatus = statusDialog.showAndWait().orElse(selectedTask.getStatus());

                selectedTask.setTitle(newTitle);
                selectedTask.setDescription(newDescription);
                selectedTask.setCategory(newCategory);
                selectedTask.setPriority(newPriority);
                selectedTask.setDueDate(newDueDate);
                selectedTask.setStatus(newStatus);

                try {
                    TaskController.updateTask(taskId, selectedTask);
                    loadTasksIntoTable(taskTable); 
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        deleteTaskButton.setOnAction(event -> {
            Task selectedTask = taskTable.getSelectionModel().getSelectedItem().getValue();
            if (selectedTask != null) {
                try {
                    TaskController.deleteTask(selectedTask);
                    loadTasksIntoTable(taskTable); 
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        layout.getChildren().addAll(taskTable, new HBox(10, addTaskButton, editTaskButton, deleteTaskButton));
        return layout;
    }

    private static void loadTasksIntoTable(TreeTableView<Task> taskTable) throws IOException {
        List<Task> tasks = TaskController.loadTasks();
        Map<String, List<Task>> tasksByCategory = new HashMap<>();
        for (Task task : tasks) {
            tasksByCategory.computeIfAbsent(task.getCategory(), k -> new ArrayList<>()).add(task);
        }

        List<String> sortedCategories = new ArrayList<>(tasksByCategory.keySet());
        Collections.sort(sortedCategories);

        ObservableList<TreeItem<Task>> rootItems = FXCollections.observableArrayList();
        for (String category : sortedCategories) {
            TreeItem<Task> categoryItem = new TreeItem<>(new Task(category, "Category: " + category, category, "Default", LocalDate.now()));
            categoryItem.setExpanded(true);
            for (Task task : tasksByCategory.get(category)) {
                TreeItem<Task> taskItem = new TreeItem<>(task);
                categoryItem.getChildren().add(taskItem);
            }
            rootItems.add(categoryItem);
        }

        taskTable.setRoot(new TreeItem<>());
        taskTable.setShowRoot(false);  
        taskTable.getRoot().getChildren().addAll(rootItems);
    }
}
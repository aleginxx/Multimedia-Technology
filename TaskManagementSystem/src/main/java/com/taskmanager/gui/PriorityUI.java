package com.taskmanager.gui;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import com.taskmanager.controllers.PriorityController;
import com.taskmanager.models.Priority;

import java.io.IOException;
import java.util.List;

public class PriorityUI {
    public static Pane getView() {
        VBox layout = new VBox(10);

        Label priorityHeader = new Label("Priority Level");

        ObservableList<Priority> priorityList = FXCollections.observableArrayList();
        ListView<Priority> priorityListView = new ListView<>(priorityList);
        priorityListView.setCellFactory(param -> new ListCell<Priority>() {
            protected void updateItem(Priority priority, boolean empty) {
                super.updateItem(priority, empty);
                if (empty || priority == null) {
                    setText(null);
                } else {
                    setText(priority.getName());
                }
            }
        });

        TextField priorityField = new TextField();
        Button addPriorityButton = new Button("Add Priority");
        Button editPriorityButton = new Button("Edit Priority");
        Button deletePriorityButton = new Button("Delete Priority");

        loadPriorities(priorityList);
        addPriorityButton.setOnAction(event -> addPriority(priorityField, priorityList));
        editPriorityButton.setOnAction(event -> editPriority(priorityListView));
        deletePriorityButton.setOnAction(event -> deletePriority(priorityListView, priorityList));

        layout.getChildren().addAll(priorityHeader, priorityListView, priorityField, new HBox(10, addPriorityButton, editPriorityButton, deletePriorityButton));
        layout.setStyle("-fx-padding: 10;");

        return layout;
    }

    private static void loadPriorities(ObservableList<Priority> priorityList) {
        try {
            List<Priority> priorities = PriorityController.loadPriorities();
            priorities.sort((p1, p2) -> p1.getName().compareToIgnoreCase(p2.getName()));
            priorityList.addAll(priorities);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void addPriority(TextField priorityField, ObservableList<Priority> priorityList) {
        String priorityName = priorityField.getText().trim();
        if (!priorityName.isEmpty()) {
            Priority priority = new Priority(priorityName);
            try {
                PriorityController.addPriority(priority);
                priorityList.add(priority);
                priorityField.clear();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private static void editPriority(ListView<Priority> priorityListView) {
        Priority selectedPriority = priorityListView.getSelectionModel().getSelectedItem();
        if (selectedPriority != null) {
        	if ("Default".equals(selectedPriority.getName())) {
                // If the task priority is "Default", do not allow editing
                return;
            }
        	
            TextInputDialog editDialog = new TextInputDialog(selectedPriority.getName());
            editDialog.setHeaderText("Edit Priority Name");
            editDialog.setContentText("New Name:");
            String newPriorityName = editDialog.showAndWait().orElse(selectedPriority.getName()).trim();

            if (!newPriorityName.isEmpty() && !newPriorityName.equals(selectedPriority.getName())) {
                try {
                    PriorityController.updatePriority(selectedPriority.getName(), newPriorityName);
                    selectedPriority.setName(newPriorityName);
                    priorityListView.refresh();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private static void deletePriority(ListView<Priority> priorityListView, ObservableList<Priority> priorityList) {
        Priority selectedPriority = priorityListView.getSelectionModel().getSelectedItem();
        if (selectedPriority != null) {
        	if ("Default".equals(selectedPriority.getName())) {
                // If the task priority is "Default", do not allow deletion
                return;
            }
            try {
                PriorityController.deletePriority(selectedPriority);
                priorityList.remove(selectedPriority);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
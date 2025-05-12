package com.taskmanager.gui;

import java.io.IOException;
import java.util.List;

import com.taskmanager.controllers.CategoryController;
import com.taskmanager.models.Category;

import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

public class CategoryUI {

    public static Pane getView() {
        VBox layout = new VBox(10);
        
        Label categoryHeader = new Label("Category Name");

        ListView<String> categoryList = new ListView<>();
        TextField categoryField = new TextField();
        Button addCategoryButton = new Button("Add Category");
        Button editCategoryButton = new Button("Edit Category");
        Button deleteCategoryButton = new Button("Delete Category");

        populateCategoryList(categoryList);
        addCategoryButton.setOnAction(event -> addCategory(categoryField, categoryList));
        editCategoryButton.setOnAction(event -> editCategory(categoryList));
        deleteCategoryButton.setOnAction(event -> deleteCategory(categoryList));

        layout.getChildren().addAll(
                categoryHeader,
                categoryList,
                categoryField,
                new HBox(10, addCategoryButton, editCategoryButton, deleteCategoryButton)
        );
        layout.setStyle("-fx-padding: 10;");
        
        return layout;
    }

    private static void populateCategoryList(ListView<String> categoryList) {
        try {
            List<Category> categories = CategoryController.loadCategories();
            for (Category category : categories) {
                categoryList.getItems().add(category.getName());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void addCategory(TextField categoryField, ListView<String> categoryList) {
        String categoryName = categoryField.getText().trim();
        if (!categoryName.isEmpty()) {
            Category category = new Category(categoryName);
            try {
                CategoryController.addCategory(category);
                categoryList.getItems().add(category.getName());
                categoryField.clear();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private static void editCategory(ListView<String> categoryList) {
        String selectedCategoryName = categoryList.getSelectionModel().getSelectedItem();
        if (selectedCategoryName != null) {
            TextInputDialog editDialog = new TextInputDialog(selectedCategoryName);
            editDialog.setHeaderText("Edit Category Name");
            editDialog.setContentText("New Name:");
            String newCategoryName = editDialog.showAndWait().orElse(selectedCategoryName).trim();

            if (!newCategoryName.isEmpty() && !newCategoryName.equals(selectedCategoryName)) {
                try {
                    CategoryController.updateCategory(selectedCategoryName, newCategoryName);
                    categoryList.getItems().set(categoryList.getSelectionModel().getSelectedIndex(), newCategoryName);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private static void deleteCategory(ListView<String> categoryList) {
        String selectedCategoryName = categoryList.getSelectionModel().getSelectedItem();
        if (selectedCategoryName != null) {
            try {
                CategoryController.deleteCategory(new Category(selectedCategoryName));
                categoryList.getItems().remove(selectedCategoryName);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
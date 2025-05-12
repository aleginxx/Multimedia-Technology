package com.taskmanager.controllers;

import com.taskmanager.models.*;
import com.taskmanager.utils.JSONHandler;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

public class CategoryController {
    private static final String CATEGORIES_FILE = "medialab/categories.json";
    private static final String REMINDERS_FILE = "medialab/reminders.json";
    
    public static List<String> loadCategoryNames() throws IOException {
        List<Category> categories = loadCategories();
        return categories.stream().map(Category::getName).collect(Collectors.toList());
    }

    public static List<Category> loadCategories() throws IOException {
        return JSONHandler.readListFromFile(CATEGORIES_FILE, new com.fasterxml.jackson.core.type.TypeReference<List<Category>>() {});
    }

    public static void addCategory(Category category) throws IOException {
        List<Category> categories = loadCategories();
        categories.add(category);
        JSONHandler.writeListToFile(CATEGORIES_FILE, categories);
    }

    public static void updateCategory(String originalCategoryName, String updatedCategoryName) throws IOException {
        List<Category> categories = loadCategories();
        List<Task> tasks = TaskController.loadTasks();

        categories.stream()
                .filter(category -> category.getName().equals(originalCategoryName))
                .forEach(category -> category.setName(updatedCategoryName));

        tasks.stream()
                .filter(task -> task.getCategory().equals(originalCategoryName))
                .forEach(task -> task.setCategory(updatedCategoryName));

        JSONHandler.writeListToFile(CATEGORIES_FILE, categories);
        JSONHandler.writeListToFile(TaskController.getTasksFilePath(), tasks);
    }

    public static void deleteCategory(Category category) throws IOException {
        List<Category> categories = loadCategories();
        List<Task> tasks = TaskController.loadTasks();
        List<Reminder> reminders = ReminderController.loadReminders();  

        boolean categoryRemoved = categories.removeIf(c -> c.getName().equals(category.getName()));
        
        // If the category was removed, remove associated tasks and reminders
        if (categoryRemoved) {
            for (Task task : tasks) {
                if (task.getCategory().equals(category.getName())) {
                    TaskController.deleteTask(task);
                }
            }

            JSONHandler.writeListToFile(CATEGORIES_FILE, categories);
            System.out.println("Category and associated tasks and reminders have been deleted.");
        }
    }
}
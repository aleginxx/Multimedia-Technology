package com.taskmanager.controllers;

import java.io.IOException;
import java.util.List;

import com.fasterxml.jackson.core.type.TypeReference;
import com.taskmanager.models.Reminder;
import com.taskmanager.models.Task;
import com.taskmanager.utils.JSONHandler;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

public class TaskController {
    private static final String TASKS_FILE = "medialab/tasks.json";
    private static final String REMINDERS_FILE = "medialab/reminders.json";

    public static String getTasksFilePath() {
        return TASKS_FILE;
    }

    public static List<Task> loadTasks() throws IOException {
        List<Task> tasks = JSONHandler.readListFromFile(TASKS_FILE, new TypeReference<>() {});
        // Update status for overdue tasks
        for (Task task : tasks) {
            task.updateStatusIfOverdue();
        }
        JSONHandler.writeListToFile(TASKS_FILE, tasks);
        return tasks;
    }
    
    public static Task getTaskByID(String taskId) throws IOException {
        List<Task> tasks = loadTasks();
        return tasks.stream()
                    .filter(task -> task.getTaskId().equals(taskId))
                    .findFirst()
                    .orElse(null); 
    }
    
    public static Task getTaskByTitle(String title) throws IOException {
        List<Task> tasks = loadTasks();
        return tasks.stream()
                    .filter(task -> task.getTitle().equals(title))
                    .findFirst()
                    .orElse(null); 
    }

    public static void addTask(Task task) throws IOException {
        // Check if category is provided
        if (task.getCategory() == null || task.getCategory().isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Category Required");
            alert.setHeaderText("Category is required.");
            alert.setContentText("Please choose a category for the task.");
            alert.showAndWait();
            return; 
        }

        // Set default values for priority and status
        if (task.getPriority() == null || task.getPriority().isEmpty()) {
            task.setPriority("Optional");  
        }

        if (task.getStatus() == null || task.getStatus().isEmpty()) {
            task.setStatus(Task.STATUS_OPEN); 
        }

        List<Task> tasks = loadTasks();
        
        // Check if a task with the same title already exists
        boolean taskExists = tasks.stream().anyMatch(existingTask -> existingTask.getTitle().equals(task.getTitle()));
        
        if (taskExists) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Duplicate Task Warning");
            alert.setHeaderText("This task already exists.");
            alert.setContentText("Are you sure you want to add another task with the same name?");
            
            if (alert.showAndWait().get() == ButtonType.OK) {
                tasks.add(task);
                JSONHandler.writeListToFile(TASKS_FILE, tasks);
                System.out.println("Task added successfully: " + task);
            } else {
                System.out.println("Task not added: Task with this title already exists.");
            }
        } else {
            tasks.add(task);
            JSONHandler.writeListToFile(TASKS_FILE, tasks);
            System.out.println("Task added successfully: " + task);
        }
    }

    public static void updateTask(String taskId, Task selectedTask) throws IOException {
        List<Task> tasks = loadTasks();
        List<Reminder> reminders = JSONHandler.readListFromFile(REMINDERS_FILE, new TypeReference<>() {});

        for (Task task : tasks) {
            if (task.getTaskId().equals(taskId)) { 
            	
            	for (Reminder reminder : reminders) {
//                	System.out.println("Checking for reminders");
                    if (reminder.getTaskTitle().equals(task.getTitle())) {
//                        System.out.println("Original Title '" + reminder.getTaskTitle() + "' New TItle '" + selectedTask.getTitle());
                        reminder.setTaskTitle(selectedTask.getTitle());  
                    }
                }
            	
                task.setTaskId(taskId);
                task.setTitle(selectedTask.getTitle());
                task.setDescription(selectedTask.getDescription());
                task.setCategory(selectedTask.getCategory());
                task.setPriority(selectedTask.getPriority());
                task.setDueDate(selectedTask.getDueDate());

                if (isValidStatus(selectedTask.getStatus())) {
                    task.setStatus(selectedTask.getStatus());

                    // If task is marked as Completed, remove associated reminders
                    if (Task.STATUS_COMPLETED.equals(selectedTask.getStatus())) {
                        reminders.removeIf(reminder -> reminder.getTaskTitle().equals(task.getTitle()));
                        JSONHandler.writeListToFile(REMINDERS_FILE, reminders);
                        System.out.println("All reminders for task '" + task.getTitle() + "' have been deleted.");
                    }
                }
                break;
            }
        }

        JSONHandler.writeListToFile(REMINDERS_FILE, reminders);
        JSONHandler.writeListToFile(TASKS_FILE, tasks);
    }

    public static void deleteTask(Task task) throws IOException {
        List<Task> tasks = loadTasks();
        // Remove associated reminders
        List<Reminder> reminders = JSONHandler.readListFromFile(REMINDERS_FILE, new TypeReference<>() {});
        reminders.removeIf(reminder -> reminder.getTaskTitle().equals(task.getTitle()));
        JSONHandler.writeListToFile(REMINDERS_FILE, reminders);

        tasks.removeIf(existingTask -> existingTask.getTaskId().equals(task.getTaskId())); 
        JSONHandler.writeListToFile(TASKS_FILE, tasks);
    }

    private static boolean isValidStatus(String status) {
        return Task.STATUS_OPEN.equals(status) ||
               Task.STATUS_IN_PROGRESS.equals(status) ||
               Task.STATUS_POSTPONED.equals(status) ||
               Task.STATUS_COMPLETED.equals(status);
    }
}

package com.taskmanager.controllers;

import com.fasterxml.jackson.core.type.TypeReference;
import com.taskmanager.models.*;
import com.taskmanager.utils.JSONHandler;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

public class PriorityController {
    private static final String PRIORITIES_FILE = "medialab/priorities.json";
    private static final String DEFAULT_PRIORITY = "Default";  

    public static List<String> loadPriorityNames() throws IOException {
        List<Priority> priorities = loadPriorities();
        return priorities.stream().map(Priority::getName).collect(Collectors.toList());
    }

    public static List<Priority> loadPriorities() throws IOException {
        return JSONHandler.readListFromFile(PRIORITIES_FILE, new TypeReference<>() {});
    }

    public static void addPriority(Priority priority) throws IOException {
        List<Priority> priorities = loadPriorities();
        priorities.add(priority);
        JSONHandler.writeListToFile(PRIORITIES_FILE, priorities);
    }

    public static void updatePriority(String originalPriorityName, String updatedPriorityName) throws IOException {
        List<Priority> priorities = loadPriorities();
        List<Task> tasks = TaskController.loadTasks();

        priorities.stream()
                .filter(priority -> priority.getName().equals(originalPriorityName))
                .findFirst()
                .ifPresent(priority -> priority.setName(updatedPriorityName));

        tasks.stream()
                .filter(task -> task.getPriority().equals(originalPriorityName))
                .forEach(task -> task.setPriority(updatedPriorityName));

        if (priorities.stream().anyMatch(priority -> priority.getName().equals(updatedPriorityName))) {
            JSONHandler.writeListToFile(PRIORITIES_FILE, priorities);
        }
        if (tasks.stream().anyMatch(task -> task.getPriority().equals(updatedPriorityName))) {
            JSONHandler.writeListToFile(TaskController.getTasksFilePath(), tasks);
        }
    }

    public static void deletePriority(Priority priorityToDelete) throws IOException {
        // Prevent deletion of 'Default' priority
        if (DEFAULT_PRIORITY.equals(priorityToDelete.getName())) {
            System.out.println("The 'Default' priority cannot be deleted.");
            return;
        }

        List<Priority> priorities = loadPriorities();
        List<Task> tasks = TaskController.loadTasks();
        priorities.removeIf(priority -> priority.getName().equals(priorityToDelete.getName()));

        // Assign 'Default' priority to all tasks that had the deleted priority
        tasks.stream()
             .filter(task -> task.getPriority().equals(priorityToDelete.getName()))
             .forEach(task -> task.setPriority(DEFAULT_PRIORITY));

        if (!priorities.isEmpty()) {
            JSONHandler.writeListToFile(PRIORITIES_FILE, priorities);
        }
        if (!tasks.isEmpty()) {
            JSONHandler.writeListToFile(TaskController.getTasksFilePath(), tasks);
        }

        System.out.println("Priority '" + priorityToDelete.getName() + "' has been deleted and tasks have been reassigned to 'Default'.");
    }
}
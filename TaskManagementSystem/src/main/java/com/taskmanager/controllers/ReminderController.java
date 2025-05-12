package com.taskmanager.controllers;

import com.taskmanager.models.*;
import com.taskmanager.utils.JSONHandler;

import java.io.IOException;
import java.util.List;

public class ReminderController {
    private static final String REMINDERS_FILE = "medialab/reminders.json";
    private static final String TASKS_FILE = "medialab/tasks.json"; 

    public static List<Reminder> loadReminders() throws IOException {
        return JSONHandler.readListFromFile(REMINDERS_FILE, new com.fasterxml.jackson.core.type.TypeReference<>() {});
    }

    public static List<Task> loadTasks() throws IOException {
        return JSONHandler.readListFromFile(TASKS_FILE, new com.fasterxml.jackson.core.type.TypeReference<>() {});
    }

    public static void addReminder(Reminder reminder) throws IOException {
        List<Task> tasks = loadTasks();
        Task relatedTask = tasks.stream()
                .filter(task -> task.getTitle().equals(reminder.getTaskTitle()))
                .findFirst()
                .orElse(null);

        if (relatedTask == null) {
            throw new IllegalArgumentException("Task not found for the given reminder.");
        }

        if (relatedTask.getStatus().equalsIgnoreCase(Task.STATUS_COMPLETED)) {
            throw new IllegalArgumentException("Cannot set a reminder for a completed task.");
        }

        List<Reminder> reminders = loadReminders();
        reminders.add(reminder);
        JSONHandler.writeListToFile(REMINDERS_FILE, reminders);
    }

    public static void updateReminder(Reminder updatedReminder) throws IOException {
        List<Reminder> reminders = loadReminders();
        boolean reminderUpdated = false;

        for (Reminder reminder : reminders) {
            if (reminder.getTaskTitle().equals(updatedReminder.getTaskTitle())) {
                reminder.setReminderDate(updatedReminder.getReminderDate());
                reminderUpdated = true;
                break;
            }
        }

        if (reminderUpdated) {
            JSONHandler.writeListToFile(REMINDERS_FILE, reminders);
            System.out.println("Reminder updated successfully in reminders.json: " + updatedReminder);
        }
    }

    public static void deleteReminder(Reminder reminder) throws IOException {
        List<Reminder> reminders = loadReminders();
        reminders.remove(reminder);
        JSONHandler.writeListToFile(REMINDERS_FILE, reminders);
    }
}
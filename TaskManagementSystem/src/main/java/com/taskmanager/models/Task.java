package com.taskmanager.models;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Task {
    private String taskId;
    private String title;
    private String description;
    private String category;
    private String priority;
    private LocalDate dueDate;
    private String status;
    private List<LocalDate> reminders;

    public static final String STATUS_OPEN = "Open";
    public static final String STATUS_IN_PROGRESS = "In Progress";
    public static final String STATUS_POSTPONED = "Postponed";
    public static final String STATUS_COMPLETED = "Completed";
    public static final String STATUS_DELAYED = "Delayed";
    
    public Task() {
        this.taskId = generateTaskId();  // Ensure unique taskId for each new task
        this.title = "New Task";
        this.description = "Default";
        this.category = "Default";
        this.priority = "Default";
        this.dueDate = LocalDate.now();
        this.status = STATUS_OPEN;
        this.reminders = new ArrayList<>();
    }

    public Task(String title, String description, String category, String priority, LocalDate dueDate) {
        this.taskId = generateTaskId();  // Ensure unique taskId for each new task
        this.title = title;
        this.description = description;
        this.category = category;
        this.priority = priority;
        this.dueDate = dueDate;
        this.status = STATUS_OPEN;
        this.reminders = new ArrayList<>();
    }

    // Generate a unique taskId using UUID
    private String generateTaskId() {
        return UUID.randomUUID().toString();
    }

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<LocalDate> getReminders() {
        return reminders;
    }

    public void addReminder(LocalDate reminderDate) {
        this.reminders.add(reminderDate);
    }

    public void removeReminder(LocalDate reminderDate) {
        this.reminders.remove(reminderDate);
    }

    public void updateStatusIfOverdue() {
        if (!STATUS_COMPLETED.equals(this.status) && LocalDate.now().isAfter(this.dueDate)) {
            this.status = STATUS_DELAYED;
        }
    }

    @Override
    public String toString() {
        return "Task{" +
                "taskId='" + taskId + '\'' +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", category='" + category + '\'' +
                ", priority='" + priority + '\'' +
                ", dueDate=" + dueDate +
                ", status='" + status + '\'' +
                ", reminders=" + reminders +
                '}';
    }
}

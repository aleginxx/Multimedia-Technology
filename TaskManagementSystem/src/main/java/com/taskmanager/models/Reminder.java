package com.taskmanager.models;

import java.time.LocalDate;
import java.util.Objects;

public class Reminder {
    private String taskTitle; 
    private LocalDate reminderDate;
    
    public Reminder() {
        // Default constructor for Jackson
    }

    public Reminder(String taskTitle, LocalDate reminderDate) {
        if (taskTitle == null || taskTitle.trim().isEmpty()) {
            throw new IllegalArgumentException("Task title cannot be null or empty.");
        }
        if (reminderDate == null || reminderDate.isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("Reminder date cannot be null or in the past.");
        }

        this.taskTitle = taskTitle;
        this.reminderDate = reminderDate;
    }

    public String getTaskTitle() {
        return taskTitle;
    }

    public void setTaskTitle(String taskTitle) {
        if (taskTitle == null || taskTitle.trim().isEmpty()) {
            throw new IllegalArgumentException("Task title cannot be null or empty.");
        }
        this.taskTitle = taskTitle;
    }

    public LocalDate getReminderDate() {
        return reminderDate;
    }

    public void setReminderDate(LocalDate reminderDate) {
        if (reminderDate == null || reminderDate.isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("Reminder date cannot be null or in the past.");
        }
        this.reminderDate = reminderDate;
    }

    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Reminder reminder = (Reminder) o;
        return Objects.equals(taskTitle, reminder.taskTitle) &&
                Objects.equals(reminderDate, reminder.reminderDate);
    }

    public int hashCode() {
        return Objects.hash(taskTitle, reminderDate);
    }

    public String toString() {
        return "Reminder{" +
                "taskTitle='" + taskTitle + '\'' +
                ", reminderDate=" + reminderDate +
                '}';
    }
}
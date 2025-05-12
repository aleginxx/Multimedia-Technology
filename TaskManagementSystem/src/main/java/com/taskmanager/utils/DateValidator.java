package com.taskmanager.utils;

import java.time.LocalDate;

public class DateValidator {

    public static boolean isValidFutureDate(LocalDate date) {
        return date != null && !date.isBefore(LocalDate.now());
    }

    public static boolean isValidReminderDate(LocalDate reminderDate, LocalDate dueDate) {
        if (reminderDate == null || dueDate == null) {
            return false;
        }
        return !reminderDate.isBefore(LocalDate.now()) && !reminderDate.isAfter(dueDate);
    }

    public static boolean isOverdue(LocalDate date) {
        return date != null && date.isBefore(LocalDate.now());
    }
}
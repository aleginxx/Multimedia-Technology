package com.taskmanager;

import com.fasterxml.jackson.core.type.TypeReference;
import com.taskmanager.gui.MainUI;
import com.taskmanager.models.*;
import com.taskmanager.utils.JSONHandler;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Main {
    private static final String TASKS_FILE = "medialab/tasks.json";
    private static final String CATEGORIES_FILE = "medialab/categories.json";
    private static final String PRIORITIES_FILE = "medialab/priorities.json";
    private static final String REMINDERS_FILE = "medialab/reminders.json";

    private static final Logger LOGGER = Logger.getLogger(Main.class.getName());

    public static void main(String[] args) {
        try {
            List<Task> tasks = loadData(TASKS_FILE, new ArrayList<>(), new TypeReference<List<Task>>() {});
            List<Category> categories = loadData(CATEGORIES_FILE, List.of(new Category("Default")), new TypeReference<List<Category>>() {});
            List<Priority> priorities = loadData(PRIORITIES_FILE, List.of(new Priority("Default")), new TypeReference<List<Priority>>() {});
            List<Reminder> reminders = loadData(REMINDERS_FILE, List.of(new Reminder("Default", LocalDate.now())), new TypeReference<List<Reminder>>() {});

            LOGGER.info("Data Loaded Successfully");
            LOGGER.info("Tasks: " + tasks);
            LOGGER.info("Categories: " + categories);
            LOGGER.info("Priorities: " + priorities);
            LOGGER.info("Reminders: " + reminders);

            // Start GUI
            MainUI.main(args);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error initializing the system", e);
        }
    }

    private static <T> List<T> loadData(String filePath, List<T> defaultData, TypeReference<List<T>> typeRef) throws IOException {
        File file = new File(filePath);

        if (!file.exists()) {
            LOGGER.warning("File not found: " + filePath + ". Initializing with default data.");
            JSONHandler.writeListToFile(filePath, defaultData);
            return defaultData;
        }

        try {
            LOGGER.info("Loading data from file: " + file.getAbsolutePath());
            return JSONHandler.readListFromFile(filePath, typeRef);
        } catch (IOException e) {
            LOGGER.log(Level.WARNING, "Error reading file: " + filePath + ". Initializing with default data.", e);
            JSONHandler.writeListToFile(filePath, defaultData);
            return defaultData;
        }
    }
}

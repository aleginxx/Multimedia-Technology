package com.taskmanager.models;

import java.util.Objects;

public class Priority {
    private String name;
    
    public Priority() {
        // Default constructor for Jackson
    }

    public Priority(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Priority name cannot be null or empty.");
        }
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Priority name cannot be null or empty.");
        }
        this.name = name;
    }

    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Priority priority = (Priority) o;
        return Objects.equals(name, priority.name);
    }

    public int hashCode() {
        return Objects.hash(name);
    }

    public String toString() {
        return "Priority{" +
                "name='" + name + '\'' +
                '}';
    }
}
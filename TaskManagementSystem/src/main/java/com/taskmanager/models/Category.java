package com.taskmanager.models;

import java.util.Objects;

public class Category {
    private String name;
    
    public Category() {
    	this.name = "New Category";
    }
    
    public Category(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Category name cannot be null or empty.");
        }
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Category name cannot be null or empty.");
        }
        this.name = name;
    }

    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Category category = (Category) o;
        return Objects.equals(name, category.name);
    }

    public int hashCode() {
        return Objects.hash(name);
    }

    public String toString() {
        return "Category{" +
                "name='" + name + '\'' +
                '}';
    }
}
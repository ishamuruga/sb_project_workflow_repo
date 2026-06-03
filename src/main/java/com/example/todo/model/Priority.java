package com.example.todo.model;

import com.fasterxml.jackson.annotation.JsonCreator;

/**
 * Priority levels for Todo items.
 * LOW (1) < MEDIUM (2) < HIGH (3)
 */
public enum Priority {
    LOW(1, "Low"),
    MEDIUM(2, "Medium"),
    HIGH(3, "High");

    private final int level;
    private final String displayName;

    Priority(int level, String displayName) {
        this.level = level;
        this.displayName = displayName;
    }

    public int getLevel() {
        return level;
    }

    public String getDisplayName() {
        return displayName;
    }

    @JsonCreator
    public static Priority fromValue(String value) {
        if (value == null || value.isEmpty()) {
            return MEDIUM; // Default priority
        }
        return Priority.valueOf(value.toUpperCase());
    }
}

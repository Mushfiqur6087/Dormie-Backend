package com.HMS.hms.enums;

public enum UserRole {
    STUDENT("STUDENT"),
    HALL_MANAGER("HALL_MANAGER"),
    ADMIN("ADMIN"),
    AUTHORITY("AUTHORITY"),
    SUPERVISOR("SUPERVISOR");

    private final String value;

    UserRole(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static UserRole fromString(String value) {
        if (value == null) {
            return STUDENT; // Default role
        }
        
        for (UserRole role : UserRole.values()) {
            if (role.value.equalsIgnoreCase(value) || role.name().equalsIgnoreCase(value)) {
                return role;
            }
        }
        
        // Handle legacy string values
        return switch (value.toLowerCase()) {
            case "admin" -> ADMIN;
            case "hall_manager", "hallmanager" -> HALL_MANAGER;
            case "student" -> STUDENT;
            case "authority" -> AUTHORITY;
            case "supervisor" -> SUPERVISOR;
            default -> STUDENT; // Default to student if unknown role
        };
    }

    @Override
    public String toString() {
        return value;
    }
}

// File: src/main/java/com/smartoffice/facility/core/User.java
package com.smartoffice.facility.core;

/**
 * Represents a user of the Smart Office Facility system, containing identity,
 * authorization, and credentials, including a unique Manager ID for elevated security.
 */
public class User {
    private final String username;
    private final String userId;
    private final String password;
    private final boolean isAdmin;
    private final String managerId; // NEW: Field to store the manager's unique ID for secondary check

    /**
     * Primary constructor used by the UserBuilder to finalize the object.
     */
    public User(String username, String userId, String password, boolean isAdmin, String managerId) {
        this.username = username;
        this.userId = userId;
        this.password = password;
        this.isAdmin = isAdmin;
        this.managerId = managerId; // Initialized here
    }

    // --- Getters for Authentication and Display ---

    public String getUsername() {
        return username;
    }

    public String getUserId() {
        return userId;
    }

    public String getPassword() {
        return password;
    }

    public boolean isAdmin() {
        return isAdmin;
    }

    /**
     * NEW GETTER: Retrieves the unique manager ID for the secondary security check.
     */
    public String getManagerId() {
        return managerId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        // Identity is based on the unique ID
        return userId.equals(user.userId);
    }

    @Override
    public int hashCode() {
        return userId.hashCode();
    }

    @Override
    public String toString() {
        return username + " (ID: " + userId + ")";
    }
}
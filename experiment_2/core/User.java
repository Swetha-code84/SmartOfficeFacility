
package com.smartoffice.facility.core;

public class User {
    private final String username;
    private final String userId;
    private final String password;
    private final boolean isAdmin;
    private final String managerId; 

    public User(String username, String userId, String password, boolean isAdmin, String managerId) {
        this.username = username;
        this.userId = userId;
        this.password = password;
        this.isAdmin = isAdmin;
        this.managerId = managerId; 
    }

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

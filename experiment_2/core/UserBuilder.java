package com.smartoffice.facility.core;

public class UserBuilder {
    
    private final String username;
    private final String userId;
    private final String password;

    private boolean isAdmin = false;
    private String managerId = null; 

    public UserBuilder(String username, String userId, String password) {
        this.username = username;
        this.userId = userId;
        this.password = password;
    }

    public UserBuilder asAdmin() {
        this.isAdmin = true;
        return this;
    }

    public UserBuilder withManagerId(String managerId) {
        this.managerId = managerId;
        return this;
    }

    public User build() {
        return new User(username, userId, password, isAdmin, managerId);
    }
}

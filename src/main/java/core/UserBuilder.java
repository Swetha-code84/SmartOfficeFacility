package com.smartoffice.facility.core;

/**
 * Creational Pattern: Builder. Manages the step-by-step construction of the User object,
 * enforcing initial mandatory fields (username, userId, password) required for registration
 * and optional security/role fields (isAdmin, managerId).
 */
public class UserBuilder {

    // --- Mandatory Fields (Set in Constructor) ---
    private final String username;
    private final String userId;
    private final String password;

    // --- Optional/Default Fields (Set via Fluent Methods) ---
    private boolean isAdmin = false;
    private String managerId = null; // NEW: Field for the Manager's unique ID

    /**
     * Constructor must take the minimum mandatory fields required for registration.
     */
    public UserBuilder(String username, String userId, String password) {
        this.username = username;
        this.userId = userId;
        this.password = password;
    }

    /**
     * Fluent interface method to set the optional admin status.
     */
    public UserBuilder asAdmin() {
        this.isAdmin = true;
        return this;
    }

    /**
     * NEW FLUENT METHOD: Sets the unique Manager ID for the secondary security check.
     * This is crucial for satisfying the new manager access requirement.
     */
    public UserBuilder withManagerId(String managerId) {
        this.managerId = managerId;
        return this;
    }

    /**
     * Finalizes and returns the immutable User object.
     * The arguments MUST match the updated User.java constructor (including managerId).
     */
    public User build() {
        // FIX: Pass the new managerId field to the User constructor.
        return new User(username, userId, password, isAdmin, managerId);
    }
}
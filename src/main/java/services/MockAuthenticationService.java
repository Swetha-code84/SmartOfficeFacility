package com.smartoffice.facility.services;

import com.smartoffice.facility.core.User;
import com.smartoffice.facility.core.UserBuilder; // Dependency for Builder Pattern
import com.smartoffice.facility.interfaces.IAuthenticationService;
import com.smartoffice.facility.main.AppConstants;

import java.util.HashMap;
import java.util.Map;

/**
 * A mock implementation of the IAuthenticationService that handles user registration,
 * persistence (in-memory map), and unique ID/password checking.
 */
public class MockAuthenticationService implements IAuthenticationService {

    // Persistent storage for all registered users (Key: Username)
    private final Map<String, User> userDatabase;
    private User currentlyLoggedInUser = null;

    public MockAuthenticationService() {
        this.userDatabase = new HashMap<>();
        // Initialize the mandatory admin user and the specialized manager user
        initializeRoles();
    }

    /**
     * Initializes all hardcoded system roles (Admin and Manager) using the Builder.
     */
    private void initializeRoles() {
        // --- 1. ADMIN USER (For configuration access) ---
        User admin = new UserBuilder(
                AppConstants.DEFAULT_ADMIN_USERNAME,
                "ADMIN001",
                AppConstants.DEFAULT_ADMIN_PASSWORD
        ).asAdmin().build();
        userDatabase.put(AppConstants.DEFAULT_ADMIN_USERNAME, admin);

        // --- 2. MANAGER USER (For statistics access - Requires Manager ID check) ---
        // This user is the target for the ManagerViewStatusCommand's strict check.
        User managerUser = new UserBuilder(
                "manager",
                "MGR001", // Unique User ID
                "mgrpass" // Password for login
        ).withManagerId("MGR_SECURE_KEY_77").build(); // Manager ID for 2nd factor check

        userDatabase.put("manager", managerUser);
    }

    // ------------------------------------------------------------------------
    // REGISTRATION LOGIC
    // ------------------------------------------------------------------------

    /**
     * Implements the registration logic required by the interface.
     */
    @Override
    public User registerUser(String username, String userId, String password) {
        // 1. Check if username or userId already exists
        boolean exists = userDatabase.containsKey(username) ||
                userDatabase.values().stream().anyMatch(u -> u.getUserId().equals(userId));

        if (exists) {
            return null; // Registration failed: user already exists
        }

        // 2. CREATIONAL PATTERN: BUILDER - Construct the new standard user object
        User newUser = new UserBuilder(username, userId, password)
                .build();

        // 3. Store user in the mock database
        userDatabase.put(username, newUser);
        return newUser;
    }

    // ------------------------------------------------------------------------
    // AUTHENTICATION LOGIC
    // ------------------------------------------------------------------------

    /**
     * Authenticates a user by checking the username against the mock database
     * and verifying the stored password.
     */
    @Override
    public User authenticate(String username, String password) {
        User user = userDatabase.get(username);

        if (user != null && user.getPassword().equals(password)) {
            // Success: Password matches the stored password
            this.currentlyLoggedInUser = user;
            return user;
        }

        // Failure: User not found or password incorrect
        return null;
    }

    // ------------------------------------------------------------------------
    // AUTHORIZATION/CURRENT USER
    // ------------------------------------------------------------------------

    @Override
    public boolean isAdmin(User user) {
        return user != null && user.isAdmin();
    }

    @Override
    public User getCurrentUser() {
        return this.currentlyLoggedInUser;
    }
}
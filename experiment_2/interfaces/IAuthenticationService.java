package com.smartoffice.facility.interfaces;

import com.smartoffice.facility.core.User;

/**
 * Interface for authentication and authorization services.
 * Adheres to Dependency Inversion Principle (DIP).
 */
public interface IAuthenticationService {

    /**
     * Authenticates a user based on credentials.
     */
    User authenticate(String username, String password);

    /**
     * Checks if a user has the necessary administrative privileges.
     */
    boolean isAdmin(User user);

    /**
     * Gets the currently authenticated user session object.
     */
    User getCurrentUser();

    /**
     * NEW REQUIREMENT: Registers a new user with a unique username, ID, and password.
     * @return The newly created User object if registration succeeds, or null otherwise.
     */
    User registerUser(String username, String userId, String password);
}
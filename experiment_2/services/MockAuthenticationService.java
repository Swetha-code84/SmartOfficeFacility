package com.smartoffice.facility.services;

import com.smartoffice.facility.core.User;
import com.smartoffice.facility.core.UserBuilder; 
import com.smartoffice.facility.interfaces.IAuthenticationService;
import com.smartoffice.facility.main.AppConstants;

import java.util.HashMap;
import java.util.Map;

public class MockAuthenticationService implements IAuthenticationService {

    private final Map<String, User> userDatabase;
    private User currentlyLoggedInUser = null;

    public MockAuthenticationService() {
        this.userDatabase = new HashMap<>();
        initializeRoles();
    }

    private void initializeRoles() {
        User admin = new UserBuilder(
                AppConstants.DEFAULT_ADMIN_USERNAME,
                "ADMIN001",
                AppConstants.DEFAULT_ADMIN_PASSWORD
        ).asAdmin().build();
        userDatabase.put(AppConstants.DEFAULT_ADMIN_USERNAME, admin);

        User managerUser = new UserBuilder(
                "manager",
                "MGR001", 
                "mgrpass" 
        ).withManagerId("MGR_SECURE_KEY_77").build(); 

        userDatabase.put("manager", managerUser);
    }

    @Override
    public User registerUser(String username, String userId, String password) {
        boolean exists = userDatabase.containsKey(username) ||
                userDatabase.values().stream().anyMatch(u -> u.getUserId().equals(userId));

        if (exists) {
            return null; // Registration failed: user already exists
        }
        User newUser = new UserBuilder(username, userId, password)
                .build();
        
        userDatabase.put(username, newUser);
        return newUser;
    }

    @Override
    public User authenticate(String username, String password) {
        User user = userDatabase.get(username);

        if (user != null && user.getPassword().equals(password)) {
            // Success: Password matches the stored password
            this.currentlyLoggedInUser = user;
            return user;
        }

        return null;
    }

    @Override
    public boolean isAdmin(User user) {
        return user != null && user.isAdmin();
    }

    @Override
    public User getCurrentUser() {
        return this.currentlyLoggedInUser;
    }
}

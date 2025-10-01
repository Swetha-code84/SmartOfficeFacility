package com.smartoffice.facility.commands;

import com.smartoffice.facility.core.User;
import com.smartoffice.facility.interfaces.IAuthenticationService;
import com.smartoffice.facility.interfaces.ICommand;
import com.smartoffice.facility.main.SmartOfficeApp;

import java.util.Scanner;

/**
 * Concrete Command: Handles the user login process.
 * This is a simple command that does not require prior authentication.
 */
public class LoginCommand implements ICommand {

    private final IAuthenticationService authService;
    private final Scanner scanner;
    private final SmartOfficeApp app; // Reference to the app to update the currentUser state

    /**
     * Constructor to match the usage in SmartOfficeApp.java.
     * RESOLVES ERROR: constructor LoginCommand cannot be applied to given types.
     */
    public LoginCommand(IAuthenticationService authService, Scanner scanner, SmartOfficeApp app) {
        this.authService = authService;
        this.scanner = scanner;
        this.app = app;
    }

    /**
     * Executes the login operation.
     * RESOLVES ERROR: execute() return type mismatch (implements boolean from ICommand).
     */
    @Override
    public boolean execute() {
        System.out.println("\n--- User Login ---");
        System.out.print("Enter Username: ");
        String username = scanner.nextLine().trim();

        System.out.print("Enter Password: ");
        String password = scanner.nextLine().trim();

        // 1. Authenticate User
        User user = authService.authenticate(username, password);

        if (user != null) {
            // 2. Update Application State
            app.setCurrentUser(user);
            System.out.println("✅ Login successful! Welcome, " + user.getUsername() + ".");
            return true;
        } else {
            System.out.println("❌ Login failed. Invalid username or password.");
            return false;
        }
    }
}
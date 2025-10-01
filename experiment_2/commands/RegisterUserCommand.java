// File: src/main/java/com/smartoffice/facility/commands/RegisterUserCommand.java
package com.smartoffice.facility.commands;

import com.smartoffice.facility.interfaces.IAuthenticationService;
import com.smartoffice.facility.interfaces.ICommand;
import java.util.Scanner;

/**
 * Handles the user registration process.
 */
public class RegisterUserCommand implements ICommand {
    private final IAuthenticationService authService;
    private final Scanner scanner;

    public RegisterUserCommand(IAuthenticationService authService, Scanner scanner) {
        this.authService = authService;
        this.scanner = scanner;
    }

    @Override
    public boolean execute() {
        System.out.println("\n--- 📝 New User Registration ---");
        System.out.print("Enter Username: ");
        String username = scanner.nextLine().trim();
        System.out.print("Enter User ID (unique): ");
        String userId = scanner.nextLine().trim();
        System.out.print("Enter Password: ");
        String password = scanner.nextLine().trim();

        // The MockAuthenticationService handles uniqueness checks now.
        if (authService.registerUser(username, userId, password) != null) {
            System.out.println("✅ Registration successful! Please log in.");
            return true;
        } else {
            System.out.println("❌ Registration failed. Username or User ID may already exist.");
            return false;
        }
    }
}
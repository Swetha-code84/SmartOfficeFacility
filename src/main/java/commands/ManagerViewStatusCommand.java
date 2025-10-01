package com.smartoffice.facility.commands;

import com.smartoffice.facility.core.User;
import com.smartoffice.facility.interfaces.IAuthenticationService;
import com.smartoffice.facility.interfaces.ICommand;
import com.smartoffice.facility.services.OfficeFacility;
import java.util.Scanner;

/**
 * Command dedicated to the Manager role for viewing statistics, featuring a
 * secondary ID check (two-factor authorization simulation).
 */
public class ManagerViewStatusCommand extends AuthenticatedCommand {

    private final StatusCommand statusCommand;
    private final Scanner scanner;

    public ManagerViewStatusCommand(OfficeFacility officeFacility, IAuthenticationService authService, Scanner scanner, User currentUser) {
        super(officeFacility, authService, currentUser);
        this.scanner = scanner;
        this.statusCommand = new StatusCommand(officeFacility);
    }

    @Override
    protected boolean authenticatedExecute() {
        // AUTH CHECK 1: Ensure the user is logged in (handled by AuthenticatedCommand)

        // CHECK 2: Specific Manager Role Check (by username)
        if (!currentUser.getUsername().equalsIgnoreCase("manager")) {
            System.out.println("❌ Access denied. This option is reserved for the 'manager' user role.");
            return false;
        }

        System.out.println("\n--- 🔒 MANAGER ID VERIFICATION ---");
        System.out.print("Enter Manager ID for authorization: ");
        String enteredManagerId = scanner.nextLine().trim();

        // CHECK 3: Manager ID Verification
        if (currentUser.getManagerId() != null && currentUser.getManagerId().equals(enteredManagerId)) {

            // Authorization Successful - Display Statistics
            System.out.println("✅ ID Verified. Loading Manager Dashboard...");
            System.out.println("----------------------------------------");
            return statusCommand.execute(); // Delegate to StatusCommand for display

        } else {
            // Failure: Incorrect ID
            System.out.println("❌ Command Failed: Invalid Manager ID or you are not the correct person.");
            return false;
        }
    }
}
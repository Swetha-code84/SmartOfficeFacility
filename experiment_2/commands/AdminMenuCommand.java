package com.smartoffice.facility.commands;

import com.smartoffice.facility.core.Room;
import com.smartoffice.facility.core.User;
import com.smartoffice.facility.interfaces.IAuthenticationService;
import com.smartoffice.facility.services.OfficeFacility;

import java.util.Scanner;

public class AdminMenuCommand extends AuthenticatedCommand {
    private final Scanner scanner;

    public AdminMenuCommand(OfficeFacility officeFacility, IAuthenticationService authService, Scanner scanner, User currentUser) {
        super(officeFacility, authService, currentUser);
        this.scanner = scanner;
    }

    @Override
    protected boolean authenticatedExecute() {
        if (!authService.isAdmin(currentUser)) {
            System.out.println("❌ Access denied. Only administrators can access configuration menu.");
            return false;
        }

        // --- NEW LOGIC: Initial Batch Configuration ---
        if (!officeFacility.isConfigured()) {
            System.out.println("\n--- 🚨 INITIAL OFFICE SETUP REQUIRED 🚨 ---");
            System.out.print("Enter the initial number of meeting rooms to create: ");
            if (initialConfigure()) {
                System.out.println("✅ Initial configuration complete. Accessing Maintenance Menu.");
            } else {
                return false; // Exit if initial setup fails
            }
        }

        // --- ONGOING MAINTENANCE MENU ---
        while (true) {
            displayAdminMenu();
            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1": // Add New Room
                    addSingleRoom();
                    break;
                case "2": // List All Rooms
                    listRooms();
                    break;
                case "3": // Set Room Max Capacity
                    setRoomCapacity();
                    break;
                case "4": // Add Occupants (Placeholder/Hint)
                    System.out.println("Use main menu option 6 to update occupants (sensor mock).");
                    break;
                case "0": // Return to Main Menu
                    return true;
                default:
                    System.out.println("❌ Invalid choice.");
            }
        }
    }

    /**
     * Handles the initial room count input and delegates to OfficeFacility.initialConfigure.
     */
    private boolean initialConfigure() {
        try {
            int numberOfRooms = Integer.parseInt(scanner.nextLine().trim());

            // FIX: Correctly call the renamed method for batch setup
            return officeFacility.initialConfigure(numberOfRooms);

        } catch (NumberFormatException e) {
            System.out.println("❌ Invalid number input. Initial configuration aborted.");
            return false;
        }
    }

    private void displayAdminMenu() {
        System.out.println("\n--- ⚙️ Admin Maintenance Menu ---");
        System.out.println("1. Add New Room");
        System.out.println("2. List All Rooms");
        System.out.println("3. Set Room Max Capacity");
        System.out.println("4. (View Main Menu Occupancy Option)");
        System.out.println("0. Return to Main Menu");
        System.out.print("Enter choice: ");
    }

    /**
     * New Feature 2: Add Room (Individual Room Addition)
     */
    private void addSingleRoom() {
        System.out.print("Enter NEW Room Number (must be unique): ");
        try {
            int newRoomNumber = Integer.parseInt(scanner.nextLine().trim());
            officeFacility.addSingleRoom(newRoomNumber);
        } catch (NumberFormatException e) {
            System.out.println("❌ Invalid room number.");
        }
    }

    /**
     * New Feature 3: List Rooms
     */
    private void listRooms() {
        System.out.println("\n--- Room Inventory (ID | Capacity | Status) ---");
        officeFacility.getAllRooms().forEach(room -> {
            System.out.printf("Room %d | Max Capacity: %d | Status: %s\n",
                    room.getRoomNumber(), room.getMaxCapacity(), room.getStatus());
        });
        System.out.println("----------------------------------------------");
    }

    /**
     * New Feature 1: Set Room MaxCapacity
     */
    private void setRoomCapacity() {
        System.out.print("Enter Room Number to modify capacity: ");
        try {
            int roomNumber = Integer.parseInt(scanner.nextLine().trim());
            Room room = officeFacility.getRoom(roomNumber);

            if (room == null) {
                System.out.println("❌ Room " + roomNumber + " not found.");
                return;
            }

            System.out.print("Enter NEW Max Capacity: ");
            int newCapacity = Integer.parseInt(scanner.nextLine().trim());

            if (newCapacity <= 0) {
                System.out.println("❌ Capacity must be positive.");
                return;
            }

            room.setMaxCapacity(newCapacity);
            System.out.println("✅ Room " + roomNumber + " capacity set to " + newCapacity + ".");
        } catch (NumberFormatException e) {
            System.out.println("❌ Invalid number input.");
        }
    }
}
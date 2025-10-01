package com.smartoffice.facility.main;

import com.smartoffice.facility.commands.AdminMenuCommand;
import com.smartoffice.facility.commands.BookRoomCommand;
import com.smartoffice.facility.commands.CancelBookingCommand;
import com.smartoffice.facility.commands.CommandLoggerDecorator;
import com.smartoffice.facility.commands.EmergencyCommand; // Imports the new Emergency Command
import com.smartoffice.facility.interfaces.ICommand;
import com.smartoffice.facility.commands.LoginCommand;
import com.smartoffice.facility.commands.ManagerViewStatusCommand;
import com.smartoffice.facility.commands.RegisterUserCommand;
import com.smartoffice.facility.commands.StatusCommand;
import com.smartoffice.facility.commands.UpdateOccupancyCommand;
import com.smartoffice.facility.core.User;
import com.smartoffice.facility.interfaces.IAuthenticationService;
import com.smartoffice.facility.services.AutomationScheduler;
import com.smartoffice.facility.services.MockAuthenticationService;
import com.smartoffice.facility.services.OfficeFacility;

import java.util.InputMismatchException;
import java.util.Scanner;

/**
 * The Main Application class for the Smart Office Facility.
 * This class handles the user interface, command invocation, and manages the
 * lifecycle of the Singleton OfficeFacility and the AutomationScheduler.
 */
public class SmartOfficeApp {

    // Singleton Instances
    private final OfficeFacility officeFacility = OfficeFacility.getInstance();
    private final IAuthenticationService authService = new MockAuthenticationService();
    private final AutomationScheduler scheduler = AutomationScheduler.getInstance();

    // Application State
    private User currentUser = null;
    private final Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        SmartOfficeApp app = new SmartOfficeApp();
        app.start();
    }

    /**
     * Initializes the scheduler and starts the main application loop.
     */
    public void start() {
        System.out.println("🚀 Starting Smart Office Facility Manager...");
        scheduler.startScheduler(); // Start the background task for auto-releasing rooms

        mainLoop();

        System.out.println("👋 Shutting down scheduler and exiting application. Goodbye!");
        scheduler.stopScheduler();
        scanner.close();
    }

    /**
     * The main interactive loop for the console application.
     */
    private void mainLoop() {
        boolean running = true;
        while (running) {
            displayMenu();
            ICommand command = null;

            try {
                int choice = Integer.parseInt(scanner.nextLine().trim());

                switch (choice) {
                    case 1: // 1. Configure Office (Admin Menu Router)
                        command = new AdminMenuCommand(officeFacility, authService, scanner, currentUser);
                        break;
                    case 2: // 2. Register New User
                        command = new RegisterUserCommand(authService, scanner);
                        break;
                    case 3: // 3. Login
                        command = new LoginCommand(authService, scanner, this);
                        break;
                    case 4: // 4. Book Room
                        command = new BookRoomCommand(officeFacility, authService, scanner, currentUser);
                        break;
                    case 5: // 5. Cancel Booking
                        command = new CancelBookingCommand(officeFacility, authService, scanner, currentUser);
                        break;
                    case 6: // 6. Update Room Occupancy (Sensor Mock)
                        command = new UpdateOccupancyCommand(officeFacility, authService, scanner, currentUser);
                        break;
                    case 7: // 7. View Manager Statistics (Manager Only)
                        command = new ManagerViewStatusCommand(officeFacility, authService, scanner, currentUser);
                        break;
                    case 8: // 8. View Facility Status (General Status)
                        command = new StatusCommand(officeFacility);
                        break;
                    case 9: // 9. TOGGLE EMERGENCY MODE (NEW)
                        // Route to EmergencyCommand, passing the EmergencyService dependency
                        command = new EmergencyCommand(officeFacility, officeFacility.getEmergencyService());
                        break;
                    case 10: // 10. Exit
                        running = false;
                        break;
                    default:
                        System.out.println("❌ Invalid choice. Please select a number from the menu.");
                }

                if (command != null) {
                    // DECORATOR INTEGRATION: Wrap the concrete command with the decorator
                    ICommand loggedCommand = new CommandLoggerDecorator(command);
                    loggedCommand.execute();
                }

            } catch (NumberFormatException e) {
                System.out.println("❌ Invalid input. Please enter a number.");
            } catch (Exception e) {
                System.out.println("🚨 An unexpected error occurred: " + e.getMessage());
            }
        }
    }

    /**
     * Displays the main menu to the user.
     */
    private void displayMenu() {
        System.out.println(AppConstants.MENU_SEPARATOR);
        System.out.println(AppConstants.MENU_HEADER);
        if (currentUser != null) {
            System.out.println("👤 Logged in as: " + currentUser.getUsername());
        } else {
            System.out.println("🔒 Not Logged In");
        }
        System.out.println(AppConstants.MENU_SEPARATOR);

        System.out.println(AppConstants.MENU_CONFIG);
        System.out.println(AppConstants.MENU_REGISTER);
        System.out.println(AppConstants.MENU_LOGIN);
        System.out.println(AppConstants.MENU_BOOK);
        System.out.println(AppConstants.MENU_CANCEL);
        System.out.println(AppConstants.MENU_OCCUPANCY);
        System.out.println("   (Enter 999 in Option 6 for Emergency Sensor Test)"); // Hint for Emergency Sensor
        System.out.println(AppConstants.MENU_MANAGER_STATS);
        System.out.println(AppConstants.MENU_STATUS);
        System.out.println(AppConstants.MENU_EMERGENCY);
        System.out.println(AppConstants.MENU_EXIT);
        System.out.print(AppConstants.MENU_PROMPT);
    }

    /**
     * Setter method used by the LoginCommand to update the application's current user state.
     * @param user The user that has successfully logged in.
     */
    public void setCurrentUser(User user) {
        this.currentUser = user;
    }
}
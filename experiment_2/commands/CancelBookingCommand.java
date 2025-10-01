package com.smartoffice.facility.commands;
import com.smartoffice.facility.core.User;
import com.smartoffice.facility.interfaces.IAuthenticationService;
import com.smartoffice.facility.services.OfficeFacility;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.Scanner;
public class CancelBookingCommand extends AuthenticatedCommand {

    private final Scanner scanner;

    public CancelBookingCommand(OfficeFacility officeFacility, IAuthenticationService authService, Scanner scanner, User currentUser) {
        super(officeFacility, authService, currentUser);
        this.scanner = scanner;
    }

    @Override
    protected boolean authenticatedExecute() {
        if (!officeFacility.isConfigured()) {
            System.out.println(" Office not configured.");
            return false;
        }
        if (currentUser == null) {
            System.out.println(" You must be logged in to cancel a booking.");
            return false;
        }

        System.out.println("\n---  Cancel Booking ---");

    
        System.out.print("Enter Room Number: ");
        String roomNumStr = scanner.nextLine().trim();

        
        System.out.print("Enter Booking Start Time (YYYY-MM-DDTHH:MM:SS): "); 
        String timeStr = scanner.nextLine().trim();

        try {
            int roomNumber = Integer.parseInt(roomNumStr);
            LocalDateTime startTime = LocalDateTime.parse(timeStr);
            String result = officeFacility.cancelBooking(roomNumber, currentUser, startTime);
            if (result.startsWith("✅")) {
                System.out.println(result);
                return true;
            } else {
                System.out.println(result);
                return false;
            }

        } catch (NumberFormatException e) {
            System.out.println(" Invalid input. Please enter a valid whole number for the Room Number.");
            return false;
        } catch (DateTimeParseException e) {
            System.out.println(" Invalid date/time format. Please use the exact format YYYY-MM-DDTHH:MM:SS.");
            System.out.println("   Example: 2025-09-29T09:09:00");
            return false;
        } catch (Exception e) {
            System.out.println(" An unexpected error occurred during cancellation: " + e.getMessage());
            return false;
        }
    }
}

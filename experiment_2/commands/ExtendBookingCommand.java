package com.smartoffice.facility.commands;

import com.smartoffice.facility.core.Booking;
import com.smartoffice.facility.core.Room;
import com.smartoffice.facility.core.User;
import com.smartoffice.facility.interfaces.IAuthenticationService;
import com.smartoffice.facility.services.OfficeFacility;

import java.time.LocalDateTime;
import java.util.Scanner;

public class ExtendBookingCommand extends AuthenticatedCommand {
    private final Scanner scanner;

    public ExtendBookingCommand(OfficeFacility officeFacility, IAuthenticationService authService, Scanner scanner, User currentUser) {
        super(officeFacility, authService, currentUser);
        this.scanner = scanner;
    }

    @Override
    protected boolean authenticatedExecute() {
        if (!officeFacility.isConfigured()) {
            System.out.println("❌ Office not configured.");
            return false;
        }

        System.out.println("\n--- ⏳ Request Meeting Extension ---");
        System.out.print("Enter Room Number: ");
        int roomNumber = Integer.parseInt(scanner.nextLine().trim());

        Room room = officeFacility.getRoom(roomNumber);
        if (room == null) {
            System.out.println("❌ Room not found.");
            return false;
        }

        Booking activeBooking = room.getCurrentActiveBooking();
        if (activeBooking == null || !activeBooking.getUser().equals(currentUser)) {
            System.out.println("❌ No active booking found for your user in this room right now.");
            return false;
        }

        System.out.print("Enter Additional Minutes (e.g., 30): ");
        int additionalMinutes = Integer.parseInt(scanner.nextLine().trim());

        if (additionalMinutes <= 0) {
            System.out.println("❌ Extension must be positive.");
            return false;
        }

        // 1. Check if the extension is possible (delegated to OfficeFacility for safety)
        if (officeFacility.requestExtension(roomNumber, activeBooking, additionalMinutes)) {

            // 2. Notify Manager about the change/success
            officeFacility.getNotificationService().notifyManagerOfExtension(
                    "Room " + roomNumber + " (Original End: " + activeBooking.getEndTime().toLocalTime() + ")",
                    currentUser.getUsername(),
                    additionalMinutes
            );

            System.out.println("✅ Extension granted and users notified. New end time: " + activeBooking.getEndTime().plusMinutes(additionalMinutes).toLocalTime());
            return true;
        } else {
            System.out.println("⚠️ Extension denied! A conflicting booking was found.");
            return false;
        }
    }
}
package com.smartoffice.facility.commands;

import com.smartoffice.facility.core.Booking;
import com.smartoffice.facility.core.Room;
import com.smartoffice.facility.core.User;
import com.smartoffice.facility.interfaces.IAuthenticationService;
import com.smartoffice.facility.services.OfficeFacility;

import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.Scanner;


public class BookRoomCommand extends AuthenticatedCommand {

    private final Scanner scanner;

    public BookRoomCommand(OfficeFacility officeFacility, IAuthenticationService authService, Scanner scanner, User currentUser) {
        super(officeFacility, authService, currentUser);
        this.scanner = scanner;
    }

    @Override
    protected boolean authenticatedExecute() {
        if (!officeFacility.isConfigured()) {
            System.out.println(" Cannot book. The office facility has not been configured yet.");
            return false;
        }

        System.out.println("\n---  Book Room (Capacity & Time Slot) ---");
        System.out.print("Enter Room Number: ");
        String roomNumStr = scanner.nextLine().trim();

        try {
            int roomNumber = Integer.parseInt(roomNumStr);
            Room room = officeFacility.getRoom(roomNumber);

            
            if (room == null) {
                
                System.out.println(" Booking Denied. Room " + roomNumber + " does not exist in the facility map.");
                return false;
            }

            System.out.print("Enter Number of Occupants (Capacity Check): ");
            int occupants = Integer.parseInt(scanner.nextLine().trim());

           
            if (occupants > room.getMaxCapacity()) {
                System.out.println(" Booking Denied. Capacity limit (" + room.getMaxCapacity() + ") exceeded by requested occupants (" + occupants + ").");
                return false;
            }

            System.out.print("Enter Booking Start Time (YYYY-MM-DDTHH:MM:SS): ");
            LocalDateTime startTime = LocalDateTime.parse(scanner.nextLine().trim());

            System.out.print("Enter Duration (minutes): ");
            int duration = Integer.parseInt(scanner.nextLine().trim());

            if (duration <= 0) {
                System.out.println(" Duration must be greater than zero.");
                return false;
            }

            
            if (!room.isAvailable(startTime, duration, occupants)) {

             
                System.out.println(" Booking Denied. This room is already reserved by another user during the requested time slot. Access is denied to this time slot.");
                return false;
            }

          
            Booking newBooking = new Booking(currentUser, room, startTime, duration);

            
            if (officeFacility.addBookingToRoom(roomNumber, newBooking)) {
                System.out.println(" Booking for Room " + roomNumber + " confirmed: " + newBooking.getStartTime().toLocalTime() + " for " + duration + " min.");
                return true;
            } else {
                System.out.println(" Booking failed due to an unknown error (facility issue).");
                return false;
            }

        } catch (NumberFormatException | DateTimeParseException e) {
            System.out.println(" Invalid input. Ensure Room Number, Occupants, and Time/Duration are in the correct format.");
            System.out.println("   Time format must be YYYY-MM-DDTHH:MM:SS (e.g., 2025-10-15T14:30:00).");
            return false;
        }
    }
}

package com.smartoffice.facility.commands;

import com.smartoffice.facility.core.Room;
import com.smartoffice.facility.core.User;
import com.smartoffice.facility.interfaces.IAuthenticationService;
import com.smartoffice.facility.services.EmergencyService; // Import EmergencyService
import com.smartoffice.facility.services.OfficeFacility;
import com.smartoffice.facility.main.AppConstants; // Import AppConstants for the code

import java.time.format.DateTimeParseException;
import java.util.Scanner;

/**
 * Concrete Command: Simulates input from an occupancy sensor.
 * Now includes a check for the Emergency Sensor Code (999).
 */
public class UpdateOccupancyCommand extends AuthenticatedCommand {

    private final Scanner scanner;

    public UpdateOccupancyCommand(OfficeFacility officeFacility, IAuthenticationService authService, Scanner scanner, User currentUser) {
        super(officeFacility, authService, currentUser);
        this.scanner = scanner;
    }

    /**
     * Executes the sensor update.
     */
    @Override
    protected boolean authenticatedExecute() {
        if (!officeFacility.isConfigured()) {
            System.out.println("❌ Cannot update occupancy. The office facility has not been configured yet.");
            return false;
        }

        System.out.println("\n--- 💡 Sensor Occupancy Update ---");
        System.out.print("Enter Room Number: ");
        String roomNumStr = scanner.nextLine().trim();

        try {
            int roomNumber = Integer.parseInt(roomNumStr);
            Room room = officeFacility.getRoom(roomNumber);

            if (room == null) {
                System.out.println("❌ Room " + roomNumber + " does not exist.");
                return false;
            }

            System.out.print("Enter NEW occupancy count (or 999 for Emergency Test): ");
            int newCount = Integer.parseInt(scanner.nextLine().trim());

            if (newCount < 0 && newCount != AppConstants.EMERGENCY_SENSOR_CODE) {
                System.out.println("❌ Occupancy count cannot be negative.");
                return false;
            }

            // --- EMERGENCY SENSOR CHECK ---
            if (newCount == AppConstants.EMERGENCY_SENSOR_CODE) {
                EmergencyService emergencyService = officeFacility.getEmergencyService();

                // Check if emergency is already active to prevent re-triggering cancellation
                if (emergencyService.isEmergencyActive()) {
                    System.out.println("⚠️ Emergency Mode is already active. Ignoring sensor code.");
                    return true;
                }

                EmergencyCommand emergencyCommand = new EmergencyCommand(
                        officeFacility,
                        emergencyService // Pass the established dependency
                );
                System.out.println("🚨 EMERGENCY SIGNAL DETECTED by Room " + roomNumber + " sensor.");

                // Execute the mass cancellation and observer override logic
                return emergencyCommand.execute();
            }
            // ---------------------------------

            // Delegate core logic to the OfficeFacility (Normal Occupancy Update)
            boolean success = officeFacility.updateRoomOccupancy(roomNumber, newCount);

            // The success message is printed by OfficeFacility.updateRoomOccupancy.
            return success;

        } catch (NumberFormatException e) {
            System.out.println("❌ Invalid input. Please enter a valid whole number.");
            return false;
        }
    }
}
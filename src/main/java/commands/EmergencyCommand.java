package com.smartoffice.facility.commands;

import com.smartoffice.facility.interfaces.ICommand;
import com.smartoffice.facility.services.EmergencyService;
import com.smartoffice.facility.services.OfficeFacility;

/**
 * Command to toggle the system's global Emergency Mode (e.g., Fire Alarm).
 */
public class EmergencyCommand implements ICommand {
    private final EmergencyService emergencyService;
    private final OfficeFacility officeFacility;

    public EmergencyCommand(OfficeFacility officeFacility, EmergencyService emergencyService) {
        this.officeFacility = officeFacility;
        this.emergencyService = emergencyService;
    }

    @Override
    public boolean execute() {
        if (!emergencyService.isEmergencyActive()) {
            emergencyService.activateEmergency();
            System.out.println("🚨 EMERGENCY ACTIVATED. Starting Mass Booking Cancellation...");

            // Mass Cancellation Logic
            officeFacility.massCancelAllBookings("FIRE ALARM ACTIVATED.");

            // FIX: Explicitly print the cancellation status message (as requested)
            System.out.println("✅ All active and future bookings have been automatically cancelled.");

            // Trigger observers to update controls (Lights ON, AC OFF)
            officeFacility.getAllRooms().forEach(room -> {
                room.notifyObservers();
            });

            System.out.println("✅ All bookings cancelled and controls updated for safety.");
        } else {
            emergencyService.deactivateEmergency();
            // Trigger observers to restore normal state
            officeFacility.getAllRooms().forEach(room -> {
                room.notifyObservers();
            });
            System.out.println("✅ Emergency Mode deactivated. Restoring normal operation.");
        }
        return true;
    }
}
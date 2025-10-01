package com.smartoffice.facility.controls;

import com.smartoffice.facility.core.Room;
import com.smartoffice.facility.core.RoomStatus;
import com.smartoffice.facility.interfaces.IControlObserver;
import com.smartoffice.facility.main.AppConstants;
import com.smartoffice.facility.services.EmergencyService; // NEW IMPORT

/**
 * Concrete Observer: Manages the Lighting system for a single room.
 * Implements an override to force lights ON during a system emergency (e.g., fire).
 */
public class LightingSystem implements IControlObserver {

    private final int roomNumber;
    private boolean isOn = false;

    public LightingSystem(int roomNumber) {
        this.roomNumber = roomNumber;
        AppConstants.LOGGER.info("LightingSystem initialized for Room " + roomNumber);
    }

    /**
     * Called by the Room (Subject) when its status changes.
     * Checks the global EmergencyService state first.
     */
    @Override
    public void update(Room room) {
        EmergencyService emergencyService = EmergencyService.getInstance();

        // --- EMERGENCY MODE OVERRIDE ---
        // Lights must be forced ON for safety/evacuation regardless of occupancy.
        if (emergencyService.isEmergencyActive()) {
            turnOnForEmergency();
            return; // Stop normal processing
        }
        // ---------------------------------

        // Normal Observer Logic: Based on Occupancy Status
        if (room.getStatus() == RoomStatus.OCCUPIED) {
            turnOn();
        } else {
            turnOff();
        }
    }

    private void turnOn() {
        if (!isOn) {
            System.out.println("💡 [LIGHTS CONTROL] Room " + roomNumber + ": Lights turned ON.");
            AppConstants.LOGGER.info("Room " + roomNumber + ": Lights ON.");
            isOn = true;
        }
    }

    private void turnOff() {
        if (isOn) {
            System.out.println("🌑 [LIGHTS CONTROL] Room " + roomNumber + ": Lights turned OFF.");
            AppConstants.LOGGER.info("Room " + roomNumber + ": Lights OFF.");
            isOn = false;
        }
    }

    private void turnOnForEmergency() {
        if (!isOn) {
            System.out.println("🚨 💡 [LIGHTS CONTROL] Room " + roomNumber + ": EMERGENCY LIGHTING ON. (Max Brightness).");
            AppConstants.LOGGER.warning("Room " + roomNumber + ": Lights forced ON due to emergency.");
            isOn = true;
        }
    }
}
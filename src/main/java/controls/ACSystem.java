package com.smartoffice.facility.controls;

import com.smartoffice.facility.core.Room;
import com.smartoffice.facility.core.RoomStatus;
import com.smartoffice.facility.interfaces.IControlObserver;
import com.smartoffice.facility.main.AppConstants;
import com.smartoffice.facility.services.EmergencyService; // NEW IMPORT

/**
 * Concrete Observer: Manages the Air Conditioning system for a single room.
 * Implements an override to force shutdown during a system emergency (e.g., fire).
 */
public class ACSystem implements IControlObserver {

    private final int roomNumber;
    private boolean isOn = false;

    public ACSystem(int roomNumber) {
        this.roomNumber = roomNumber;
        AppConstants.LOGGER.info("ACSystem initialized for Room " + roomNumber);
    }

    /**
     * Called by the Room (Subject) when its status changes.
     * Checks the global EmergencyService state first.
     */
    @Override
    public void update(Room room) {
        EmergencyService emergencyService = EmergencyService.getInstance();

        // --- EMERGENCY MODE OVERRIDE ---
        if (emergencyService.isEmergencyActive()) {
            turnOffForEmergency();
            return; // Stop normal processing when emergency is active
        }
        // ---------------------------------

        // Normal Observer Logic: Based on Occupancy Status
        if (room.getStatus() == RoomStatus.OCCUPIED) {
            turnOn();
        } else {
            // If the status is BOOKED, AVAILABLE, or IDLE, turn the AC OFF.
            turnOff();
        }
    }

    private void turnOn() {
        if (!isOn) {
            System.out.println("❄️ [AC CONTROL] Room " + roomNumber + ": AC turned ON.");
            AppConstants.LOGGER.info("Room " + roomNumber + ": AC ON.");
            isOn = true;
        }
    }

    private void turnOff() {
        if (isOn) {
            System.out.println("♨️ [AC CONTROL] Room " + roomNumber + ": AC turned OFF.");
            AppConstants.LOGGER.info("Room " + roomNumber + ": AC OFF.");
            isOn = false;
        }
    }

    private void turnOffForEmergency() {
        if (isOn) {
            System.out.println("♨️ 🛑 [AC CONTROL] Room " + roomNumber + ": EMERGENCY SHUTDOWN. AC OFF.");
            AppConstants.LOGGER.warning("Room " + roomNumber + ": AC forced OFF due to emergency.");
            isOn = false;
        }
    }
}
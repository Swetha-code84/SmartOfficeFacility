package com.smartoffice.facility.controls;
import com.smartoffice.facility.core.Room;
import com.smartoffice.facility.core.RoomStatus;
import com.smartoffice.facility.interfaces.IControlObserver;
import com.smartoffice.facility.main.AppConstants;
import com.smartoffice.facility.services.EmergencyService; // NEW IMPORT
public class LightingSystem implements IControlObserver {
    private final int roomNumber;
    private boolean isOn = false;
    public LightingSystem(int roomNumber) {
        this.roomNumber = roomNumber;
        AppConstants.LOGGER.info("LightingSystem initialized for Room " + roomNumber);
    }
    @Override
    public void update(Room room) {
        EmergencyService emergencyService = EmergencyService.getInstance();
        if (emergencyService.isEmergencyActive()) {
            turnOnForEmergency();
            return; 
        }
 
        if (room.getStatus() == RoomStatus.OCCUPIED) {
            turnOn();
        } else {
            turnOff();
        }
    }
    private void turnOn() {
        if (!isOn) {
            System.out.println(" [LIGHTS CONTROL] Room " + roomNumber + ": Lights turned ON.");
            AppConstants.LOGGER.info("Room " + roomNumber + ": Lights ON.");
            isOn = true;
        }
    }
    private void turnOff() {
        if (isOn) {
            System.out.println(" [LIGHTS CONTROL] Room " + roomNumber + ": Lights turned OFF.");
            AppConstants.LOGGER.info("Room " + roomNumber + ": Lights OFF.");
            isOn = false;
        }
    }
    private void turnOnForEmergency() {
        if (!isOn) {
            System.out.println(" [LIGHTS CONTROL] Room " + roomNumber + ": EMERGENCY LIGHTING ON. (Max Brightness).");
            AppConstants.LOGGER.warning("Room " + roomNumber + ": Lights forced ON due to emergency.");
            isOn = true;
        }
    }
}

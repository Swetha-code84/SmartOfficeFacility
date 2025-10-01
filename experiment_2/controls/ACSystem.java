package com.smartoffice.facility.controls;
import com.smartoffice.facility.core.Room;
import com.smartoffice.facility.core.RoomStatus;
import com.smartoffice.facility.interfaces.IControlObserver;
import com.smartoffice.facility.main.AppConstants;
import com.smartoffice.facility.services.EmergencyService; 
public class ACSystem implements IControlObserver {
    private final int roomNumber;
    private boolean isOn = false;
    public ACSystem(int roomNumber) {
        this.roomNumber = roomNumber;
        AppConstants.LOGGER.info("ACSystem initialized for Room " + roomNumber);
    }
    @Override
    public void update(Room room) {
        EmergencyService emergencyService = EmergencyService.getInstance();
        if (emergencyService.isEmergencyActive()) {
            turnOffForEmergency();
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
            System.out.println(" [AC CONTROL] Room " + roomNumber + ": AC turned ON.");
            AppConstants.LOGGER.info("Room " + roomNumber + ": AC ON.");
            isOn = true;
        }
    }
    private void turnOff() {
        if (isOn) {
            System.out.println(" [AC CONTROL] Room " + roomNumber + ": AC turned OFF.");
            AppConstants.LOGGER.info("Room " + roomNumber + ": AC OFF.");
            isOn = false;
        }
    }
    private void turnOffForEmergency() {
        if (isOn) {
            System.out.println(" [AC CONTROL] Room " + roomNumber + ": EMERGENCY SHUTDOWN. AC OFF.");
            AppConstants.LOGGER.warning("Room " + roomNumber + ": AC forced OFF due to emergency.");
            isOn = false;
        }
    }
}

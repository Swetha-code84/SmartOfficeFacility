package com.smartoffice.facility.commands;
import com.smartoffice.facility.interfaces.ICommand;
import com.smartoffice.facility.services.EmergencyService;
import com.smartoffice.facility.services.OfficeFacility;
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
            System.out.println(" EMERGENCY ACTIVATED. Starting Mass Booking Cancellation...");
            
            officeFacility.massCancelAllBookings("FIRE ALARM ACTIVATED.");

            System.out.println(" All active and future bookings have been automatically cancelled.");
            
            officeFacility.getAllRooms().forEach(room -> {
                room.notifyObservers();
            });
            System.out.println(" All bookings cancelled and controls updated for safety.");
        } else {
            emergencyService.deactivateEmergency();
            officeFacility.getAllRooms().forEach(room -> {
                room.notifyObservers();
            });
            System.out.println(" Emergency Mode deactivated. Restoring normal operation.");
        }
        return true;
    }
}

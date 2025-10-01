package com.smartoffice.facility.commands;
import com.smartoffice.facility.core.Room;
import com.smartoffice.facility.core.Booking;
import com.smartoffice.facility.interfaces.ICommand;
import com.smartoffice.facility.services.OfficeFacility;
import com.smartoffice.facility.main.AppConstants;
public class ReleaseBookingCommand implements ICommand {
    private final OfficeFacility officeFacility;
    private final int roomNumber;
    public ReleaseBookingCommand(OfficeFacility officeFacility, int roomNumber) {
        this.officeFacility = officeFacility;
        this.roomNumber = roomNumber;
    }
    @Override
    public boolean execute() {
        AppConstants.LOGGER.info("Executing ReleaseBookingCommand for Room " + roomNumber + ".");
        boolean released = officeFacility.releaseBooking(roomNumber);

        if (released) {
            AppConstants.LOGGER.info("SUCCESS: Room " + roomNumber + " booking automatically released.");
            System.out.println(String.format("[AUTOMATED RELEASE] Room %d booking released due to grace period expiry.",
                    roomNumber));
        } else {
            AppConstants.LOGGER.warning("Failed to auto-release booking for Room " + roomNumber + ". (No overdue booking found).");
        }
        return released;
    }
}

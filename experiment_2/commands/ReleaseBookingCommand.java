package com.smartoffice.facility.commands;

import com.smartoffice.facility.core.Room;
import com.smartoffice.facility.core.Booking;
import com.smartoffice.facility.interfaces.ICommand;
import com.smartoffice.facility.services.OfficeFacility;
import com.smartoffice.facility.main.AppConstants;

/**
 * Concrete Command: Handles the automated release of a booking due to non-occupancy (Mandatory 4).
 * This command is executed by the AutomationScheduler (background thread).
 */
public class ReleaseBookingCommand implements ICommand {
    private final OfficeFacility officeFacility;
    private final int roomNumber;

    /**
     * Constructor signature expected by AutomationScheduler.java.
     */
    public ReleaseBookingCommand(OfficeFacility officeFacility, int roomNumber) {
        this.officeFacility = officeFacility;
        this.roomNumber = roomNumber;
    }

    /**
     * Executes the automated release logic.
     */
    @Override
    public boolean execute() {
        AppConstants.LOGGER.info("Executing ReleaseBookingCommand for Room " + roomNumber + ".");

        // The responsibility of finding the overdue booking, checking occupancy,
        // removing it from the schedule, and sending the notification is now
        // delegated entirely to the OfficeFacility service layer.

        // Note: The previous lines causing errors (isBooked(), getCurrentBooking())
        // are removed because Room no longer uses those methods.

        // Delegate the core logic
        boolean released = officeFacility.releaseBooking(roomNumber);

        if (released) {
            AppConstants.LOGGER.info("SUCCESS: Room " + roomNumber + " booking automatically released.");
            // Print user-friendly message, but avoid accessing Booking details here,
            // as the OfficeFacility should have already handled the details in the notification step.
            System.out.println(String.format("[AUTOMATED RELEASE] Room %d booking released due to grace period expiry.",
                    roomNumber));
        } else {
            // This happens if the room is not found, or no eligible overdue booking was found.
            AppConstants.LOGGER.warning("Failed to auto-release booking for Room " + roomNumber + ". (No overdue booking found).");
        }

        return released;
    }
}
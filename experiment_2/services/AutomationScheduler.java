package com.smartoffice.facility.services;

import com.smartoffice.facility.core.Booking;
import com.smartoffice.facility.core.Room;
import com.smartoffice.facility.interfaces.ICommand;
import com.smartoffice.facility.commands.ReleaseBookingCommand;
import com.smartoffice.facility.main.AppConstants;

import java.time.LocalDateTime;
import java.time.ZoneId; // Required for reliable timestamp calculation
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class AutomationScheduler {

    private static AutomationScheduler instance;
    private final ScheduledExecutorService scheduler;
    private ScheduledFuture<?> autoReleaseTask;

    private AutomationScheduler() {
        this.scheduler = Executors.newSingleThreadScheduledExecutor();
    }

    public static synchronized AutomationScheduler getInstance() {
        if (instance == null) {
            instance = new AutomationScheduler();
        }
        return instance;
    }

    public void startScheduler() {
        if (autoReleaseTask == null || autoReleaseTask.isDone()) {
            System.out.println(" Automation Scheduler initialized and running...");

            this.autoReleaseTask = scheduler.scheduleWithFixedDelay(
                    this::checkForAutomationEvents,
                    0, 
                    AppConstants.SCHEDULER_DELAY_MS,
                    TimeUnit.MILLISECONDS
            );
        }
    }

    public void stopScheduler() {
        if (autoReleaseTask != null) {
            autoReleaseTask.cancel(true);
        }
        if (!scheduler.isShutdown()) {
            scheduler.shutdownNow();
        }
    }

    private void checkForAutomationEvents() {
        OfficeFacility officeFacility = OfficeFacility.getInstance();

        if (!officeFacility.isConfigured()) {
            return;
        }

        LocalDateTime now = LocalDateTime.now();

        for (Room room : officeFacility.getAllRooms()) {
            if (room.isOccupied()) {
                continue;
            }

            room.getBookings().stream()
                    .filter(b -> !b.getStartTime().isAfter(now))
                    .forEach(booking -> {

                        long timeElapsedMs = now.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
                                - booking.getCreationTimestamp();

                        if (timeElapsedMs >= AppConstants.GRACE_PERIOD_MS) {

                            System.out.println("\n[AUTOMATION] Room " + room.getRoomNumber() +
                                    ": Booking (starts " + booking.getStartTime().toLocalTime() +
                                    ") grace period expired. Releasing automatically.");

                            ICommand releaseCommand = new ReleaseBookingCommand(officeFacility, room.getRoomNumber());
                            releaseCommand.execute();

                            System.out.print(AppConstants.MENU_PROMPT);

                        }
                        else if (!booking.isWarningSent() && timeElapsedMs >= AppConstants.WARNING_PERIOD_MS) {

                            String warningMessage = String.format(
                                    " WARNING: Your meeting for Room %d (starts %s) has been active for %d minutes but is unoccupied. If not occupied within the next %d minutes, the booking will be cancelled and the room released.",
                                    room.getRoomNumber(), booking.getStartTime().toLocalTime(),
                                    AppConstants.WARNING_PERIOD_MINUTES,
                                    AppConstants.GRACE_PERIOD_MINUTES - AppConstants.WARNING_PERIOD_MINUTES
                            );

                            officeFacility.getNotificationService().notifyUser(booking, warningMessage);
                            booking.setWarningSent(true); 

                            System.out.println(" [AUTOMATION] Sent 2-minute warning to " + booking.getUser().getUsername() + " for Room " + room.getRoomNumber());
                        }
                    });
        }
    }
}

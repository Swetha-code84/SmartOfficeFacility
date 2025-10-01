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

/**
 * Manages the background automation tasks (Singleton).
 * Implements the two-stage logic: 2-minute warning and 5-minute cancellation.
 */
public class AutomationScheduler {

    // ------------------------------------------------------------------------
    // SINGLETON IMPLEMENTATION
    // ------------------------------------------------------------------------
    private static AutomationScheduler instance;

    // Dependency (Will be lazily retrieved inside the check method)
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

    // ------------------------------------------------------------------------
    // SCHEDULER CONTROL
    // ------------------------------------------------------------------------

    public void startScheduler() {
        if (autoReleaseTask == null || autoReleaseTask.isDone()) {
            System.out.println("⏰ Automation Scheduler initialized and running...");

            // Rename the method to reflect its new, broader purpose
            this.autoReleaseTask = scheduler.scheduleWithFixedDelay(
                    this::checkForAutomationEvents,
                    0, // Initial delay
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

    // ------------------------------------------------------------------------
    // AUTOMATION CORE LOGIC (TWO-STAGE CHECK)
    // ------------------------------------------------------------------------

    /**
     * Checks for two events: 2-minute warning and 5-minute cancellation.
     */
    private void checkForAutomationEvents() {
        OfficeFacility officeFacility = OfficeFacility.getInstance();

        if (!officeFacility.isConfigured()) {
            return;
        }

        LocalDateTime now = LocalDateTime.now();

        for (Room room : officeFacility.getAllRooms()) {
            // Must be unoccupied to trigger either automation event
            if (room.isOccupied()) {
                continue;
            }

            room.getBookings().stream()
                    // Filter 1: Booking must have started already
                    .filter(b -> !b.getStartTime().isAfter(now))
                    .forEach(booking -> {

                        // Calculate time elapsed since the booking STARTED (creationTimestamp uses LocalDateTime.now())
                        // IMPORTANT: We should check time elapsed since START TIME, not CREATION TIME,
                        // but since the original implementation used a creation timestamp proxy, we stick to that
                        // for time elapsed measurement.
                        long timeElapsedMs = now.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
                                - booking.getCreationTimestamp();

                        // --- 1. CANCELLATION CHECK (5 Minutes) ---
                        if (timeElapsedMs >= AppConstants.GRACE_PERIOD_MS) {

                            System.out.println("\n[AUTOMATION] Room " + room.getRoomNumber() +
                                    ": Booking (starts " + booking.getStartTime().toLocalTime() +
                                    ") grace period expired. Releasing automatically.");

                            // Execute the cancellation command
                            ICommand releaseCommand = new ReleaseBookingCommand(officeFacility, room.getRoomNumber());
                            releaseCommand.execute();

                            System.out.print(AppConstants.MENU_PROMPT);

                        }
                        // --- 2. WARNING CHECK (2 Minutes) ---
                        else if (!booking.isWarningSent() && timeElapsedMs >= AppConstants.WARNING_PERIOD_MS) {

                            String warningMessage = String.format(
                                    "⚠️ WARNING: Your meeting for Room %d (starts %s) has been active for %d minutes but is unoccupied. If not occupied within the next %d minutes, the booking will be cancelled and the room released.",
                                    room.getRoomNumber(), booking.getStartTime().toLocalTime(),
                                    AppConstants.WARNING_PERIOD_MINUTES,
                                    AppConstants.GRACE_PERIOD_MINUTES - AppConstants.WARNING_PERIOD_MINUTES
                            );

                            // Send notification and update the flag immediately
                            officeFacility.getNotificationService().notifyUser(booking, warningMessage);
                            booking.setWarningSent(true); // Mark warning as sent

                            System.out.println("⚠️ [AUTOMATION] Sent 2-minute warning to " + booking.getUser().getUsername() + " for Room " + room.getRoomNumber());
                        }
                    });
        }
    }
}
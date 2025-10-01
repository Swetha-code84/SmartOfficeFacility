package com.smartoffice.facility.core;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;

/**
 * Core Entity: Represents a single room booking instance with a specified start time and duration.
 * This entity supports conflict checking and tracks automation state.
 */
public class Booking {

    private final Room room;
    private final User user;

    private final LocalDateTime startTime;
    private final int durationMinutes;

    private final LocalDateTime creationTime;
    private final long creationTimestamp;

    // NEW FIELD: Tracks if the 2-minute warning notification has been sent.
    private boolean warningSent = false;

    /**
     * Primary Constructor for the new time-based booking system.
     */
    public Booking(User user, Room room, LocalDateTime startTime, int durationMinutes) {
        this.user = user;
        this.room = room;
        this.startTime = startTime;
        this.durationMinutes = durationMinutes;

        this.creationTime = LocalDateTime.now();

        // Use system's default zone for millisecond conversion
        this.creationTimestamp = creationTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
    }

    // --- GETTERS & SETTERS for Automation State ---

    /**
     * Getter for the automation warning flag.
     */
    public boolean isWarningSent() {
        return warningSent;
    }

    /**
     * Setter to mark that the warning has been sent (required by AutomationScheduler).
     */
    public void setWarningSent(boolean warningSent) {
        this.warningSent = warningSent;
    }

    // --- Core Getters (Required for scheduling and display) ---

    public Room getRoom() {
        return room;
    }

    public User getUser() {
        return user;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public int getDurationMinutes() {
        return durationMinutes;
    }

    public LocalDateTime getEndTime() {
        return startTime.plusMinutes(durationMinutes);
    }

    public LocalDateTime getCreationTime() {
        return creationTime;
    }

    public long getCreationTimestamp() {
        return creationTimestamp;
    }

    // --- CORE SCHEDULING LOGIC ---

    /**
     * Checks if this booking time slot overlaps with a proposed new booking slot.
     */
    public boolean conflictsWith(LocalDateTime proposedStart, int proposedDuration) {
        LocalDateTime proposedEnd = proposedStart.plusMinutes(proposedDuration);

        // Conflict check logic: (Start A < End B) AND (End A > Start B)
        boolean startsBeforeExistingEnds = proposedStart.isBefore(this.getEndTime());
        boolean endsAfterExistingStarts = proposedEnd.isAfter(this.startTime);

        return startsBeforeExistingEnds && endsAfterExistingStarts;
    }

    @Override
    public String toString() {
        return String.format("Room %d booked by %s | Starts: %s | Duration: %d min",
                room.getRoomNumber(), user.getUsername(), startTime.toLocalTime(), durationMinutes);
    }
}
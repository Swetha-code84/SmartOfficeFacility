package com.smartoffice.facility.core;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;

public class Booking {

    private final Room room;
    private final User user;
    private final LocalDateTime startTime;
    private final int durationMinutes;
    private final LocalDateTime creationTime;
    private final long creationTimestamp;
    private boolean warningSent = false;

    public Booking(User user, Room room, LocalDateTime startTime, int durationMinutes) {
        this.user = user;
        this.room = room;
        this.startTime = startTime;
        this.durationMinutes = durationMinutes;
        this.creationTime = LocalDateTime.now();

        this.creationTimestamp = creationTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
    }

    public boolean isWarningSent() {
        return warningSent;
    }
    public void setWarningSent(boolean warningSent) {
        this.warningSent = warningSent;
    }

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

    public boolean conflictsWith(LocalDateTime proposedStart, int proposedDuration) {
        LocalDateTime proposedEnd = proposedStart.plusMinutes(proposedDuration);
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

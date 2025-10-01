package com.smartoffice.facility.core;

import com.smartoffice.facility.interfaces.IControlObserver;
import com.smartoffice.facility.main.AppConstants;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Core Entity: Represents a single conference room.
 * This class acts as the SUBJECT in the Observer Pattern and tracks usage statistics,
 * managing a schedule of multiple bookings, and now handling extensions.
 */
public class Room {
    private final int roomNumber;
    private int maxCapacity;
    private int occupancyCount = 0;
    private RoomStatus status = RoomStatus.AVAILABLE;

    // --- SCHEDULING FIELD (Thread-safe list) ---
    private final List<Booking> bookings;
    // ------------------------------------------------------------------

    private final List<IControlObserver> observers;

    // --- USAGE STATISTICS FIELDS ---
    private long totalOccupiedDurationMs = 0;
    private long occupiedStartTimeMs = 0;
    private int totalBookingCount = 0;

    public Room(int roomNumber) {
        this.roomNumber = roomNumber;
        this.maxCapacity = 10; // Default capacity
        this.observers = new ArrayList<>();
        this.bookings = Collections.synchronizedList(new ArrayList<>());
    }

    // --- ADMIN SETTERS ---
    public synchronized void setMaxCapacity(int maxCapacity) {
        this.maxCapacity = maxCapacity;
        AppConstants.LOGGER.info("Room " + roomNumber + " Max Capacity updated to " + maxCapacity);
    }
    public int getMaxCapacity() {
        return maxCapacity;
    }

    // --- CORE SCHEDULING METHODS ---

    public boolean isAvailable(LocalDateTime start, int durationMinutes, int requiredOccupants) {
        if (requiredOccupants > this.maxCapacity) {
            return false;
        }

        for (Booking existingBooking : bookings) {
            if (existingBooking.conflictsWith(start, durationMinutes)) {
                return false; // Time conflict found
            }
        }
        return true;
    }

    public synchronized void addBooking(Booking newBooking) {
        this.bookings.add(newBooking);
        this.totalBookingCount++;
    }

    public synchronized boolean removeBooking(Booking bookingToRemove) {
        return this.bookings.remove(bookingToRemove);
    }

    // ------------------------------------------------------------------------
    // EXTENSION LOGIC
    // ------------------------------------------------------------------------

    /**
     * Step 1 of Extension: Checks if extending the booking creates any new conflicts.
     */
    public synchronized boolean checkExtension(Booking bookingToExtend, int additionalMinutes) {
        int totalNewDuration = bookingToExtend.getDurationMinutes() + additionalMinutes;

        // Check the new extended range against ALL OTHER bookings.
        for (Booking existingBooking : bookings) {
            if (existingBooking.equals(bookingToExtend)) {
                continue;
            }

            // Check if the *new* total reservation period conflicts with any other booking.
            if (existingBooking.conflictsWith(bookingToExtend.getStartTime(), totalNewDuration)) {
                return false; // Conflict found
            }
        }
        return true; // Extension is valid
    }

    /**
     * Step 2 of Extension: Applies the valid extension by replacing the old booking.
     */
    public synchronized void updateBooking(Booking oldBooking, Booking extendedBooking) {
        // 1. Remove the old (unextended) booking
        this.bookings.remove(oldBooking);

        // 2. Add the new (extended) booking
        this.bookings.add(extendedBooking);
    }

    // --- OBSERVER PATTERN (Subject Methods) ---
    public void registerObserver(IControlObserver observer) {
        this.observers.add(observer);
        AppConstants.LOGGER.info("Room " + roomNumber + ": Attached observer: " + observer.getClass().getSimpleName());
    }

    /**
     * FIX: Changed visibility to public so EmergencyCommand can call it.
     */
    public void notifyObservers() {
        for (IControlObserver observer : observers) {
            observer.update(this);
        }
    }

    // --- STATE MUTATORS & STATS LOGIC ---
    public synchronized void setOccupancyCount(int newCount) {
        if (newCount < 0) return;
        this.occupancyCount = newCount;
    }

    public synchronized void setStatus(RoomStatus newStatus) {
        RoomStatus oldStatus = this.status;
        this.status = newStatus;

        if (oldStatus != newStatus) {
            AppConstants.LOGGER.info(String.format("Room %d Status change: %s -> %s", roomNumber, oldStatus, newStatus));

            if (newStatus == RoomStatus.OCCUPIED) {
                this.occupiedStartTimeMs = System.currentTimeMillis();
            }
            if (oldStatus == RoomStatus.OCCUPIED) {
                long duration = System.currentTimeMillis() - occupiedStartTimeMs;
                this.totalOccupiedDurationMs += duration;
                this.occupiedStartTimeMs = 0;
            }

            notifyObservers(); // Notify controls on status change
        }
    }

    // --- GETTERS & STATUS CHECKERS ---

    public int getRoomNumber() { return roomNumber; }
    public int getOccupancyCount() { return occupancyCount; }
    public RoomStatus getStatus() { return status; }

    public List<Booking> getBookings() {
        return Collections.unmodifiableList(this.bookings);
    }

    public Booking getCurrentActiveBooking() {
        LocalDateTime now = LocalDateTime.now();

        for (Booking booking : bookings) {
            if (now.isEqual(booking.getStartTime()) ||
                    (now.isAfter(booking.getStartTime()) && now.isBefore(booking.getEndTime()))) {
                return booking;
            }
        }
        return null;
    }

    public boolean isBookedNow() {
        return getCurrentActiveBooking() != null;
    }
    public boolean isOccupied() {
        return occupancyCount >= AppConstants.MIN_OCCUPANCY_FOR_ACTIVATION;
    }

    // --- STATISTICS GETTERS ---
    public long getTotalOccupiedDurationSeconds() {
        long currentRunningDuration = 0;
        if (status == RoomStatus.OCCUPIED && occupiedStartTimeMs > 0) {
            currentRunningDuration = System.currentTimeMillis() - occupiedStartTimeMs;
        }
        return (totalOccupiedDurationMs + currentRunningDuration) / 1000;
    }

    public int getTotalBookingCount() { return totalBookingCount; }

    @Override
    public String toString() {
        long currentReservations = bookings.stream()
                .filter(b -> b.getEndTime().isAfter(LocalDateTime.now()))
                .count();
        return String.format("Room %d (Max: %d): %s | Occupancy: %d | Future Bookings: %d",
                roomNumber, maxCapacity, status, occupancyCount, currentReservations);
    }
}
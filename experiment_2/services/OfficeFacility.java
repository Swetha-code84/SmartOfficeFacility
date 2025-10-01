package com.smartoffice.facility.services;

import com.smartoffice.facility.core.Booking;
import com.smartoffice.facility.core.Room;
import com.smartoffice.facility.core.RoomStatus;
import com.smartoffice.facility.core.User;
import com.smartoffice.facility.interfaces.IAuthenticationService;
import com.smartoffice.facility.interfaces.INotificationService;
import com.smartoffice.facility.interfaces.IRoomFactory;
import com.smartoffice.facility.main.AppConstants;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List; // Needed for ArrayList initialization
import java.util.Map;
import java.util.Optional;

/**
 * The central management system for the Smart Office.
 * Implements the Singleton Pattern.
 */
public class OfficeFacility {

    // ------------------------------------------------------------------------
    // SINGLETON IMPLEMENTATION
    // ------------------------------------------------------------------------
    private static OfficeFacility instance;

    // Dependencies
    private AutomationScheduler scheduler = null;
    private final IAuthenticationService authService;
    private final INotificationService notificationService;
    private final IRoomFactory roomFactory;
    private final EmergencyService emergencyService; // Emergency Service Dependency

    // ------------------------------------------------------------------------
    // OFFICE STATE
    // ------------------------------------------------------------------------
    private final Map<Integer, Room> rooms;
    private boolean isConfigured = false;

    private OfficeFacility() {
        this.rooms = new HashMap<>();
        this.authService = new MockAuthenticationService();
        ExternalSMSGateway smsGateway = new ExternalSMSGateway();
        this.notificationService = new NotificationServiceAdapter(smsGateway);
        this.roomFactory = new SimpleRoomFactory();
        this.emergencyService = EmergencyService.getInstance();
    }

    public static synchronized OfficeFacility getInstance() {
        if (instance == null) {
            instance = new OfficeFacility();
        }
        return instance;
    }

    // --- DEPENDENCY GETTERS ---
    public IAuthenticationService getAuthenticationService() { return authService; }
    public AutomationScheduler getAutomationScheduler() {
        if (scheduler == null) { scheduler = AutomationScheduler.getInstance(); }
        return scheduler;
    }
    public INotificationService getNotificationService() { return notificationService; }
    public EmergencyService getEmergencyService() { return emergencyService; } // Emergency Getter

    // ------------------------------------------------------------------------
    // CONFIGURATION
    // ------------------------------------------------------------------------

    public synchronized boolean initialConfigure(int numberOfRooms) {
        if (isConfigured) {
            System.out.println("⚠️ Office facility is already configured. Cannot reconfigure.");
            return false;
        }
        if (numberOfRooms <= 0) {
            System.out.println("⚠️ Invalid number of rooms. Must be greater than 0.");
            return false;
        }

        rooms.clear();
        for (int i = 1; i <= numberOfRooms; i++) {
            Room room = roomFactory.createRoom(i);
            rooms.put(i, room);
        }
        isConfigured = true;
        AppConstants.LOGGER.info("Office configured successfully with " + numberOfRooms + " conference rooms.");
        System.out.println("✅ Office configured successfully with " + numberOfRooms + " conference rooms.");
        return true;
    }

    public synchronized boolean addSingleRoom(int roomNumber) {
        if (rooms.containsKey(roomNumber)) {
            System.out.println("❌ Room " + roomNumber + " already exists.");
            return false;
        }
        Room room = roomFactory.createRoom(roomNumber);
        rooms.put(roomNumber, room);
        isConfigured = true;
        AppConstants.LOGGER.info("Room " + roomNumber + " added successfully via Admin Menu.");
        System.out.println("✅ Room " + roomNumber + " added successfully.");
        return true;
    }

    public boolean isConfigured() {
        return isConfigured;
    }

    // ------------------------------------------------------------------------
    // SCHEDULING OPERATIONS
    // ------------------------------------------------------------------------

    public synchronized boolean addBookingToRoom(int roomNumber, Booking newBooking) {
        Room room = rooms.get(roomNumber);

        if (room == null) {
            System.out.println("❌ Room " + roomNumber + " does not exist.");
            return false;
        }

        room.addBooking(newBooking);

        AppConstants.LOGGER.info("Room " + roomNumber + " booking added by " + newBooking.getUser().getUsername() + ".");
        return true;
    }

    public synchronized String cancelBooking(int roomNumber, User user, LocalDateTime startTime) {
        Room room = rooms.get(roomNumber);

        if (room == null) {
            return "❌ Cancellation Denied: Room " + roomNumber + " not found.";
        }

        // 1. Find the specific booking in the room's schedule
        Optional<Booking> bookingToCancel = room.getBookings().stream()
                .filter(b -> b.getStartTime().equals(startTime) && b.getUser().equals(user))
                .findFirst();

        if (bookingToCancel.isPresent()) {
            // 2. Use the dedicated removeBooking method in Room
            if (room.removeBooking(bookingToCancel.get())) {
                AppConstants.LOGGER.info("Booking cancelled manually for Room " + roomNumber + " at " + startTime + ".");

                // Force a status refresh if the cancelled booking was the current active one.
                updateRoomOccupancy(roomNumber, room.getOccupancyCount());
                return "✅ Booking for Room " + roomNumber + " at " + startTime.toLocalTime() + " successfully cancelled.";
            }
        }

        // Provide specific feedback for cancellation failure
        if (room.getBookings().isEmpty()) {
            return "❌ Cancellation Denied: Room " + roomNumber + " has no scheduled bookings.";
        }

        return "❌ Cancellation Denied: A booking for Room " + roomNumber + " at " + startTime.toLocalTime() + " was not found for your user. Access denied.";
    }

    public synchronized boolean requestExtension(int roomNumber, Booking bookingToExtend, int additionalMinutes) {
        Room room = rooms.get(roomNumber);

        if (room == null) {
            System.out.println("❌ Extension failed: Room " + roomNumber + " not found.");
            return false;
        }

        // 1. Check for conflicts using Room.checkExtension
        if (!room.checkExtension(bookingToExtend, additionalMinutes)) {
            System.out.println("❌ Extension failed: New duration conflicts with an existing reservation.");
            return false;
        }

        // 2. Create the new extended Booking object (Immutable replacement)
        LocalDateTime newEndTime = bookingToExtend.getEndTime().plusMinutes(additionalMinutes);
        int newDuration = bookingToExtend.getDurationMinutes() + additionalMinutes;

        Booking extendedBooking = new Booking(
                bookingToExtend.getUser(),
                room,
                bookingToExtend.getStartTime(),
                newDuration
        );

        // 3. Apply the extension (remove old, add new)
        room.updateBooking(bookingToExtend, extendedBooking);

        // 4. Notify Manager about the successful extension
        getNotificationService().notifyManagerOfExtension(
                "Room " + roomNumber + " extended to end at " + newEndTime.toLocalTime(),
                bookingToExtend.getUser().getUsername(),
                additionalMinutes
        );

        AppConstants.LOGGER.info("Room " + roomNumber + " extended by " + additionalMinutes + " min. New end time: " + newEndTime.toLocalTime());
        return true;
    }

    // ------------------------------------------------------------------------
    // OCCUPANCY AND AUTOMATION (Auto-Release & Emergency)
    // ------------------------------------------------------------------------

    /**
     * Mass cancellation method used during system emergency (NEW FEATURE).
     */
    public synchronized void massCancelAllBookings(String reason) {
        for (Room room : rooms.values()) {
            // Create a list to avoid ConcurrentModificationException while iterating and modifying
            List<Booking> bookingsToCancel = new java.util.ArrayList<>(room.getBookings());

            for (Booking booking : bookingsToCancel) {
                // Remove the booking from the room's schedule
                if (room.removeBooking(booking)) {

                    // Set the room status to AVAILABLE (safe state)
                    room.setStatus(RoomStatus.AVAILABLE);

                    // Notify the user about the forced cancellation
                    String notificationMessage = String.format("EMERGENCY CANCELLATION: Your booking for Room %d (starts %s) has been auto-cancelled. Reason: %s",
                            room.getRoomNumber(), booking.getStartTime().toLocalTime(), reason);

                    getNotificationService().notifyUser(booking, notificationMessage);
                    AppConstants.LOGGER.info("EMERGENCY: Cancelled booking for " + booking.getUser().getUsername() + " in Room " + room.getRoomNumber());
                }
            }
        }
    }


    /**
     * Finds and removes an overdue booking from a room's schedule list (Auto-Release).
     */
    public synchronized boolean releaseBooking(int roomNumber) {
        Room room = rooms.get(roomNumber);

        if (room == null || room.getBookings().isEmpty()) {
            return false;
        }

        LocalDateTime now = LocalDateTime.now();

        // 1. Find the overdue booking (matching AutomationScheduler's criteria)
        Optional<Booking> overdueBooking = room.getBookings().stream()
                .filter(b -> !b.getStartTime().isAfter(now))
                .filter(b -> now.isAfter(b.getStartTime().plusMinutes(AppConstants.GRACE_PERIOD_MINUTES)))
                .findFirst();

        if (overdueBooking.isPresent()) {
            Booking releasedBooking = overdueBooking.get();

            // 2. Remove the booking from the room's schedule list
            room.removeBooking(releasedBooking);

            // 3. Update room status based on current occupancy and new schedule
            if (room.isOccupied()) {
                room.setStatus(RoomStatus.OCCUPIED);
            } else if (room.isBookedNow()) {
                room.setStatus(RoomStatus.BOOKED);
            } else {
                room.setStatus(RoomStatus.AVAILABLE);
            }

            // 4. Send notification
            getNotificationService().notifyUser(releasedBooking,
                    String.format("Your booking for Room %d (starts %s) was automatically released.",
                            roomNumber, releasedBooking.getStartTime().toLocalTime()));

            AppConstants.LOGGER.info("Booking for Room " + roomNumber + " automatically released.");
            return true;
        }

        return false;
    }

    public synchronized boolean updateRoomOccupancy(int roomNumber, int newCount) {
        Room room = rooms.get(roomNumber);

        if (room == null || newCount < 0) {
            System.out.println("❌ Invalid room number or occupancy count.");
            return false;
        }

        // CHECK: If emergency is active, controls should be handled by the Observer override.
        if (emergencyService.isEmergencyActive()) {
            AppConstants.LOGGER.warning("Occupancy update ignored: Emergency mode active.");
            return true; // Still return true as the sensor read was successful
        }

        room.setOccupancyCount(newCount);

        // Core Logic: Update room status based on current occupancy and scheduling context
        if (room.isOccupied()) {
            room.setStatus(RoomStatus.OCCUPIED);
        } else if (room.isBookedNow()) {
            room.setStatus(RoomStatus.BOOKED);
        } else {
            room.setStatus(RoomStatus.AVAILABLE);
        }

        System.out.println("💡 Sensor update for Room " + roomNumber + ": Count is " + newCount + ". Status: " + room.getStatus() + ".");
        return true;
    }

    // ------------------------------------------------------------------------
    // GETTERS
    // ------------------------------------------------------------------------

    public Collection<Room> getAllRooms() {
        return Collections.unmodifiableCollection(rooms.values());
    }

    public Room getRoom(int roomNumber) {
        return rooms.get(roomNumber);
    }
}
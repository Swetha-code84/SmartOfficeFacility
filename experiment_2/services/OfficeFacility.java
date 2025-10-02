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
import java.util.List; 
import java.util.Map;
import java.util.Optional;

public class OfficeFacility {

    private static OfficeFacility instance;

    private AutomationScheduler scheduler = null;
    private final IAuthenticationService authService;
    private final INotificationService notificationService;
    private final IRoomFactory roomFactory;
    private final EmergencyService emergencyService; 

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

    public IAuthenticationService getAuthenticationService() { return authService; }
    public AutomationScheduler getAutomationScheduler() {
        if (scheduler == null) { scheduler = AutomationScheduler.getInstance(); }
        return scheduler;
    }
    public INotificationService getNotificationService() { return notificationService; }
    public EmergencyService getEmergencyService() { return emergencyService; } 

    public synchronized boolean initialConfigure(int numberOfRooms) {
        if (isConfigured) {
            System.out.println(" X Office facility is already configured. Cannot reconfigure.");
            return false;
        }
        if (numberOfRooms <= 0) {
            System.out.println("X Invalid number of rooms. Must be greater than 0.");
            return false;
        }

        rooms.clear();
        for (int i = 1; i <= numberOfRooms; i++) {
            Room room = roomFactory.createRoom(i);
            rooms.put(i, room);
        }
        isConfigured = true;
        AppConstants.LOGGER.info("Office configured successfully with " + numberOfRooms + " conference rooms.");
        System.out.println(" Office configured successfully with " + numberOfRooms + " conference rooms.");
        return true;
    }

    public synchronized boolean addSingleRoom(int roomNumber) {
        if (rooms.containsKey(roomNumber)) {
            System.out.println("X Room " + roomNumber + " already exists.");
            return false;
        }
        Room room = roomFactory.createRoom(roomNumber);
        rooms.put(roomNumber, room);
        isConfigured = true;
        AppConstants.LOGGER.info("Room " + roomNumber + " added successfully via Admin Menu.");
        System.out.println(" Room " + roomNumber + " added successfully.");
        return true;
    }

    public boolean isConfigured() {
        return isConfigured;
    }

    public synchronized boolean addBookingToRoom(int roomNumber, Booking newBooking) {
        Room room = rooms.get(roomNumber);

        if (room == null) {
            System.out.println(" X Room " + roomNumber + " does not exist.");
            return false;
        }
        room.addBooking(newBooking);

        AppConstants.LOGGER.info("Room " + roomNumber + " booking added by " + newBooking.getUser().getUsername() + ".");
        return true;
    }
    public synchronized String cancelBooking(int roomNumber, User user, LocalDateTime startTime) {
        Room room = rooms.get(roomNumber);

        if (room == null) {
            return " Cancellation Denied: Room " + roomNumber + " not found.";
        }
        Optional<Booking> bookingToCancel = room.getBookings().stream()
                .filter(b -> b.getStartTime().equals(startTime) && b.getUser().equals(user))
                .findFirst();

        if (bookingToCancel.isPresent()) {
            if (room.removeBooking(bookingToCancel.get())) {
                AppConstants.LOGGER.info("Booking cancelled manually for Room " + roomNumber + " at " + startTime + ".");
                updateRoomOccupancy(roomNumber, room.getOccupancyCount());
                return " ✅ Booking for Room " + roomNumber + " at " + startTime.toLocalTime() + " successfully cancelled.";
            }
        }

        if (room.getBookings().isEmpty()) {
            return "X Cancellation Denied: Room " + roomNumber + " has no scheduled bookings.";
        }

        return " X Cancellation Denied: A booking for Room " + roomNumber + " at " + startTime.toLocalTime() + " was not found for your user. Access denied.";
    }

    public synchronized boolean requestExtension(int roomNumber, Booking bookingToExtend, int additionalMinutes) {
        Room room = rooms.get(roomNumber);

        if (room == null) {
            System.out.println(" X Extension failed: Room " + roomNumber + " not found.");
            return false;
        }

        if (!room.checkExtension(bookingToExtend, additionalMinutes)) {
            System.out.println(" X Extension failed: New duration conflicts with an existing reservation.");
            return false;
        }

        LocalDateTime newEndTime = bookingToExtend.getEndTime().plusMinutes(additionalMinutes);
        int newDuration = bookingToExtend.getDurationMinutes() + additionalMinutes;

        Booking extendedBooking = new Booking(
                bookingToExtend.getUser(),
                room,
                bookingToExtend.getStartTime(),
                newDuration
        );

        room.updateBooking(bookingToExtend, extendedBooking);

        getNotificationService().notifyManagerOfExtension(
                "Room " + roomNumber + " extended to end at " + newEndTime.toLocalTime(),
                bookingToExtend.getUser().getUsername(),
                additionalMinutes
        );

        AppConstants.LOGGER.info("Room " + roomNumber + " extended by " + additionalMinutes + " min. New end time: " + newEndTime.toLocalTime());
        return true;
    }

    public synchronized void massCancelAllBookings(String reason) {
        for (Room room : rooms.values()) {
            List<Booking> bookingsToCancel = new java.util.ArrayList<>(room.getBookings());
            for (Booking booking : bookingsToCancel) {
                if (room.removeBooking(booking)) {
                    room.setStatus(RoomStatus.AVAILABLE);

                    String notificationMessage = String.format("EMERGENCY CANCELLATION: Your booking for Room %d (starts %s) has been auto-cancelled. Reason: %s",
                            room.getRoomNumber(), booking.getStartTime().toLocalTime(), reason);

                    getNotificationService().notifyUser(booking, notificationMessage);
                    AppConstants.LOGGER.info("EMERGENCY: Cancelled booking for " + booking.getUser().getUsername() + " in Room " + room.getRoomNumber());
                }
            }
        }
    }

    public synchronized boolean releaseBooking(int roomNumber) {
        Room room = rooms.get(roomNumber);

        if (room == null || room.getBookings().isEmpty()) {
            return false;
        }

        LocalDateTime now = LocalDateTime.now();

        Optional<Booking> overdueBooking = room.getBookings().stream()
                .filter(b -> !b.getStartTime().isAfter(now))
                .filter(b -> now.isAfter(b.getStartTime().plusMinutes(AppConstants.GRACE_PERIOD_MINUTES)))
                .findFirst();

        if (overdueBooking.isPresent()) {
            Booking releasedBooking = overdueBooking.get();

            room.removeBooking(releasedBooking);

            if (room.isOccupied()) {
                room.setStatus(RoomStatus.OCCUPIED);
            } else if (room.isBookedNow()) {
                room.setStatus(RoomStatus.BOOKED);
            } else {
                room.setStatus(RoomStatus.AVAILABLE);
            }

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
            System.out.println(" Invalid room number or occupancy count.");
            return false;
        }

        if (emergencyService.isEmergencyActive()) {
            AppConstants.LOGGER.warning("Occupancy update ignored: Emergency mode active.");
            return true; 
        }

        room.setOccupancyCount(newCount);

        if (room.isOccupied()) {
            room.setStatus(RoomStatus.OCCUPIED);
        } else if (room.isBookedNow()) {
            room.setStatus(RoomStatus.BOOKED);
        } else {
            room.setStatus(RoomStatus.AVAILABLE);
        }

        System.out.println(" Sensor update for Room " + roomNumber + ": Count is " + newCount + ". Status: " + room.getStatus() + ".");
        return true;
    }

    public Collection<Room> getAllRooms() {
        return Collections.unmodifiableCollection(rooms.values());
    }

    public Room getRoom(int roomNumber) {
        return rooms.get(roomNumber);
    }
}

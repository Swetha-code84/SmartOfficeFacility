package com.smartoffice.facility.interfaces;

import com.smartoffice.facility.core.Booking;

/**
 * The INotificationService interface defines the contract for sending notifications
 * (e.g., via Email or SMS) to users about their room bookings.
 * This abstracts the communication details from the facility management logic.
 */
public interface INotificationService {

    /**
     * Sends a notification to the user associated with a booking (Target Method).
     * This is typically used when a booking is automatically released.
     *
     * @param booking The Booking object containing details about the room and the user.
     * @param message The specific message to be delivered to the user.
     * @return true if the notification was successfully processed (e.g., sent or queued), false otherwise.
     */
    boolean notifyUser(Booking booking, String message);

    /**
     * NEW METHOD: Alerts the Manager about a meeting extension request or confirmation.
     * This separates internal system alerts from user notifications.
     *
     * @param roomDetails A string describing the room (e.g., "Room 1 (Original End: 10:00)").
     * @param requestingUser The username requesting the extension.
     * @param extensionMinutes The number of minutes requested to extend.
     */
    void notifyManagerOfExtension(String roomDetails, String requestingUser, int extensionMinutes);
}
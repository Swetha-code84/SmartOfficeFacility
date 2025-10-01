package com.smartoffice.facility.services;

import com.smartoffice.facility.core.Booking;
import com.smartoffice.facility.interfaces.INotificationService;

/**
 * Structural Pattern: Adapter. Converts the interface of ExternalSMSGateway
 * (Adaptee) into the INotificationService (Target) interface that OfficeFacility expects.
 */
public class NotificationServiceAdapter implements INotificationService {

    // 1. Hold a reference to the incompatible service
    private final ExternalSMSGateway legacySmsService;

    public NotificationServiceAdapter(ExternalSMSGateway legacySmsService) {
        this.legacySmsService = legacySmsService;
    }

    // 2. Implement the expected user notification method
    @Override
    public boolean notifyUser(Booking booking, String message) {
        // Prepare the data required by the Adaptee's signature (sendAlert)
        String recipientContact = booking.getUser().getUsername() + "@external.sms";
        String content = "ROOM RELEASED ALERT: " + message;

        System.out.println("✅ [ADAPTER: Translating user notification call]");

        // 3. Translate the call: Adapter calls the Adaptee's unique method
        boolean success = legacySmsService.sendAlert(recipientContact, content);

        return success;
    }

    // 4. FIX: Implement the new method required by the INotificationService interface
    @Override
    public void notifyManagerOfExtension(String roomDetails, String requestingUser, int extensionMinutes) {
        // For this Adapter, we simulate sending an internal alert (e.g., to a Manager Dashboard)
        System.out.println("\n📣 [MANAGER ALERT VIA ADAPTER]");
        System.out.println("   REQUESTER: " + requestingUser);
        System.out.println("   EXTENSION GRANTED: " + extensionMinutes + " minutes.");
        System.out.println("   DETAILS: " + roomDetails);
        System.out.println("----------------------------\n");
    }
}
package com.smartoffice.facility.services;

import com.smartoffice.facility.core.Booking;
import com.smartoffice.facility.interfaces.INotificationService;

public class NotificationServiceAdapter implements INotificationService {

    private final ExternalSMSGateway legacySmsService;

    public NotificationServiceAdapter(ExternalSMSGateway legacySmsService) {
        this.legacySmsService = legacySmsService;
    }

    @Override
    public boolean notifyUser(Booking booking, String message) {
        String recipientContact = booking.getUser().getUsername() + "@external.sms";
        String content = "ROOM RELEASED ALERT: " + message;

        System.out.println(" [ADAPTER: Translating user notification call]");

        boolean success = legacySmsService.sendAlert(recipientContact, content);

        return success;
    }

    @Override
    public void notifyManagerOfExtension(String roomDetails, String requestingUser, int extensionMinutes) {
        System.out.println("\n [MANAGER ALERT VIA ADAPTER]");
        System.out.println("   REQUESTER: " + requestingUser);
        System.out.println("   EXTENSION GRANTED: " + extensionMinutes + " minutes.");
        System.out.println("   DETAILS: " + roomDetails);
        System.out.println("----------------------------\n");
    }
}

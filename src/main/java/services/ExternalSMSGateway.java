package com.smartoffice.facility.services;

/**
 * Structural Pattern: Adapter - The 'Adaptee' (Incompatible Service).
 * This simulates an external service that OfficeFacility needs to use,
 * but which has a method signature different from INotificationService.
 */
public class ExternalSMSGateway {

    /**
     * Simulates the external service's unique method call.
     * @param recipientContact The contact identifier.
     * @param content The text message content.
     */
    public boolean sendAlert(String recipientContact, String content) {
        System.out.println("📱 [ADAPTEE: SMS Gateway] Sending to " + recipientContact + ". Content: " + content);
        // Simulate external API call success
        return true;
    }
}
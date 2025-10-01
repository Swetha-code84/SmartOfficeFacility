package com.smartoffice.facility.services;

public class ExternalSMSGateway {

    public boolean sendAlert(String recipientContact, String content) {
        System.out.println(" [ADAPTEE: SMS Gateway] Sending to " + recipientContact + ". Content: " + content);
        return true;
    }
}

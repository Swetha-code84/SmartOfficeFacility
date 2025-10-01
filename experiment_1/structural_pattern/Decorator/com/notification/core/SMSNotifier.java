package com.notification.core;
public class SMSNotifier implements Notifier {
    @Override
    public void send(String message) {
        System.out.println("CORE NOTIFIER: Sending basic SMS message: '" + message + "'");
    }
}

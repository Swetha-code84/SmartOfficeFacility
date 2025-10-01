package com.notification.core;

// 2. Concrete Component: The basic object we are decorating
public class SMSNotifier implements Notifier {

    @Override
    public void send(String message) {
        System.out.println("CORE NOTIFIER: Sending basic SMS message: '" + message + "'");
    }
}
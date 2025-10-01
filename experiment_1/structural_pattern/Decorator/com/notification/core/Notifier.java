package com.notification.core;

// 1. Component Interface: Defines the common method for all notifiers and decorators
public interface Notifier {
    void send(String message);
}
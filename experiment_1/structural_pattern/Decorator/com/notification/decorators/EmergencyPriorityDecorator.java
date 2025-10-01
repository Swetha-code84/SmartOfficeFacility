package com.notification.decorators;

import com.notification.core.Notifier;

// Concrete Decorator: Controls execution flow based on message content
public class EmergencyPriorityDecorator extends NotifierDecorator {
    private static final String EMERGENCY_KEYWORD = "CRITICAL";

    public EmergencyPriorityDecorator(Notifier wrappedNotifier) {
        super(wrappedNotifier);
    }

    @Override
    public void send(String message) {
        // Check for the high-priority keyword
        if (message.toUpperCase().contains(EMERGENCY_KEYWORD)) {
            System.out.println("\n!!! [PRIORITY BYPASS] Message contains '" + EMERGENCY_KEYWORD + "'. Bypassing standard chain...");

            // Bypass the rest of the chain and call the CORE component directly.
            // We assume the core component is the innermost wrappedNotifier.
            // This is a simplified bypass; a robust implementation would require
            // a reference to the absolute core component.
            Notifier core = findCoreNotifier(this.wrappedNotifier);
            if (core != null) {
                core.send(message);
            } else {
                System.err.println("Could not find core notifier for bypass.");
            }

        } else {
            // Standard flow: Delegate down the rest of the chain
            System.out.println("   [PRIORITY CHECK] Message is standard. Continuing through chain.");
            super.send(message);
        }
    }

    // Helper to find the absolute core component (the one not decorated)
    private Notifier findCoreNotifier(Notifier n) {
        if (n instanceof NotifierDecorator) {
            // Recursively unwrap the decorators
            return findCoreNotifier(((NotifierDecorator) n).wrappedNotifier);
        }
        return n; // Found the core component (e.g., SMSNotifier)
    }
}
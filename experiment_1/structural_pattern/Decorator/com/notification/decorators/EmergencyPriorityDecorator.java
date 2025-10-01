package com.notification.decorators;
import com.notification.core.Notifier;
public class EmergencyPriorityDecorator extends NotifierDecorator {
    private static final String EMERGENCY_KEYWORD = "CRITICAL";
    public EmergencyPriorityDecorator(Notifier wrappedNotifier) {
        super(wrappedNotifier);
    }
    @Override
    public void send(String message) {
        if (message.toUpperCase().contains(EMERGENCY_KEYWORD)) {
            System.out.println("\n!!! [PRIORITY BYPASS] Message contains '" + EMERGENCY_KEYWORD + "'. Bypassing standard chain...");
            Notifier core = findCoreNotifier(this.wrappedNotifier);
            if (core != null) {
                core.send(message);
            } else {
                System.err.println("Could not find core notifier for bypass.");
            }

        } else {
            System.out.println("   [PRIORITY CHECK] Message is standard. Continuing through chain.");
            super.send(message);
        }
    }
    private Notifier findCoreNotifier(Notifier n) {
        if (n instanceof NotifierDecorator) {
            return findCoreNotifier(((NotifierDecorator) n).wrappedNotifier);
        }
        return n; 
    }
}

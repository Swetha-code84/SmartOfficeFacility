package com.notification.decorators;

import com.notification.core.Notifier;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

// 1. Structural Improvement: Wraps the entire chain for central logging.
public class AuditDecorator extends NotifierDecorator {

    public AuditDecorator(Notifier wrappedNotifier) {
        super(wrappedNotifier);
    }

    @Override
    public void send(String message) {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

        System.out.println("\n===== [AUDIT START] Notification Sequence Initiated =====");
        System.out.println("  [AUDIT] Time: " + timestamp);
        System.out.println("  [AUDIT] Original Message: '" + message.substring(0, Math.min(message.length(), 40)) + "...'");

        // Pass the call down the chain
        super.send(message);

        System.out.println("===== [AUDIT END] Chain Execution Finished =====");
    }
}
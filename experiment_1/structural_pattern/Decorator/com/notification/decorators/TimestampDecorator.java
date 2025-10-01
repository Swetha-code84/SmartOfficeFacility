package com.notification.decorators;

import com.notification.core.Notifier;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

// Concrete Decorator 3: Adds a timestamp to the processing output
public class TimestampDecorator extends NotifierDecorator {

    public TimestampDecorator(Notifier wrappedNotifier) {
        super(wrappedNotifier);
    }

    /**
     * Overrides the send method to perform the Timestamp feature
     * before delegating the core notification responsibility.
     */
    @Override
    public void send(String message) {
        // Format the current date and time
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        System.out.println("   [DECORATOR] Timestamp added at: " + timestamp);

        // Pass the original message down the chain to the next decorator or the core component
        super.send(message);
    }
}
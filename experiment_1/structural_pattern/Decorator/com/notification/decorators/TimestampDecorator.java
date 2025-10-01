package com.notification.decorators;
import com.notification.core.Notifier;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
public class TimestampDecorator extends NotifierDecorator {
    public TimestampDecorator(Notifier wrappedNotifier) {
        super(wrappedNotifier);
    }
    @Override
    public void send(String message) {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        System.out.println("   [DECORATOR] Timestamp added at: " + timestamp);
        super.send(message);
    }
}

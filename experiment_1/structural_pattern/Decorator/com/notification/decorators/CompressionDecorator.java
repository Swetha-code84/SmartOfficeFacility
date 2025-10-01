package com.notification.decorators;
import com.notification.core.Notifier;
public class CompressionDecorator extends NotifierDecorator {
    private static final int SMS_LIMIT = 160;
    public CompressionDecorator(Notifier wrappedNotifier) {
        super(wrappedNotifier);
    }
    @Override
    public void send(String message) {
        String compressedMessage = message;
        if (message.length() > SMS_LIMIT) {
            compressedMessage = compress(message);
            System.out.println("   [DECORATOR] Message Compressed. Size reduced from "
                    + message.length() + " to " + compressedMessage.length() + " chars.");
        } else {
            System.out.println("   [DECORATOR] Compression Skipped. Message is below " + SMS_LIMIT + " chars.");
        }

        if (compressedMessage.length() > SMS_LIMIT) {
            String truncatedMessage = compressedMessage.substring(0, SMS_LIMIT);
            System.err.println("   [WARNING] Message STILL exceeds SMS limit! Truncating to " + SMS_LIMIT + " chars.");
            compressedMessage = truncatedMessage;
        }
        super.send(compressedMessage);
    }
    private String compress(String message) {
        return "<ZIPPED:" + message.substring(0, message.length() / 2) + "ENDZ>";
    }
}

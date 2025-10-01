package com.notification.decorators;
import com.notification.core.Notifier;
public class EncryptionDecorator extends NotifierDecorator {
    public EncryptionDecorator(Notifier wrappedNotifier) {
        super(wrappedNotifier);
    }
    @Override
    public void send(String message) {
        String encryptedMessage = encrypt(message);
        System.out.println("   [DECORATOR] Message Encrypted.");
        super.send(encryptedMessage);
    }
    private String encrypt(String message) {
        return "{" + message.hashCode() + "}";
    }
}

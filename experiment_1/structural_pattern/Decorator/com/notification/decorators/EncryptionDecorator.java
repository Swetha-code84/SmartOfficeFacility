package com.notification.decorators;

import com.notification.core.Notifier;

// 4A. Concrete Decorator 1: Adds the Encryption feature
public class EncryptionDecorator extends NotifierDecorator {

    public EncryptionDecorator(Notifier wrappedNotifier) {
        super(wrappedNotifier);
    }

    // Overrides the send method to add pre-processing (Encryption)
    @Override
    public void send(String message) {
        String encryptedMessage = encrypt(message);
        System.out.println("   [DECORATOR] Message Encrypted.");

        // Pass the modified message down the chain
        super.send(encryptedMessage);
    }

    private String encrypt(String message) {
        // Simple mock encryption for demonstration
        return "{" + message.hashCode() + "}";
    }
}
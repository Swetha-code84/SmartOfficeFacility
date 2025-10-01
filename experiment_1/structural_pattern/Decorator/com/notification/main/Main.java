package com.notification.main;

import com.notification.core.Notifier;
import com.notification.core.SMSNotifier;
import com.notification.decorators.AuditDecorator;
import com.notification.decorators.CompressionDecorator;
import com.notification.decorators.EmergencyPriorityDecorator; // NEW IMPORT: Priority Control
import com.notification.decorators.EncryptionDecorator;
import com.notification.decorators.TimestampDecorator;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        // Start with the core component (Innermost layer)
        Notifier notifier = new SMSNotifier();
        String message;

        System.out.println("--- Advanced Decorator Pattern: Notification Service ---");

        // --- STEP 1: Get Message Input ---
        System.out.print("Enter the message content to send (Try 'CRITICAL' for priority bypass): ");
        message = scanner.nextLine();

        // --- STEP 2: Feature Selection (Building the Chain Inner-to-Outer) ---
        System.out.println("\n--- Select Features to Apply (Stacking Decorators) ---");

        // 1. Encryption
        System.out.print("1. Encrypt Message (y/n)? ");
        if (scanner.nextLine().trim().equalsIgnoreCase("y")) {
            notifier = new EncryptionDecorator(notifier);
            System.out.println("  -> Encryption feature added.");
        }

        // 2. Compression (Smart Compression Logic is inside the Decorator)
        System.out.print("2. Compress Message (y/n)? ");
        if (scanner.nextLine().trim().equalsIgnoreCase("y")) {
            notifier = new CompressionDecorator(notifier);
            System.out.println("  -> Compression feature added.");
        }

        // 3. Timestamp
        System.out.print("3. Add Timestamp (y/n)? ");
        if (scanner.nextLine().trim().equalsIgnoreCase("y")) {
            notifier = new TimestampDecorator(notifier);
            System.out.println("  -> Timestamp feature added.");
        }

        // 4. Emergency Priority (Gatekeeper)
        // This must be applied before Audit, but usually after primary features
        System.out.print("4. Add Emergency Priority Control (y/n)? ");
        if (scanner.nextLine().trim().equalsIgnoreCase("y")) {
            notifier = new EmergencyPriorityDecorator(notifier);
            System.out.println("  -> Priority Control added.");
        }

        // 5. Audit Log (OUTERMOST LAYER - Ensures full chain logging)
        notifier = new AuditDecorator(notifier);
        System.out.println("  -> Audit Log ALWAYS added.");

        scanner.close();

        // --- STEP 3: Execute ---
        System.out.println("\n--- Executing Final Notifier Chain ---");

        // The final stacked object (AuditDecorator) is called by the client
        notifier.send(message);

        System.out.println("\n--- Implementation Complete ---");
    }
}
package com.notification.main;
import com.notification.core.Notifier;
import com.notification.core.SMSNotifier;
import com.notification.decorators.AuditDecorator;
import com.notification.decorators.CompressionDecorator;
import com.notification.decorators.EmergencyPriorityDecorator; 
import com.notification.decorators.EncryptionDecorator;
import com.notification.decorators.TimestampDecorator;
import java.util.Scanner;
public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        Notifier notifier = new SMSNotifier();
        String message;
        System.out.println("--- Advanced Decorator Pattern: Notification Service ---");
        System.out.print("Enter the message content to send (Try 'CRITICAL' for priority bypass): ");
        message = scanner.nextLine();
        System.out.println("\n--- Select Features to Apply (Stacking Decorators) ---");
        System.out.print("1. Encrypt Message (y/n)? ");
        if (scanner.nextLine().trim().equalsIgnoreCase("y")) {
            notifier = new EncryptionDecorator(notifier);
            System.out.println("  -> Encryption feature added.");
        }
        System.out.print("2. Compress Message (y/n)? ");
        if (scanner.nextLine().trim().equalsIgnoreCase("y")) {
            notifier = new CompressionDecorator(notifier);
            System.out.println("  -> Compression feature added.");
        }
        System.out.print("3. Add Timestamp (y/n)? ");
        if (scanner.nextLine().trim().equalsIgnoreCase("y")) {
            notifier = new TimestampDecorator(notifier);
            System.out.println("  -> Timestamp feature added.");
        }
        System.out.print("4. Add Emergency Priority Control (y/n)? ");
        if (scanner.nextLine().trim().equalsIgnoreCase("y")) {
            notifier = new EmergencyPriorityDecorator(notifier);
            System.out.println("  -> Priority Control added.");
        }
        notifier = new AuditDecorator(notifier);
        System.out.println("  -> Audit Log ALWAYS added.");
        scanner.close();
        System.out.println("\n--- Executing Final Notifier Chain ---");
        notifier.send(message);
        System.out.println("\n--- Implementation Complete ---");
    }
}

package com.system.singleton.main;

import com.system.singleton.core.HealthMonitor;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) throws InterruptedException {

        System.out.println("--- Singleton Pattern: System Health Monitor (Advanced Control) ---");

        // --- STEP 1: INITIAL ACCESS ---
        HealthMonitor monitor = HealthMonitor.getInstance();
        System.out.println("Verification: Monitor instance acquired successfully.");
        System.out.println("-------------------------------------------------");

        Scanner scanner = new Scanner(System.in);
        int interval = 0;

        // Get initial configuration
        while (interval < 1) {
            System.out.print("Enter initial monitoring interval (seconds, min 1): ");
            try {
                interval = Integer.parseInt(scanner.nextLine());
            } catch (NumberFormatException e) {
                interval = 0;
            }
            if (interval < 1) {
                System.err.println("Invalid input. Interval must be 1 or greater.");
            }
        }

        // Start the monitoring process
        monitor.startMonitoring(interval);

        // --- INTERACTIVE CONTROL LOOP ---
        String command = "";

        while (!command.equalsIgnoreCase("EXIT")) {
            System.out.println("\n--- Control Panel ---");
            System.out.println("Commands: (S)tatus, (P)ause, (R)esume, (U)pdate Interval, (E)XIT");
            System.out.print("Enter command: ");
            command = scanner.nextLine().trim().toUpperCase();

            switch (command) {
                case "S":
                    System.out.println(monitor.getStatusReport());
                    break;
                case "P":
                    monitor.pauseMonitoring();
                    break;
                case "R":
                    monitor.resumeMonitoring();
                    break;
                case "U":
                    System.out.print("Enter NEW interval (seconds): ");
                    try {
                        int newInterval = Integer.parseInt(scanner.nextLine());
                        if (newInterval >= 1) {
                            // Dynamic Configuration Update (Feature 1)
                            monitor.startMonitoring(newInterval);
                        } else {
                            System.err.println("Interval must be >= 1.");
                        }
                    } catch (NumberFormatException e) {
                        System.err.println("Invalid number format.");
                    }
                    break;
                case "EXIT":
                    break;
                default:
                    System.err.println("Unknown command.");
            }
            // Add a small delay to let the monitor thread catch up with commands
            Thread.sleep(100);
        }

        // --- FINAL CLEANUP ---
        monitor.shutdown();
        scanner.close();

        // Wait a moment for threads to clean up
        Thread.sleep(500);
    }
}
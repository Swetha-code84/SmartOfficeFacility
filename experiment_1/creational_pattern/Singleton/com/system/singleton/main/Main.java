package com.system.singleton.main;
import com.system.singleton.core.HealthMonitor;
import java.util.Scanner;
public class Main {
    public static void main(String[] args) throws InterruptedException {
        System.out.println("--- Singleton Pattern: System Health Monitor (Advanced Control) ---");
        HealthMonitor monitor = HealthMonitor.getInstance();
        System.out.println("Verification: Monitor instance acquired successfully.");
        System.out.println("-------------------------------------------------");
        Scanner scanner = new Scanner(System.in);
        int interval = 0;
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
        monitor.startMonitoring(interval);
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
            Thread.sleep(100);
        }
        monitor.shutdown();
        scanner.close();
        Thread.sleep(500);
    }
}

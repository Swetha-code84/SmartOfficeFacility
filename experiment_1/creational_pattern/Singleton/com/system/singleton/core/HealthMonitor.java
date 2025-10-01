package com.system.singleton.core;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

/**
 * Singleton Pattern: System Health Check Scheduler (HealthMonitor)
 * Uses Initialization-on-demand holder idiom for thread-safe lazy initialization.
 */
public class HealthMonitor {

    // --- State and Scheduler Management ---
    private ScheduledExecutorService scheduler;
    private ScheduledFuture<?> currentTask; // Handle to the running task for control (pause/resume)
    private int checkIntervalSeconds;
    private final List<String> statusHistory; // NEW: For Status Reporting
    private boolean isPaused = false; // NEW: For Pause/Resume feature

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("HH:mm:ss");

    // 1. Private Constructor
    private HealthMonitor(int intervalSeconds) {
        this.checkIntervalSeconds = intervalSeconds;
        this.statusHistory = new ArrayList<>(); // Initialize the log
        this.scheduler = Executors.newSingleThreadScheduledExecutor(r -> {
            Thread t = Executors.defaultThreadFactory().newThread(r);
            t.setName("HealthMonitor-Thread"); // Give the thread a name
            return t;
        });

        System.out.println("[MONITOR]: Initialized. Status: Stopped.");
    }

    // 2. Thread-Safe Lazy Holder Class (The Singleton Idiom)
    private static class MonitorHolder {
        private static final HealthMonitor INSTANCE = new HealthMonitor(5); // Default 5 seconds
    }

    // 3. The Global Access Point
    public static HealthMonitor getInstance() {
        return MonitorHolder.INSTANCE;
    }

    // --- ADVANCED FEATURE 2: Status Reporting and Logging ---

    private void performCheck() {
        if (isPaused) {
            return; // Don't perform check if paused
        }

        String time = LocalDateTime.now().format(FORMATTER);
        long memory = Runtime.getRuntime().freeMemory() / (1024 * 1024);
        String status = String.format("System OK. Free Memory: %dMB", memory);

        String logEntry = String.format("[%s]: %s (Interval: %ds)", time, status, checkIntervalSeconds);

        // Update history (manage shared state)
        statusHistory.add(logEntry);
        if (statusHistory.size() > 5) { // Keep only the last 5 entries
            statusHistory.remove(0);
        }

        System.out.println(">>> [HEALTH CHECK]: " + status);
    }

    public String getStatusReport() {
        if (statusHistory.isEmpty()) {
            return "Monitor Status: " + (isPaused ? "Paused" : "Running") + ". No checks performed yet.";
        }

        StringBuilder report = new StringBuilder("=== Last 5 Health Checks ===\n");
        for (String entry : statusHistory) {
            report.append(entry).append("\n");
        }
        report.append("============================");
        return report.toString();
    }

    // --- ADVANCED FEATURE 1: Dynamic Configuration Update (Restarting Scheduler) ---

    public void startMonitoring(int intervalSeconds) {
        if (scheduler.isShutdown()) {
            return;
        }

        if (currentTask != null) {
            // Cancel existing task before starting a new one
            currentTask.cancel(false);
        }

        this.checkIntervalSeconds = intervalSeconds;
        isPaused = false;

        // Schedule the task to run repeatedly, storing the handle
        currentTask = scheduler.scheduleAtFixedRate(this::performCheck, 0, intervalSeconds, TimeUnit.SECONDS);
        System.out.println("[MONITOR]: Monitoring started/updated to " + intervalSeconds + " seconds.");
    }

    // --- ADVANCED FEATURE 3: Resilience (Thread-Safe Pausing/Resuming) ---

    public void pauseMonitoring() {
        if (!isPaused) {
            isPaused = true;
            System.out.println("[MONITOR]: Monitoring temporarily PAUSED. Scheduler thread is still running.");
        }
    }

    public void resumeMonitoring() {
        if (isPaused) {
            isPaused = false;
            System.out.println("[MONITOR]: Monitoring RESUMED. Checks will continue.");
        }
    }

    public void shutdown() {
        if (!scheduler.isShutdown()) {
            scheduler.shutdownNow(); // Aggressively shut down all tasks
            System.out.println("\n[MONITOR]: Shutdown initiated. All checks stopped.");
        }
    }
}
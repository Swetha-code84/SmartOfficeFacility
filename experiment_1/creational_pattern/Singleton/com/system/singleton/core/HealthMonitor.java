package com.system.singleton.core;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
public class HealthMonitor {
    private ScheduledExecutorService scheduler;
    private ScheduledFuture<?> currentTask; 
    private int checkIntervalSeconds;
    private final List<String> statusHistory; 
    private boolean isPaused = false; 
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("HH:mm:ss");
    private HealthMonitor(int intervalSeconds) {
        this.checkIntervalSeconds = intervalSeconds;
        this.statusHistory = new ArrayList<>(); 
        this.scheduler = Executors.newSingleThreadScheduledExecutor(r -> {
            Thread t = Executors.defaultThreadFactory().newThread(r);
            t.setName("HealthMonitor-Thread"); 
            return t;
        });
        System.out.println("[MONITOR]: Initialized. Status: Stopped.");
    }
    private static class MonitorHolder {
        private static final HealthMonitor INSTANCE = new HealthMonitor(5);
    }
    public static HealthMonitor getInstance() {
        return MonitorHolder.INSTANCE;
    }
    private void performCheck() {
        if (isPaused) {
            return; 
        }

        String time = LocalDateTime.now().format(FORMATTER);
        long memory = Runtime.getRuntime().freeMemory() / (1024 * 1024);
        String status = String.format("System OK. Free Memory: %dMB", memory);
        String logEntry = String.format("[%s]: %s (Interval: %ds)", time, status, checkIntervalSeconds);
        statusHistory.add(logEntry);
        if (statusHistory.size() > 5) { 
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
    public void startMonitoring(int intervalSeconds) {
        if (scheduler.isShutdown()) {
            return;
        }

        if (currentTask != null) {
            currentTask.cancel(false);
        }
        this.checkIntervalSeconds = intervalSeconds;
        isPaused = false;
        currentTask = scheduler.scheduleAtFixedRate(this::performCheck, 0, intervalSeconds, TimeUnit.SECONDS);
        System.out.println("[MONITOR]: Monitoring started/updated to " + intervalSeconds + " seconds.");
    }
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

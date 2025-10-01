package com.discount.strategies;

// NEW STRATEGY: Active only for a limited time window
public class FlashSaleStrategy implements IDiscountStrategy {
    private static final double FLASH_RATE = 0.30; // 30% discount
    private final long startTime;
    private final long durationSeconds = 10; // Sale lasts 10 seconds

    public FlashSaleStrategy() {
        this.startTime = System.currentTimeMillis();
    }

    @Override
    public String getName() {
        // Displays the duration to the user
        return "Flash Sale Discount (30% - Active for " + durationSeconds + "s)";
    }

    // --- ADVANCED FEATURE: TIME-BASED GATE ---
    @Override
    public double calculateDiscount(double originalTotal) {
        long currentTime = System.currentTimeMillis();
        long elapsedSeconds = (currentTime - startTime) / 1000;

        if (elapsedSeconds < durationSeconds) {
            // Apply 30% discount if the sale is still active
            return originalTotal * FLASH_RATE;
        } else {
            // Self-deactivation logic
            System.err.println("[FLASH SALE]: ERROR - Sale window expired! Applying zero discount.");
            return 0.00;
        }
    }
}
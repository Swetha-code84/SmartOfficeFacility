package com.discount.strategies;
public class FlashSaleStrategy implements IDiscountStrategy {
    private static final double FLASH_RATE = 0.30; 
    private final long startTime;
    private final long durationSeconds = 10; 
    public FlashSaleStrategy() {
        this.startTime = System.currentTimeMillis();
    }
    @Override
    public String getName() {
        return "Flash Sale Discount (30% - Active for " + durationSeconds + "s)";
    }
    @Override
    public double calculateDiscount(double originalTotal) {
        long currentTime = System.currentTimeMillis();
        long elapsedSeconds = (currentTime - startTime) / 1000;
        if (elapsedSeconds < durationSeconds) {
            return originalTotal * FLASH_RATE;
        } else {
            System.err.println("[FLASH SALE]: ERROR - Sale window expired! Applying zero discount.");
            return 0.00;
        }
    }
}

package com.discount.strategies;

// 3. Concrete Strategy 2: Algorithm based on a fixed sale
public class SeasonalSaleStrategy implements IDiscountStrategy {
    private static final double MIN_TOTAL = 500.00;
    private static final double SALE_RATE = 0.20; // 20% off for large orders

    @Override
    public String getName() {
        return "Seasonal Sale Discount (20% off > $500)";
    }

    // --- ADVANCED FEATURE 1: Contextual Algorithm ---
    @Override
    public double calculateDiscount(double originalTotal) {
        if (originalTotal >= MIN_TOTAL) {
            // Apply 20% discount only if the minimum total is met
            return originalTotal * SALE_RATE;
        } else {
            // No discount applied
            return 0.00;
        }
    }
}
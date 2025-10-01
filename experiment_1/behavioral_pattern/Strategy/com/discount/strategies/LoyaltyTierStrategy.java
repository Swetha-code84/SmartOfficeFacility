package com.discount.strategies;

// 2. Concrete Strategy 1: Algorithm based on customer loyalty
public class LoyaltyTierStrategy implements IDiscountStrategy {
    private static final double LOYALTY_RATE = 0.15; // 15% discount

    @Override
    public String getName() {
        return "Loyalty Tier Discount (15%)";
    }

    @Override
    public double calculateDiscount(double originalTotal) {
        // Calculate 15% discount
        return originalTotal * LOYALTY_RATE;
    }
}
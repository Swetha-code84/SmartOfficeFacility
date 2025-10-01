package com.discount.strategies;
public class LoyaltyTierStrategy implements IDiscountStrategy {
    private static final double LOYALTY_RATE = 0.15; 
    @Override
    public String getName() {
        return "Loyalty Tier Discount (15%)";
    }
    @Override
    public double calculateDiscount(double originalTotal) {
        return originalTotal * LOYALTY_RATE;
    }
}

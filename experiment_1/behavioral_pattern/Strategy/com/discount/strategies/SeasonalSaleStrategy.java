package com.discount.strategies;
public class SeasonalSaleStrategy implements IDiscountStrategy {
    private static final double MIN_TOTAL = 500.00;
    private static final double SALE_RATE = 0.20; 
    @Override
    public String getName() {
        return "Seasonal Sale Discount (20% off > $500)";
    }
    @Override
    public double calculateDiscount(double originalTotal) {
        if (originalTotal >= MIN_TOTAL) {
            return originalTotal * SALE_RATE;
        } else {
            return 0.00;
        }
    }
}

package com.discount.context;
import com.discount.strategies.IDiscountStrategy;
public class ShoppingCart {
    private IDiscountStrategy discountStrategy;
    private double currentTotal;
    public ShoppingCart(double initialTotal) {
        this.currentTotal = initialTotal;
    }
    public void setDiscountStrategy(IDiscountStrategy strategy) {
        this.discountStrategy = strategy;
    }
    public double calculateFinalTotal() {
        if (discountStrategy == null) {
            System.err.println("\n[ERROR]: No discount strategy applied.");
            return currentTotal;
        }
        double discountAmount = discountStrategy.calculateDiscount(currentTotal);
        if (discountAmount > currentTotal) {
            System.err.println("\n[ERROR]: Discount exceeds total! Capping at 100%.");
            discountAmount = currentTotal;
        }
        double finalTotal = currentTotal - discountAmount;
        System.out.println("\n--- FINAL CALCULATION ---");
        System.out.printf("Strategy Applied: %s\n", discountStrategy.getName());
        System.out.printf("Original Total:   $%.2f\n", currentTotal);
        System.out.printf("Discount Applied: $%.2f\n", discountAmount);
        System.out.printf("FINAL PAYABLE:    $%.2f\n", finalTotal);
        System.out.println("-------------------------");

        return finalTotal;
    }
}

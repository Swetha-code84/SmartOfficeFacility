package com.discount.context;

import com.discount.strategies.IDiscountStrategy;

// 4. Context: Maintains a reference to one of the concrete Strategy objects
public class ShoppingCart {
    private IDiscountStrategy discountStrategy;
    private double currentTotal;

    public ShoppingCart(double initialTotal) {
        this.currentTotal = initialTotal;
    }

    // Method to dynamically change the strategy at runtime
    public void setDiscountStrategy(IDiscountStrategy strategy) {
        this.discountStrategy = strategy;
    }

    // Core method that executes the currently set strategy
    public double calculateFinalTotal() {
        if (discountStrategy == null) {
            System.err.println("\n[ERROR]: No discount strategy applied.");
            return currentTotal;
        }

        // 1. Calculate the discount amount by calling the chosen strategy
        double discountAmount = discountStrategy.calculateDiscount(currentTotal);

        // --- ADVANCED FEATURE 2: Validation and Reporting ---
        if (discountAmount > currentTotal) {
            System.err.println("\n[ERROR]: Discount exceeds total! Capping at 100%.");
            discountAmount = currentTotal;
        }

        double finalTotal = currentTotal - discountAmount;

        // --- ADVANCED FEATURE 3: Detailed Reporting ---
        System.out.println("\n--- FINAL CALCULATION ---");
        System.out.printf("Strategy Applied: %s\n", discountStrategy.getName());
        System.out.printf("Original Total:   $%.2f\n", currentTotal);
        System.out.printf("Discount Applied: $%.2f\n", discountAmount);
        System.out.printf("FINAL PAYABLE:    $%.2f\n", finalTotal);
        System.out.println("-------------------------");

        return finalTotal;
    }
}
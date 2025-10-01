package com.discount.main;

import com.discount.context.ShoppingCart;
import com.discount.strategies.FlashSaleStrategy; // New Import
import com.discount.strategies.IDiscountStrategy;
import com.discount.strategies.LoyaltyTierStrategy;
import com.discount.strategies.SeasonalSaleStrategy;

import java.util.InputMismatchException;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        double total = 0.0;
        String choice;

        System.out.println("--- Strategy Pattern: Interactive Discount Calculator ---");

        // --- STEP 1: Get Initial Input ---
        while (total <= 0) {
            System.out.print("Enter initial shopping cart total ($): ");
            try {
                total = scanner.nextDouble();
                scanner.nextLine(); // Consume newline
            } catch (InputMismatchException e) {
                System.err.println("Invalid input. Please enter a number.");
                scanner.nextLine(); // Consume the invalid input
                total = 0;
            }
        }

        // Instantiate the Context
        ShoppingCart cart = new ShoppingCart(total);

        // --- STEP 2: Choose Strategy ---
        System.out.println("\nAvailable Discount Strategies:");
        System.out.println("1: Loyalty Tier (Fixed 15% off)");
        System.out.println("2: Seasonal Sale (20% off, requires $500 minimum)");
        System.out.println("3: FLASH SALE (30% off, expires in 10 seconds!)"); // NEW OPTION ADDED
        System.out.print("Enter choice (1, 2, or 3): ");
        choice = scanner.nextLine().trim();

        IDiscountStrategy chosenStrategy = null;

        switch (choice) {
            case "1":
                chosenStrategy = new LoyaltyTierStrategy();
                break;
            case "2":
                chosenStrategy = new SeasonalSaleStrategy();
                break;
            case "3":
                chosenStrategy = new FlashSaleStrategy(); // NEW STRATEGY INSTANTIATED
                break;
            default:
                System.out.println("[INFO]: Invalid choice. Proceeding without discount.");
        }

        // --- STEP 3: Inject and Execute (Initial Run) ---
        if (chosenStrategy != null) {
            // Dynamically inject the chosen algorithm into the Context
            cart.setDiscountStrategy(chosenStrategy);
            System.out.println("\n*** INITIAL CALCULATION (Within Active Time Window) ***");
            cart.calculateFinalTotal();
        } else {
            cart.calculateFinalTotal(); // Run to show no discount applied
        }

        // --- ADVANCED FEATURE DEMO: TESTING EXPIRED STRATEGY ---
        if (chosenStrategy instanceof FlashSaleStrategy) {
            System.out.println("\n--- DEMO: Testing Expired Strategy ---");
            System.out.println("Pausing thread for 11 seconds to simulate time expiration...");

            try {
                // Pause for 11 seconds (durationSeconds + 1)
                Thread.sleep(11000);
            } catch (InterruptedException e) {
                System.err.println("Thread interrupted during pause.");
                Thread.currentThread().interrupt();
            }

            System.out.println("Time elapsed. Re-calculating with the same Flash Sale strategy...");

            // Re-execute the calculation. The Strategy itself will return 0.00 discount.
            cart.calculateFinalTotal();
        }

        scanner.close();
        System.out.println("\nProcess finished with exit code 0");
    }
}
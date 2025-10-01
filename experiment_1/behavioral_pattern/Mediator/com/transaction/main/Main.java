package com.transaction.main;

import com.transaction.colleagues.InventoryService;
import com.transaction.colleagues.LedgerService;
import com.transaction.colleagues.IServiceColleague;
import com.transaction.mediator.ITransactionCoordinator;
import com.transaction.mediator.TransactionCoordinator;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        String txId;
        String failTrigger = null;

        System.out.println("--- Mediator Pattern: Advanced Transaction Coordinator ---");

        // 1. Setup the Mediator and Colleagues
        ITransactionCoordinator coordinator = new TransactionCoordinator();
        InventoryService inventory = new InventoryService();
        LedgerService ledger = new LedgerService();

        coordinator.registerService(inventory);
        coordinator.registerService(ledger);

        // --- INTERACTIVE SETUP: Configure Failure ---
        System.out.println("\n--- Setup: Configure Failure Condition ---");
        System.out.println("Enter a keyword that will trigger failure in InventoryService.");
        System.out.print("Keyword (e.g., 'FAIL', or leave blank for full success): ");
        failTrigger = scanner.nextLine().trim();

        if (!failTrigger.isEmpty()) {
            System.out.println("[SETUP]: InventoryService will FAIL if Tx ID contains: '" + failTrigger + "'");
        } else {
            System.out.println("[SETUP]: All transactions are set for success.");
        }

        // --- INTERACTIVE TRANSACTION EXECUTION ---
        System.out.println("\n--- Execute Transaction ---");
        System.out.print("Enter Transaction ID (e.g., T-101): ");
        txId = scanner.nextLine().trim();

        // --- ADVANCED FEATURE: Single Source of Truth ---
        System.out.println("\n[CLIENT]: Initiating Two-Phase Commit for Tx ID: " + txId);

        // Pass the failure trigger to the Mediator
        boolean isReady = coordinator.initiateCommit(txId, failTrigger);

        if (isReady) {
            // Mediator commands global COMMIT
            System.out.println("\n[CLIENT - COMMIT STATUS]: All services prepared successfully.");
            coordinator.executeCommit(txId);
        } else {
            // Mediator commands global ROLLBACK
            System.err.println("\n[CLIENT - COMMIT STATUS]: One or more services failed preparation.");
            coordinator.executeRollback(txId);
        }

        scanner.close();
        System.out.println("\n--- Demonstration Complete ---");
    }
}
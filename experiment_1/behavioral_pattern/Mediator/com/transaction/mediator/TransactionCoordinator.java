package com.transaction.mediator;

import com.transaction.colleagues.IServiceColleague;
import java.util.ArrayList;
import java.util.List;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class TransactionCoordinator implements ITransactionCoordinator {

    private final List<IServiceColleague> services = new ArrayList<>();
    private static final DateTimeFormatter LOG_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Override
    public void registerService(IServiceColleague service) {
        services.add(service);
        System.out.println("[COORDINATOR]: Registered service: " + service.getName());
        service.setCoordinator(this);
    }

    @Override
    public boolean initiateCommit(String transactionId, String failTrigger) {
        System.out.println("\n[COORDINATOR - PHASE 1]: Initiating commit for Tx ID: " + transactionId);
        boolean allReady = true;

        // Check if the current transaction ID matches the fail trigger
        boolean globalFailure = (failTrigger != null && transactionId.toUpperCase().contains(failTrigger.toUpperCase()));

        // Ask all services to prepare
        for (IServiceColleague service : services) {

            // --- STRUCTURAL ADVANCEMENT: Mediator decides if the colleague fails ---
            boolean shouldFail = globalFailure && service.getName().equals("InventoryService");

            System.out.println("  -> Querying " + service.getName() + "...");

            // Pass the failure instruction to the colleague
            if (!service.prepare(transactionId, shouldFail)) {
                allReady = false;
                System.err.println("[COORDINATOR]: " + service.getName() + " FAILED preparation. Aborting.");
                break;
            }
        }

        // --- REMOVAL OF LOGIC ERROR: Status is NO LONGER printed here ---
        // String finalState = allReady ? "COMMIT" : "ROLLBACK";
        // recordFinalStatus(transactionId, finalState, services.size()); // <--- REMOVED THIS CALL

        return allReady;
    }

    // (Helper method for old signature)
    @Override
    public boolean initiateCommit(String transactionId) {
        return initiateCommit(transactionId, null);
    }


    @Override
    public void executeCommit(String transactionId) {
        System.out.println("\n[COORDINATOR - PHASE 2]: Global COMMIT issued for Tx ID: " + transactionId);
        for (IServiceColleague service : services) {
            service.commit(transactionId);
        }
        // AFTER PHASE 2 EXECUTION, record the final status
        recordFinalStatus(transactionId, "COMMIT", services.size());
    }

    @Override
    public void executeRollback(String transactionId) {
        System.err.println("\n[COORDINATOR - PHASE 2]: Global ROLLBACK issued for Tx ID: " + transactionId);
        for (IServiceColleague service : services) {
            service.rollback(transactionId);
        }
        // AFTER PHASE 2 EXECUTION, record the final status
        recordFinalStatus(transactionId, "ROLLBACK", services.size());
    }

    /**
     * ADVANCED FEATURE: Generates and prints a final, structured status report.
     * This method is now called *after* Phase 2 completes.
     */
    public void recordFinalStatus(String txId, String finalState, int serviceCount) {
        String time = LocalDateTime.now().format(LOG_FORMATTER);
        String report = String.format(
                "\n*** FINAL TRANSACTION AUDIT REPORT ***" +
                        "\nTIME:        %s" +
                        "\nTX ID:       %s" +
                        "\nSERVICES:    %d" +
                        "\nOUTCOME:     %s",
                time, txId, serviceCount, finalState
        );
        System.out.println(report);
    }
}
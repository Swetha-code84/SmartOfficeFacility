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
        boolean globalFailure = (failTrigger != null && transactionId.toUpperCase().contains(failTrigger.toUpperCase()));
        for (IServiceColleague service : services) {
            boolean shouldFail = globalFailure && service.getName().equals("InventoryService");
            System.out.println("  -> Querying " + service.getName() + "...");
            if (!service.prepare(transactionId, shouldFail)) {
                allReady = false;
                System.err.println("[COORDINATOR]: " + service.getName() + " FAILED preparation. Aborting.");
                break;
            }
        }

        return allReady;
    }
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
        recordFinalStatus(transactionId, "COMMIT", services.size());
    }

    @Override
    public void executeRollback(String transactionId) {
        System.err.println("\n[COORDINATOR - PHASE 2]: Global ROLLBACK issued for Tx ID: " + transactionId);
        for (IServiceColleague service : services) {
            service.rollback(transactionId);
        }
        recordFinalStatus(transactionId, "ROLLBACK", services.size());
    }

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

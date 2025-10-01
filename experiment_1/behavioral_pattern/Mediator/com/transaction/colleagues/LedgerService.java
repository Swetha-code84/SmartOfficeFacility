package com.transaction.colleagues;

import com.transaction.mediator.ITransactionCoordinator;

// Concrete Colleague 2: Simulates financial ledger updates
public class LedgerService implements IServiceColleague {
    private ITransactionCoordinator coordinator;
    private final String name = "LedgerService";

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setCoordinator(ITransactionCoordinator coordinator) {
        this.coordinator = coordinator;
    }

    // FIX: Updated method signature to match the new IServiceColleague interface
    @Override
    public boolean prepare(String transactionId, boolean forceFailure) {
        // We ignore the forceFailure flag here, as only InventoryService is configured to fail

        // Simulates complex check (e.g., checking if the user has sufficient funds)
        System.out.println("  [" + name + "]: Funds validated. Ready to commit.");
        return true;
    }

    @Override
    public void commit(String transactionId) {
        System.out.println("  [" + name + "]: Funds committed (Final Write).");
    }

    @Override
    public void rollback(String transactionId) {
        System.err.println("  [" + name + "]: Funds ROLLBACK (Voided transaction).");
    }
}
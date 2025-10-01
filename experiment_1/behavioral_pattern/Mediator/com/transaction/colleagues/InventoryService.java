package com.transaction.colleagues;
import com.transaction.mediator.ITransactionCoordinator;
public class InventoryService implements IServiceColleague {
    private ITransactionCoordinator coordinator;
    private final String name = "InventoryService";
    @Override
    public String getName() { return name; }
    @Override
    public void setCoordinator(ITransactionCoordinator coordinator) {
        this.coordinator = coordinator;
    }
    @Override
    public boolean prepare(String transactionId, boolean forceFailure) {
        if (forceFailure) {
            System.err.println("  [" + name + "]: Stock check FAILED (Forced by Coordinator).");
            return false;
        }
        System.out.println("  [" + name + "]: Stock reserved. Ready to commit.");
        return true;
    }
    @Override
    public void commit(String transactionId) {
        System.out.println("  [" + name + "]: Stock committed (Final Write).");
    }
    @Override
    public void rollback(String transactionId) {
        System.err.println("  [" + name + "]: Stock ROLLBACK (Released reservation).");
    }
}

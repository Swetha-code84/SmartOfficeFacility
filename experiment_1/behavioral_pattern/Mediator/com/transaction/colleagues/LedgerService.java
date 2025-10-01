package com.transaction.colleagues;
import com.transaction.mediator.ITransactionCoordinator;
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
    @Override
    public boolean prepare(String transactionId, boolean forceFailure) {
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

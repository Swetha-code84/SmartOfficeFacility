package com.transaction.colleagues;
import com.transaction.mediator.ITransactionCoordinator;
public interface IServiceColleague {
    String getName();
    void setCoordinator(ITransactionCoordinator coordinator);
    boolean prepare(String transactionId, boolean forceFailure);
    void commit(String transactionId);
    void rollback(String transactionId);
}

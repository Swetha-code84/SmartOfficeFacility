package com.transaction.colleagues;

import com.transaction.mediator.ITransactionCoordinator;

// Colleague Interface
public interface IServiceColleague {
    String getName();
    void setCoordinator(ITransactionCoordinator coordinator);

    // UPDATED SIGNATURE
    boolean prepare(String transactionId, boolean forceFailure);
    void commit(String transactionId);
    void rollback(String transactionId);
}
package com.transaction.mediator;
import com.transaction.colleagues.IServiceColleague;
public interface ITransactionCoordinator {
    boolean initiateCommit(String transactionId, String failTrigger);
    boolean initiateCommit(String transactionId);
    void executeCommit(String transactionId);
    void executeRollback(String transactionId);
    void registerService(IServiceColleague service);
}

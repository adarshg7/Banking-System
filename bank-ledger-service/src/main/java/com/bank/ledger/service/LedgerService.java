package com.bank.ledger.service;

import java.math.BigDecimal;
import java.util.UUID;

public interface LedgerService {

    void recordDeposit(String transactionReference, UUID accountId, BigDecimal amount, String description);

    void recordWithdrawal(String transactionReference, UUID accountId,BigDecimal amount, String  description);

    void recordTransfer(String transactionReference, UUID fromAccountId, UUID toAccountId, BigDecimal amount, String description);

    BigDecimal getLedgerBalance(UUID accountId);

    boolean verifyTransactionBalances(String transactionReference);
}

package com.bank.common.event;

import lombok.Getter;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
public class TransactionCompletedEvent {
    private final UUID transactionId;
    private final String transactionReference;
    private final String transactionType;
    private final String status;
    private final UUID fromAccountId;
    private final UUID toAccountId;
    private final BigDecimal amount;
    private final UUID initiatedByUserId;

    public TransactionCompletedEvent(UUID transactionId, String transactionReference, String transactionType,
                                     String status, UUID fromAccountId, UUID toAccountId,
                                     BigDecimal amount, UUID initiatedByUserId) {
        this.transactionId = transactionId;
        this.transactionReference = transactionReference;
        this.transactionType = transactionType;
        this.status = status;
        this.fromAccountId = fromAccountId;
        this.toAccountId = toAccountId;
        this.amount = amount;
        this.initiatedByUserId = initiatedByUserId;
    }


}

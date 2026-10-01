package com.bank.transaction.dto.response;

import com.bank.common.enums.Currency;
import com.bank.transaction.enums.TransactionStatus;
import com.bank.transaction.enums.TransactionType;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
public class TransactionResponse {

    private UUID id;
    private String transactionReference;
    private TransactionType transactionType;
    private TransactionStatus status;
    private UUID fromAccountId;
    private UUID toAccountId;
    private BigDecimal amount;
    private Currency currency;
    private BigDecimal balanceAfter;
    private String description;
    private LocalDateTime createdDate;
}
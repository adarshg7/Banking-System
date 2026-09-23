package com.bank.account.dto.request;

import com.bank.account.enums.AccountType;
import com.bank.common.enums.Currency;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
public class AccountOpeningRequest {

    @NotNull(message = "Primary holder user ID is required")
    private UUID primaryUserId;

    @NotNull(message = "Account type is required")
    private AccountType accountType;

    private Currency currency;

    private List<UUID> jointHolderUserIds;

    private String operationMode;

    private UUID guardianUserId;

    private BigDecimal initialDeposit;
}
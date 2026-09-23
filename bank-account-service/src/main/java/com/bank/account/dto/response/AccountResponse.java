package com.bank.account.dto.response;

import com.bank.account.enums.AccountStatus;
import com.bank.account.enums.AccountType;
import com.bank.account.enums.OperationMode;
import com.bank.common.enums.Currency;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
public class AccountResponse {

    private UUID id;
    private String accountNumber;
    private AccountType accountType;
    private AccountStatus status;
    private OperationMode operationMode;
    private BigDecimal balance;
    private Currency currency;
    private String ifscCode;
    private List<AccountHolderResponse> holders;
}
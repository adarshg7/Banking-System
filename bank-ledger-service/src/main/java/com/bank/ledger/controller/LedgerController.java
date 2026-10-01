package com.bank.ledger.controller;

import com.bank.common.response.ApiResponse;
import com.bank.ledger.service.LedgerService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/ledger")
public class LedgerController {

    private final LedgerService ledgerService;

    public LedgerController(LedgerService ledgerService) {
        this.ledgerService = ledgerService;
    }

    /**
     * Demo/audit endpoint: shows the ledger-computed balance for an
     * account, independent of the Account.balance column. In a healthy
     * system these always match — this endpoint is genuinely useful to
     * show in your demo as proof the double-entry system is correct.
     */
    @GetMapping("/balance/{accountId}")
    public ApiResponse<BigDecimal> getLedgerBalance(@PathVariable UUID accountId) {
        BigDecimal balance = ledgerService.getLedgerBalance(accountId);
        return ApiResponse.success(balance, "Ledger-computed balance");
    }
}
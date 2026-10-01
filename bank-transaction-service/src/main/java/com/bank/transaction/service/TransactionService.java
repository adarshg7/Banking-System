package com.bank.transaction.service;

import com.bank.common.response.PageResponse;
import com.bank.transaction.dto.request.DepositRequest;
import com.bank.transaction.dto.request.TransferRequest;
import com.bank.transaction.dto.request.WithdrawalRequest;
import com.bank.transaction.dto.response.TransactionResponse;

import java.util.UUID;

public interface TransactionService {

    TransactionResponse deposit(DepositRequest request);

    TransactionResponse withdraw(WithdrawalRequest request);

    TransactionResponse transfer(TransferRequest request);

    PageResponse<TransactionResponse> getTransactionHistory(UUID accountId, int page, int size);

    TransactionResponse approveTransfer(String transactionReference);

    TransactionResponse rejectTransfer(String transactionReference, String reason);

    PageResponse<TransactionResponse> getPendingApprovals();
}
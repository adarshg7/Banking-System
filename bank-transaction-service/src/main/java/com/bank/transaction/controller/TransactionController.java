package com.bank.transaction.controller;
import com.bank.common.response.ApiResponse;
import com.bank.common.response.PageResponse;
import com.bank.transaction.dto.request.DepositRequest;
import com.bank.transaction.dto.request.RejectRequest;
import com.bank.transaction.dto.request.TransferRequest;
import com.bank.transaction.dto.request.WithdrawalRequest;
import com.bank.transaction.dto.response.TransactionResponse;
import com.bank.transaction.service.TransactionService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.UUID;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@RestController
@RequestMapping("/api/v1/transactions")
public class TransactionController {

    private final TransactionService transactionService;
    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @PostMapping("/deposit")
    public ResponseEntity<ApiResponse<TransactionResponse>> deposit(@Valid @RequestBody DepositRequest request){
        TransactionResponse response = transactionService.deposit(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(response,"Deposit Successful"));
    }

    @PostMapping("/withdraw")
    public ResponseEntity<ApiResponse<TransactionResponse>> withdraw(@Valid @RequestBody WithdrawalRequest request){
        TransactionResponse response = transactionService.withdraw(request);
        return  ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(response,"withdrawal successful"));
    }

    @PostMapping("/transfer")
    public ResponseEntity<ApiResponse<TransactionResponse>> transfer(@Valid @RequestBody TransferRequest request) {
        TransactionResponse response = transactionService.transfer(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(response, "Transfer successful"));
    }

    @GetMapping("/account/{accountId}")
    public ResponseEntity<ApiResponse<PageResponse<TransactionResponse>>> getHistory(
            @PathVariable UUID accountId, @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "20") int size){

        PageResponse<TransactionResponse> response = transactionService.getTransactionHistory(accountId,page,size);
        return ResponseEntity.ok(ApiResponse.success(response,"Transaction history fetched successfully"));
    }

    @PostMapping("/{transactionReference}/approve")
    public ResponseEntity<ApiResponse<TransactionResponse>> approve(
            @PathVariable String transactionReference) {

        TransactionResponse response =
                transactionService.approveTransfer(transactionReference);

        return ResponseEntity.ok(
                ApiResponse.success(response, "Transfer approved and executed")
        );
    }

    @PostMapping("/{transactionReference}/reject")
    public ResponseEntity<ApiResponse<TransactionResponse>> reject(
            @PathVariable String transactionReference,
            @RequestBody RejectRequest request) {

        TransactionResponse response =
                transactionService.rejectTransfer(
                        transactionReference,
                        request.getReason()
                );

        return ResponseEntity.ok(
                ApiResponse.success(response, "Transfer rejected")
        );
    }

    @GetMapping("/pending-approvals")
    public ResponseEntity<ApiResponse<PageResponse<TransactionResponse>>> pendingApprovals() {
        PageResponse<TransactionResponse> response = transactionService.getPendingApprovals();
        return ResponseEntity.ok(ApiResponse.success(response, "Pending approvals fetched"));
    }
}

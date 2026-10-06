package com.bank.transaction.service.impl;

import com.bank.account.entity.Account;
import com.bank.account.enums.AccountStatus;
import com.bank.common.constants.AppConstants;
import com.bank.common.response.PageResponse;
import com.bank.common.util.IdGenerator;
import com.bank.ledger.service.LedgerService;
import com.bank.security.util.SecurityUtils;
import com.bank.transaction.client.AccountLookupClient;
import com.bank.transaction.dto.request.DepositRequest;
import com.bank.transaction.dto.request.TransferRequest;
import com.bank.transaction.dto.request.WithdrawalRequest;
import com.bank.transaction.dto.response.TransactionResponse;
import com.bank.transaction.entity.Transaction;
import com.bank.transaction.enums.ApprovalStatus;
import com.bank.transaction.enums.TransactionStatus;
import com.bank.transaction.enums.TransactionType;
import com.bank.transaction.exception.InsufficientBalanceException;
import com.bank.transaction.exception.InvalidTransactionStateException;
import com.bank.transaction.exception.SelfTransferException;
import com.bank.transaction.exception.TransactionNotFoundException;
import com.bank.transaction.mapper.TransactionMapper;
import com.bank.transaction.repository.TransactionRepository;
import com.bank.transaction.service.TransactionService;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.context.ApplicationEventPublisher;
import com.bank.common.event.TransactionCompletedEvent;

import java.math.BigDecimal;
import java.util.UUID;

@Service
public class TransactionServiceImpl implements TransactionService {
    private final TransactionRepository transactionRepository;
    private final AccountLookupClient accountLookupClient;
    private final TransactionMapper transactionMapper;
    private final LedgerService ledgerService;
    private final ApplicationEventPublisher eventPublisher;

    public TransactionServiceImpl(TransactionRepository transactionRepository,
                                  AccountLookupClient accountLookupClient,
                                  TransactionMapper transactionMapper,LedgerService ledgerService,
                                  ApplicationEventPublisher eventPublisher) {
        this.transactionRepository = transactionRepository;
        this.accountLookupClient = accountLookupClient;
        this.transactionMapper = transactionMapper;
        this.ledgerService = ledgerService;
        this.eventPublisher = eventPublisher;
    }

    @Override
    @Transactional
    public TransactionResponse deposit(DepositRequest request){
        UUID currentUserId = SecurityUtils.getCurrentUserId();
        Account account = accountLookupClient.getAccountForUpdate(request.getAccountId());
        validateAccountIsActive(account);

        BigDecimal newBalance = account.getBalance().add(request.getAmount());
        account.setBalance(newBalance);

        try{
            accountLookupClient.save(account);
        } catch (ObjectOptimisticLockingFailureException e){
            throw new InsufficientBalanceException("This account was just updated by another operation. Please retry the deposit.");
        }

        Transaction transaction = new Transaction();
        transaction.setTransactionReference(IdGenerator.generateTransactionReference());
        transaction.setTransactionType(TransactionType.DEPOSIT);
        transaction.setStatus(TransactionStatus.SUCCESS);
        transaction.setToAccountId(account.getId());
        transaction.setAmount(request.getAmount());
        transaction.setCurrency(account.getCurrency());
        transaction.setBalanceAfter(newBalance);
        transaction.setDescription(request.getDescription());
        transaction.setInitiatedByUserId(currentUserId);

        transaction = transactionRepository.save(transaction);

        eventPublisher.publishEvent(new TransactionCompletedEvent(
                transaction.getId(),
                transaction.getTransactionReference(),
                transaction.getTransactionType().name(),
                transaction.getStatus().name(),
                transaction.getFromAccountId(),
                transaction.getToAccountId(),
                transaction.getAmount(),
                transaction.getInitiatedByUserId()
        ));

        ledgerService.recordDeposit(transaction.getTransactionReference(), account.getId(), request.getAmount(), request.getDescription());
        return transactionMapper.toResponse(transaction);
    }

    @Override
    @Transactional
    public TransactionResponse withdraw(WithdrawalRequest request){
        UUID currentUserId = SecurityUtils.getCurrentUserId();
        Account account = accountLookupClient.getAccountForUpdate(request.getAccountId());
        validateAccountIsActive(account);
        accountLookupClient.verifyUserCanOperate(account.getId(),currentUserId);
        validateSufficientBalance(account,request.getAmount());

        BigDecimal newBalance = account.getBalance().subtract(request.getAmount());
        account.setBalance(newBalance);

        try {
            accountLookupClient.save(account);
        } catch (ObjectOptimisticLockingFailureException e){
            throw new InsufficientBalanceException(
                    "This account was just updated by another operation. Please retry the withdrawal.");
        }

        Transaction transaction = new Transaction();
        transaction.setTransactionReference(IdGenerator.generateTransactionReference());
        transaction.setTransactionType(TransactionType.WITHDRAWAL);
        transaction.setStatus(TransactionStatus.SUCCESS);
        transaction.setFromAccountId(account.getId());
        transaction.setAmount(request.getAmount());
        transaction.setCurrency(account.getCurrency());
        transaction.setBalanceAfter(newBalance);
        transaction.setDescription(request.getDescription());
        transaction.setInitiatedByUserId(currentUserId);

        transaction = transactionRepository.save(transaction);

        eventPublisher.publishEvent(new TransactionCompletedEvent(
                transaction.getId(),
                transaction.getTransactionReference(),
                transaction.getTransactionType().name(),
                transaction.getStatus().name(),
                transaction.getFromAccountId(),
                transaction.getToAccountId(),
                transaction.getAmount(),
                transaction.getInitiatedByUserId()
        ));

        ledgerService.recordWithdrawal(transaction.getTransactionReference(), account.getId(), request.getAmount(), request.getDescription());

        return transactionMapper.toResponse(transaction);
    }

    @Override
    @Transactional
    public TransactionResponse transfer(TransferRequest request) {

        UUID currentUserId = SecurityUtils.getCurrentUserId();

        if (request.getFromAccountId().equals(request.getToAccountId())) {
            throw new SelfTransferException("Cannot transfer money to the same account");
        }

        Account fromAccount = accountLookupClient.getAccountForUpdate(request.getFromAccountId());
        Account toAccount = accountLookupClient.getAccountForUpdate(request.getToAccountId());

        validateAccountIsActive(fromAccount);
        validateAccountIsActive(toAccount);
        accountLookupClient.verifyUserCanOperate(fromAccount.getId(), currentUserId);
        validateSufficientBalance(fromAccount, request.getAmount());

        boolean requiresApproval = request.getAmount()
                .compareTo(BigDecimal.valueOf(AppConstants.MAKER_CHECKER_THRESHOLD)) > 0;

        if (requiresApproval) {
            return createPendingTransfer(request, currentUserId, fromAccount);
        }

        return executeTransferImmediately(request, currentUserId, fromAccount, toAccount);
    }

    /**
     * Amount exceeds the maker-checker threshold — record the request but
     * do NOT move any money yet. A CHECKER/AUTHORIZER must call
     * approveTransfer() before this actually executes.
     */
    private TransactionResponse createPendingTransfer(TransferRequest request, UUID currentUserId, Account fromAccount) {

        Transaction transaction = new Transaction();
        transaction.setTransactionReference(IdGenerator.generateTransactionReference());
        transaction.setTransactionType(TransactionType.TRANSFER);
        transaction.setStatus(TransactionStatus.PENDING_APPROVAL);
        transaction.setApprovalStatus(ApprovalStatus.PENDING);
        transaction.setFromAccountId(request.getFromAccountId());
        transaction.setToAccountId(request.getToAccountId());
        transaction.setAmount(request.getAmount());
        transaction.setCurrency(fromAccount.getCurrency());
        transaction.setDescription(request.getDescription());
        transaction.setInitiatedByUserId(currentUserId);
        // balanceAfter deliberately left null — money hasn't moved yet

        transaction = transactionRepository.save(transaction);

        eventPublisher.publishEvent(new TransactionCompletedEvent(
                transaction.getId(),
                transaction.getTransactionReference(),
                transaction.getTransactionType().name(),
                transaction.getStatus().name(),
                transaction.getFromAccountId(),
                transaction.getToAccountId(),
                transaction.getAmount(),
                transaction.getInitiatedByUserId()
        ));

        return transactionMapper.toResponse(transaction);
    }

    private TransactionResponse executeTransferImmediately(TransferRequest request, UUID currentUserId,
                                                           Account fromAccount, Account toAccount) {

        BigDecimal fromNewBalance = fromAccount.getBalance().subtract(request.getAmount());
        fromAccount.setBalance(fromNewBalance);

        BigDecimal toNewBalance = toAccount.getBalance().add(request.getAmount());
        toAccount.setBalance(toNewBalance);

        try {
            accountLookupClient.save(fromAccount);
            accountLookupClient.save(toAccount);
        } catch (ObjectOptimisticLockingFailureException e) {
            throw new InsufficientBalanceException(
                    "One of the accounts was just updated by another operation. Please retry the transfer.");
        }

        Transaction transaction = new Transaction();
        transaction.setTransactionReference(IdGenerator.generateTransactionReference());
        transaction.setTransactionType(TransactionType.TRANSFER);
        transaction.setStatus(TransactionStatus.SUCCESS);
        transaction.setApprovalStatus(ApprovalStatus.NOT_REQUIRED);
        transaction.setFromAccountId(fromAccount.getId());
        transaction.setToAccountId(toAccount.getId());
        transaction.setAmount(request.getAmount());
        transaction.setCurrency(fromAccount.getCurrency());
        transaction.setBalanceAfter(fromNewBalance);
        transaction.setDescription(request.getDescription());
        transaction.setInitiatedByUserId(currentUserId);

        transaction = transactionRepository.save(transaction);

        eventPublisher.publishEvent(new TransactionCompletedEvent(
                transaction.getId(),
                transaction.getTransactionReference(),
                transaction.getTransactionType().name(),
                transaction.getStatus().name(),
                transaction.getFromAccountId(),
                transaction.getToAccountId(),
                transaction.getAmount(),
                transaction.getInitiatedByUserId()
        ));

        ledgerService.recordTransfer(transaction.getTransactionReference(), fromAccount.getId(),
                toAccount.getId(), request.getAmount(), request.getDescription());

        return transactionMapper.toResponse(transaction);
    }

    private void validateAccountIsActive(Account account) {
        if (account.getStatus() != AccountStatus.ACTIVE) {
            throw new InsufficientBalanceException(
                    "Account " + account.getAccountNumber() + " is not active (status: " + account.getStatus() + ")");
        }
    }

    private void validateSufficientBalance(Account account, BigDecimal amount) {
        BigDecimal balanceAfterDebit = account.getBalance().subtract(amount);
        if (balanceAfterDebit.compareTo(account.getMinimumBalance()) < 0) {
            throw new InsufficientBalanceException(
                    "Insufficient balance. Available: " + account.getBalance()
                            + ", minimum balance required: " + account.getMinimumBalance());
        }
    }

    @Override
    public PageResponse<TransactionResponse> getTransactionHistory(UUID accountId , int page , int size){
        Pageable pageable = PageRequest.of(page,size, Sort.by(Sort.Direction.DESC,"createdDate"));

        Page<Transaction> transactionPage = transactionRepository.findByFromAccountIdOrToAccountId(accountId, accountId, pageable);
        Page<TransactionResponse> responsePage = transactionPage.map(transactionMapper::toResponse);

        return PageResponse.from(responsePage);
    }

    @Override
    @Transactional
    public TransactionResponse approveTransfer(String transactionReference){
        UUID currentUserId = SecurityUtils.getCurrentUserId();

        Transaction transaction = transactionRepository
                .findByTransactionReference(transactionReference)
                .orElseThrow(() ->
                        new TransactionNotFoundException(
                                "Transaction not found: " + transactionReference));

        if (transaction.getApprovalStatus() != ApprovalStatus.PENDING) {
            throw new InvalidTransactionStateException(
                    "Transaction is not pending approval (current status: " + transaction.getApprovalStatus() + ")");
        }

        if (transaction.getInitiatedByUserId().equals(currentUserId)) {
            throw new InvalidTransactionStateException(
                    "The maker of a transaction cannot also approve it (four-eyes principle)");
        }

        Account fromAccount = accountLookupClient.getAccountForUpdate(transaction.getFromAccountId());
        Account toAccount = accountLookupClient.getAccountForUpdate(transaction.getToAccountId());

        validateAccountIsActive(fromAccount);
        validateAccountIsActive(toAccount);
        validateSufficientBalance(fromAccount, transaction.getAmount());

        BigDecimal fromNewBalance = fromAccount.getBalance().subtract(transaction.getAmount());
        fromAccount.setBalance(fromNewBalance);

        BigDecimal toNewBalance = toAccount.getBalance().add(transaction.getAmount());
        toAccount.setBalance(toNewBalance);

        try {
            accountLookupClient.save(fromAccount);
            accountLookupClient.save(toAccount);
        } catch (ObjectOptimisticLockingFailureException e) {
            throw new InsufficientBalanceException(
                    "One of the accounts was just updated by another operation. Please retry the approval.");
        }

        transaction.setStatus(TransactionStatus.SUCCESS);
        transaction.setApprovalStatus(ApprovalStatus.APPROVED);
        transaction.setApprovedByUserId(currentUserId);
        transaction.setApprovedDate(java.time.LocalDateTime.now());
        transaction.setBalanceAfter(fromNewBalance);

        transaction = transactionRepository.save(transaction);

        ledgerService.recordTransfer(transaction.getTransactionReference(), fromAccount.getId(),
                toAccount.getId(), transaction.getAmount(), transaction.getDescription());

        return transactionMapper.toResponse(transaction);
    }


    @Override
    @Transactional
    public TransactionResponse rejectTransfer(String transactionReference, String reason) {

        UUID currentUserId = SecurityUtils.getCurrentUserId();

        Transaction transaction = transactionRepository
                .findByTransactionReference(transactionReference)
                .orElseThrow(() ->
                        new TransactionNotFoundException(
                                "Transaction not found: " + transactionReference));

        if (transaction.getApprovalStatus() != ApprovalStatus.PENDING) {
            throw new InvalidTransactionStateException(
                    "Transaction is not pending approval (current status: " + transaction.getApprovalStatus() + ")");
        }

        transaction.setStatus(TransactionStatus.REJECTED);
        transaction.setApprovalStatus(ApprovalStatus.REJECTED);
        transaction.setApprovedByUserId(currentUserId);
        transaction.setApprovedDate(java.time.LocalDateTime.now());
        transaction.setRejectionReason(reason);
        // No money was ever moved for a pending transfer, so nothing to roll back —
        // this is exactly why we don't debit the account until approval.

        transaction = transactionRepository.save(transaction);
        return transactionMapper.toResponse(transaction);
    }

    @Override
    public PageResponse<TransactionResponse> getPendingApprovals() {
        Pageable pageable = PageRequest.of(0, 50, Sort.by(Sort.Direction.ASC, "createdDate"));
        Page<Transaction> page = transactionRepository.findByStatus(TransactionStatus.PENDING_APPROVAL, pageable);
        return PageResponse.from(page.map(transactionMapper::toResponse));
    }
}

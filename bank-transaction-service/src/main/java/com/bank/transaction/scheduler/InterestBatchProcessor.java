package com.bank.transaction.scheduler;

import com.bank.account.entity.Account;
import com.bank.common.util.IdGenerator;
import com.bank.ledger.service.LedgerService;
import com.bank.transaction.entity.Transaction;
import com.bank.transaction.enums.ApprovalStatus;
import com.bank.transaction.enums.TransactionStatus;
import com.bank.transaction.enums.TransactionType;
import com.bank.transaction.repository.AccountRepositoryInterest;
import com.bank.transaction.repository.TransactionRepository;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Slf4j
@Service
public class InterestBatchProcessor {
    private final AccountRepositoryInterest accountRepository;
    private final TransactionRepository transactionRepository;
    private final LedgerService ledgerService;

    public InterestBatchProcessor(
            AccountRepositoryInterest accountRepository,
            TransactionRepository transactionRepository,
            LedgerService ledgerService) {

        this.accountRepository = accountRepository;
        this.transactionRepository = transactionRepository;
        this.ledgerService = ledgerService;
    }

    @Transactional
    public int processBatch(List<Account> accounts, BigDecimal monthlyRate){
        int creditedCount = 0;

        for(Account account : accounts){
            BigDecimal interest = account.getBalance().multiply(monthlyRate).setScale(2, RoundingMode.HALF_UP);

            if(interest.compareTo(BigDecimal.ZERO) <= 0){
                continue;
            }
            creditInterest(account,interest);
            creditedCount++;
        }
        return creditedCount;
    }

    private void creditInterest(Account account, BigDecimal interestAmount){
        BigDecimal newBalance = account.getBalance().add(interestAmount);

        account.setBalance(newBalance);
        Transaction transaction = new Transaction();

        transaction.setTransactionReference(
                IdGenerator.generateTransactionReference()
        );

        transaction.setTransactionType(
                TransactionType.DEPOSIT
        );

        transaction.setStatus(
                TransactionStatus.SUCCESS
        );

        transaction.setApprovalStatus(
                ApprovalStatus.NOT_REQUIRED
        );

        transaction.setToAccountId(account.getId());

        transaction.setAmount(interestAmount);

        transaction.setCurrency(account.getCurrency());

        transaction.setBalanceAfter(newBalance);

        transaction.setDescription(
                "Monthly interest credit"
        );

        transaction.setInitiatedByUserId(account.getId());

        transactionRepository.save(transaction);

        ledgerService.recordDeposit(
                transaction.getTransactionReference(),
                account.getId(),
                interestAmount,
                "Monthly interest credit"
        );

        log.debug(
                "Interest {} credited to account {}",
                interestAmount,
                account.getAccountNumber()
        );
    }
}

package com.bank.account.service;

import com.bank.account.entity.Account;
import com.bank.account.entity.Transaction;
import com.bank.account.enums.AccountStatus;
import com.bank.account.enums.AccountType;
import com.bank.account.enums.ApprovalStatus;
import com.bank.account.enums.TransactionStatus;
import com.bank.account.enums.TransactionType;
import com.bank.account.repository.AccountRepository;
import com.bank.account.repository.MonthlyInterestCreditRepository;
import com.bank.account.repository.TransactionRepository;
import com.bank.common.util.AppConstants;
import com.bank.common.util.IdGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.YearMonth;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class MonthlyInterestBatchService {

    private final AccountRepository accountRepository;

    private final MonthlyInterestCreditRepository
            monthlyInterestCreditRepository;

    private final TransactionRepository transactionRepository;

    private final LedgerService ledgerService;

    /*
     * Start with 1,000.
     *
     * Increase after benchmarking.
     */
    private static final int BATCH_SIZE = 1_000;

    /*
     * Annual rate:
     *
     * Example:
     * 0.06 = 6%
     */
    private static final BigDecimal MONTHLY_RATE =
            BigDecimal
                    .valueOf(
                            AppConstants.SAVINGS_ANNUAL_INTEREST_RATE
                    )
                    .divide(
                            BigDecimal.valueOf(12),
                            10,
                            RoundingMode.HALF_UP
                    );

    /**
     * Processes ONE batch.
     *
     * One call = one database transaction.
     */
    @Transactional
    public InterestBatchResult processBatch(
            Long lastSequenceId,
            YearMonth interestMonth
    ) {

        /*
         * Fetch accounts INSIDE the transaction.
         *
         * PESSIMISTIC_WRITE locks these accounts.
         */
        List<Account> accounts =
                accountRepository.findInterestEligibleAccounts(
                        AccountType.SAVINGS,
                        AccountStatus.ACTIVE,
                        lastSequenceId,
                        PageRequest.of(0, BATCH_SIZE)
                );

        if (accounts.isEmpty()) {

            return new InterestBatchResult(
                    lastSequenceId,
                    0,
                    0,
                    true
            );
        }

        int processed = 0;
        int credited = 0;

        for (Account account : accounts) {

            processed++;

            BigDecimal interest =
                    calculateInterest(account);

            /*
             * No interest to credit.
             */
            if (interest.compareTo(BigDecimal.ZERO) <= 0) {
                continue;
            }

            boolean success =
                    creditInterest(
                            account,
                            interest,
                            interestMonth
                    );

            if (success) {
                credited++;
            }
        }

        /*
         * Last account in this batch.
         */
        Long newLastSequenceId =
                accounts
                        .get(accounts.size() - 1)
                        .getSequenceId();

        log.info(
                "Interest batch processed. " +
                        "Processed={}, Credited={}, LastSequenceId={}",
                processed,
                credited,
                newLastSequenceId
        );

        return new InterestBatchResult(
                newLastSequenceId,
                processed,
                credited,
                accounts.size() < BATCH_SIZE
        );
    }

    private BigDecimal calculateInterest(
            Account account
    ) {

        return account
                .getBalance()
                .multiply(MONTHLY_RATE)
                .setScale(
                        4,
                        RoundingMode.HALF_UP
                );
    }

    private boolean creditInterest(
            Account account,
            BigDecimal interestAmount,
            YearMonth interestMonth
    ) {

        String month =
                interestMonth.toString();

        /*
         * Same reference every time for the same
         * account + month.
         *
         * Example:
         *
         * INT-2026-09-<account-id>
         */
        String transactionReference =
                "INT-" +
                        interestMonth +
                        "-" +
                        account.getId();

        /*
         * Try to claim this interest operation.
         *
         * 1 = newly inserted
         * 0 = already exists
         */
        UUID interestCreditId =
                UUID.randomUUID();

        int claimed =
                monthlyInterestCreditRepository
                        .claimInterestCredit(
                                interestCreditId,
                                account.getId(),
                                month,
                                interestAmount,
                                transactionReference
                        );

        /*
         * Already processed.
         */
        if (claimed == 0) {

            log.debug(
                    "Interest already credited. " +
                            "Account={}, Month={}",
                    account.getAccountNumber(),
                    month
            );

            return false;
        }

        /*
         * Calculate new balance.
         *
         * Account is locked by PESSIMISTIC_WRITE.
         */
        BigDecimal newBalance =
                account
                        .getBalance()
                        .add(interestAmount);

        account.setBalance(newBalance);

        /*
         * Save account.
         */
        accountRepository.save(account);

        /*
         * Create transaction.
         */
        Transaction transaction =
                new Transaction();

        transaction.setTransactionReference(
                transactionReference
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

        transaction.setToAccountId(
                account.getId()
        );

        transaction.setAmount(
                interestAmount
        );

        transaction.setCurrency(
                account.getCurrency()
        );

        transaction.setBalanceAfter(
                newBalance
        );

        transaction.setDescription(
                "Monthly interest credit for " +
                        interestMonth
        );

        /*
         * This is SYSTEM generated.
         *
         * Do NOT put account.getId()
         * here because account ID != user ID.
         */
        transaction.setInitiatedByUserId(null);

        transactionRepository.save(transaction);

        /*
         * Record ledger entry.
         */
        ledgerService.recordDeposit(
                transactionReference,
                account.getId(),
                interestAmount,
                "Monthly interest credit for " +
                        interestMonth
        );

        log.debug(
                "Interest credited. " +
                        "Account={}, Amount={}, NewBalance={}, Month={}",
                account.getAccountNumber(),
                interestAmount,
                newBalance,
                interestMonth
        );

        return true;
    }

    public record InterestBatchResult(
            Long lastSequenceId,
            int processed,
            int credited,
            boolean lastBatch
    ) {
    }
}
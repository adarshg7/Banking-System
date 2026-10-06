package com.bank.transaction.scheduler;

import com.bank.account.entity.Account;
import com.bank.account.enums.AccountStatus;
import com.bank.account.enums.AccountType;
import com.bank.account.repository.AccountRepository;
import com.bank.common.constants.AppConstants;
import com.bank.transaction.repository.AccountRepositoryInterest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.UUID;

@Slf4j
@Component
public class InterestCalculationJob {

    private static final int BATCH_SIZE = 10_000;

    private static final BigDecimal MONTHLY_RATE =
            BigDecimal.valueOf(
                    AppConstants.SAVINGS_ANNUAL_INTEREST_RATE
            ).divide(
                    BigDecimal.valueOf(12),
                    10,
                    RoundingMode.HALF_UP
            );

    private final AccountRepositoryInterest accountRepositoryInterest;
    private final InterestBatchProcessor interestBatchProcessor;

    public InterestCalculationJob(
            AccountRepositoryInterest accountRepositoryInterest,
            InterestBatchProcessor interestBatchProcessor) {

        this.accountRepositoryInterest = accountRepositoryInterest;
        this.interestBatchProcessor = interestBatchProcessor;
    }

    @Scheduled(cron = "0 0 1 1 * *")
    public void creditMonthlyInterest() {

        log.info("Starting monthly interest calculation job");

        UUID lastId = null;

        long totalProcessed = 0;
        long totalCredited = 0;

        while (true) {

            List<Account> accounts =
                    accountRepositoryInterest.findInterestEligibleAccounts(
                            AccountType.SAVINGS,
                            AccountStatus.ACTIVE,
                            lastId,
                            PageRequest.of(0, BATCH_SIZE)
                    );

            if (accounts.isEmpty()) {
                break;
            }

            int creditedInBatch =
                    interestBatchProcessor.processBatch(
                            accounts,
                            MONTHLY_RATE
                    );

            totalProcessed += accounts.size();
            totalCredited += creditedInBatch;


            lastId =
                    accounts.get(accounts.size() - 1).getId();

            log.info(
                    "Interest batch completed. " +
                            "Batch size: {}, " +
                            "Credited: {}, " +
                            "Total processed: {}, " +
                            "Total credited: {}",
                    accounts.size(),
                    creditedInBatch,
                    totalProcessed,
                    totalCredited
            );
        }

        log.info(
                "Monthly interest job completed. " +
                        "Total processed: {}, " +
                        "Total credited: {}",
                totalProcessed,
                totalCredited
        );
    }
}
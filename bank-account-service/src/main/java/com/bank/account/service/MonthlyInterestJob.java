package com.bank.account.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.YearMonth;

@Service
@RequiredArgsConstructor
@Slf4j
public class MonthlyInterestJob {

    private final MonthlyInterestBatchService
            monthlyInterestBatchService;

    /*
     * Runs at:
     *
     * 01:00 AM
     * on the 1st day
     * of every month.
     */
    @Scheduled(cron = "0 0 1 1 * *")
    public void creditMonthlyInterest() {

        YearMonth interestMonth =
                YearMonth.now().minusMonths(1);

        log.info(
                "Starting monthly interest job for {}",
                interestMonth
        );

        Long lastSequenceId = 0L;

        long totalProcessed = 0;
        long totalCredited = 0;

        while (true) {

            MonthlyInterestBatchService
                    .InterestBatchResult result =
                    monthlyInterestBatchService
                            .processBatch(
                                    lastSequenceId,
                                    interestMonth
                            );

            totalProcessed +=
                    result.processed();

            totalCredited +=
                    result.credited();

            /*
             * No more accounts.
             */
            if (result.processed() == 0) {
                break;
            }

            /*
             * Move cursor.
             */
            lastSequenceId =
                    result.lastSequenceId();

            /*
             * Last batch.
             */
            if (result.lastBatch()) {
                break;
            }
        }

        log.info(
                "Monthly interest job completed. " +
                        "Month={}, Processed={}, Credited={}",
                interestMonth,
                totalProcessed,
                totalCredited
        );
    }
}
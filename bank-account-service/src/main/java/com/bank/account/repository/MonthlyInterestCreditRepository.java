package com.bank.account.repository;

import com.bank.account.entity.MonthlyInterestCredit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.UUID;

public interface MonthlyInterestCreditRepository
        extends JpaRepository<MonthlyInterestCredit, UUID> {

    @Modifying
    @Query(value = """
        INSERT IGNORE INTO monthly_interest_credits
        (
            id,
            account_id,
            interest_month,
            interest_amount,
            transaction_reference
        )
        VALUES
        (
            :id,
            :accountId,
            :interestMonth,
            :interestAmount,
            :transactionReference
        )
        """, nativeQuery = true)
    int claimInterestCredit(
            @Param("id")
            UUID id,

            @Param("accountId")
            UUID accountId,

            @Param("interestMonth")
            String interestMonth,

            @Param("interestAmount")
            BigDecimal interestAmount,

            @Param("transactionReference")
            String transactionReference
    );
}
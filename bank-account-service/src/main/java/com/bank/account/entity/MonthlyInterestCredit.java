package com.bank.account.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.UUID;

@Entity
@Table(
        name = "monthly_interest_credits",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_interest_account_month",
                        columnNames = {
                                "account_id",
                                "interest_month"
                        }
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
public class MonthlyInterestCredit {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "account_id", nullable = false)
    private UUID accountId;

    /*
     * Example:
     * 2026-09
     * 2026-10
     */
    @Column(
            name = "interest_month",
            nullable = false,
            length = 7
    )
    private String interestMonth;

    @Column(
            name = "interest_amount",
            nullable = false,
            precision = 19,
            scale = 4
    )
    private BigDecimal interestAmount;

    @Column(
            name = "transaction_reference",
            nullable = false,
            unique = true,
            length = 100
    )
    private String transactionReference;

    public MonthlyInterestCredit(
            UUID accountId,
            YearMonth interestMonth,
            BigDecimal interestAmount,
            String transactionReference
    ) {
        this.accountId = accountId;
        this.interestMonth = interestMonth.toString();
        this.interestAmount = interestAmount;
        this.transactionReference = transactionReference;
    }
}
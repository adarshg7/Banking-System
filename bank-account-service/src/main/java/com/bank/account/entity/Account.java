package com.bank.account.entity;

import com.bank.account.enums.AccountStatus;
import com.bank.account.enums.AccountType;
import com.bank.account.enums.OperationMode;
import com.bank.common.audit.AuditListener;
import com.bank.common.audit.Auditable;
import com.bank.common.enums.Currency;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Design notes:
 * - balance uses BigDecimal, NEVER double/float. Floating point types
 *   introduce rounding errors that are unacceptable for money — this is
 *   a non-negotiable rule in any financial system, and a very common
 *   interview question ("why not use double for currency?").
 * - version field enables OPTIMISTIC LOCKING (@Version). If two
 *   concurrent requests try to update the same account's balance at the
 *   same time, Hibernate detects the conflict via this version number
 *   and throws OptimisticLockException on the second write, rather than
 *   silently corrupting the balance. This is critical for correctness
 *   under concurrent transfers — we'll rely on this heavily in
 *   bank-transaction-service.
 * - We do NOT store fromAccount/toAccount here. This entity represents
 *   the ACCOUNT ITSELF, not a movement of money. Movements belong in
 *   bank-transaction-service / bank-ledger-service.
 */
@Entity
@Table(name = "accounts")
@EntityListeners({AuditingEntityListener.class, AuditListener.class})
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Account extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "account_number", nullable = false, unique = true, length = 12)
    private String accountNumber;

    @Enumerated(EnumType.STRING)
    @Column(name = "account_type", nullable = false)
    private AccountType accountType;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    @Builder.Default
    private AccountStatus status = AccountStatus.ACTIVE;

    @Enumerated(EnumType.STRING)
    @Column(name = "operation_mode", nullable = false)
    @Builder.Default
    private OperationMode operationMode = OperationMode.SINGLE;

    @Column(name = "balance", nullable = false, precision = 19, scale = 4)
    @Builder.Default
    private BigDecimal balance = BigDecimal.ZERO;

    @Enumerated(EnumType.STRING)
    @Column(name = "currency", nullable = false)
    @Builder.Default
    private Currency currency = Currency.INR;

    @Column(name = "minimum_balance", nullable = false, precision = 19, scale = 4)
    @Builder.Default
    private BigDecimal minimumBalance = BigDecimal.ZERO;

    @Column(name = "daily_transfer_limit", precision = 19, scale = 4)
    private BigDecimal dailyTransferLimit;

    @Column(name = "ifsc_code", length = 11)
    @Builder.Default
    private String ifscCode = "BANK0001234"; // simulated single-branch IFSC for this project

    @Column(name = "closed_date")
    private java.time.LocalDateTime closedDate;

    @Version
    private Long version; // optimistic locking — see class-level note above
}
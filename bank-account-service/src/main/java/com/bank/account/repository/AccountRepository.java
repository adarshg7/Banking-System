package com.bank.account.repository;

import com.bank.account.entity.Account;
import com.bank.account.enums.AccountStatus;
import com.bank.account.enums.AccountType;
import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AccountRepository extends JpaRepository<Account, UUID> {
    Optional<Account> findByAccountNumber(String accountNumber);

    boolean existsByAccountNumber(String accountNumber);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
        SELECT a
        FROM Account a
        WHERE a.accountType = :accountType
          AND a.status = :status
          AND a.balance > 0
          AND a.sequenceId > :lastSequenceId
        ORDER BY a.sequenceId ASC
    """)
    List<Account> findInterestEligibleAccounts(
            @Param("accountType")
            AccountType accountType,

            @Param("status")
            AccountStatus status,

            @Param("lastSequenceId")
            Long lastSequenceId,

            Pageable pageable
    );
}
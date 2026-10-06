package com.bank.transaction.repository;

import com.bank.account.entity.Account;
import com.bank.account.enums.AccountStatus;
import com.bank.account.enums.AccountType;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface AccountRepositoryInterest extends JpaRepository<Account, UUID> {
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
            @Param("accountType") AccountType accountType,
            @Param("status") AccountStatus status,
            @Param("lastSequenceId") Long lastSequenceId,
            Pageable pageable
    );
}

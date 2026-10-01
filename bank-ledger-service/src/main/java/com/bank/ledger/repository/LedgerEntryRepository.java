package com.bank.ledger.repository;

import com.bank.ledger.entity.LedgerEntry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public interface LedgerEntryRepository extends JpaRepository<LedgerEntry, UUID> {

    List<LedgerEntry> findByTransactionReference(String transactionReference);
    List<LedgerEntry> findByLedgerAccountIdOrderByCreatedDateDesc(UUID ledgerAccountId);

    @Query("SELECT COALESCE(SUM(le.signedAmount), 0) FROM LedgerEntry le WHERE le.ledgerAccountId = :accountId")
    BigDecimal computeBalanceFromLedger(@Param("accountId") UUID accountId);}

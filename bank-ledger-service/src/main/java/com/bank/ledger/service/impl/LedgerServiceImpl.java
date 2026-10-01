package com.bank.ledger.service.impl;

import com.bank.ledger.entity.LedgerEntry;
import com.bank.ledger.enums.EntryType;
import com.bank.ledger.enums.LedgerAccountType;
import com.bank.ledger.exception.LedgerImbalanceException;
import com.bank.ledger.repository.LedgerEntryRepository;
import com.bank.ledger.service.LedgerService;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
public class LedgerServiceImpl implements LedgerService {

    private static final UUID SUSPENSE_ACCOUNT_ID = UUID.fromString("00000000-0000-0000-0000-000000000001");

    private final LedgerEntryRepository ledgerEntryRepository;

    public LedgerServiceImpl(LedgerEntryRepository ledgerEntryRepository) {
        this.ledgerEntryRepository = ledgerEntryRepository;
    }

    @Override
    @Transactional
    public void recordDeposit(String transactionReference, UUID accountId, BigDecimal amount, String description) {
        LedgerEntry credit = buildEntry(transactionReference, accountId, LedgerAccountType.CUSTOMER_ACCOUNT,
                EntryType.CREDIT, amount, description);

        LedgerEntry debit = buildEntry(transactionReference, SUSPENSE_ACCOUNT_ID, LedgerAccountType.BANK_SUSPENSE,
                EntryType.DEBIT, amount.negate(), description);

        ledgerEntryRepository.save(credit);
        ledgerEntryRepository.save(debit);

        assertBalanced(transactionReference);
    }

    @Override
    @Transactional
    public void recordWithdrawal(String transactionReference, UUID accountId, BigDecimal amount, String description) {
        LedgerEntry debit = buildEntry(transactionReference, accountId, LedgerAccountType.CUSTOMER_ACCOUNT,
                EntryType.DEBIT, amount.negate(), description);

        LedgerEntry credit = buildEntry(transactionReference, SUSPENSE_ACCOUNT_ID, LedgerAccountType.BANK_SUSPENSE,
                EntryType.CREDIT, amount, description);

        ledgerEntryRepository.save(debit);
        ledgerEntryRepository.save(credit);

        assertBalanced(transactionReference);
    }

    @Override
    @Transactional
    public void recordTransfer(String transactionReference, UUID fromAccountId, UUID toAccountId,
                               BigDecimal amount, String description) {
        LedgerEntry debit = buildEntry(transactionReference, fromAccountId, LedgerAccountType.CUSTOMER_ACCOUNT,
                EntryType.DEBIT, amount.negate(), description);

        LedgerEntry credit = buildEntry(transactionReference, toAccountId, LedgerAccountType.CUSTOMER_ACCOUNT,
                EntryType.CREDIT, amount, description);

        ledgerEntryRepository.save(debit);
        ledgerEntryRepository.save(credit);

        assertBalanced(transactionReference);
    }

    @Override
    public BigDecimal getLedgerBalance(UUID accountId) {
        return ledgerEntryRepository.computeBalanceFromLedger(accountId);
    }

    @Override
    public boolean verifyTransactionBalances(String transactionReference) {
        List<LedgerEntry> entries = ledgerEntryRepository.findByTransactionReference(transactionReference);
        BigDecimal sum = entries.stream()
                .map(LedgerEntry::getSignedAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        return sum.compareTo(BigDecimal.ZERO) == 0;
    }


    private void assertBalanced(String transactionReference) {
        if (!verifyTransactionBalances(transactionReference)) {
            throw new LedgerImbalanceException(
                    "Ledger entries for transaction " + transactionReference + " do not sum to zero. " +
                            "This indicates a critical bug and must be investigated immediately.");
        }
    }

    private LedgerEntry buildEntry(String transactionReference, UUID accountId, LedgerAccountType accountType,
                                   EntryType entryType, BigDecimal signedAmount, String description) {
        LedgerEntry entry = new LedgerEntry();
        entry.setTransactionReference(transactionReference);
        entry.setLedgerAccountId(accountId);
        entry.setLedgerAccountType(accountType);
        entry.setEntryType(entryType);
        entry.setSignedAmount(signedAmount);
        entry.setDescription(description);
        return entry;
    }

}



package com.bank.transaction.client;

import com.bank.account.entity.Account;
import com.bank.account.entity.AccountHolder;
import com.bank.account.repository.AccountHolderRepository;
import com.bank.account.repository.AccountRepository;
import com.bank.transaction.exception.UnauthorizedAccountAccessException;
import com.bank.account.exception.AccountNotFoundException;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
public class AccountLookupClient {

    private final AccountRepository accountRepository;
    private final AccountHolderRepository accountHolderRepository;

    public AccountLookupClient(AccountRepository accountRepository,
                               AccountHolderRepository accountHolderRepository) {
        this.accountRepository = accountRepository;
        this.accountHolderRepository = accountHolderRepository;
    }

    public Account getAccountForUpdate(UUID accountId) {
        return accountRepository.findById(accountId)
                .orElseThrow(() -> new AccountNotFoundException("Account not found with id: " + accountId));
    }

    public void save(Account account) {
        accountRepository.save(account);
    }

    public void verifyUserCanOperate(UUID accountId, UUID userId) {
        List<AccountHolder> holders = accountHolderRepository.findByAccountId(accountId);

        boolean allowed = holders.stream()
                .anyMatch(h -> h.getUserId().equals(userId) && h.isCanOperate());

        if (!allowed) {
            throw new UnauthorizedAccountAccessException(
                    "User " + userId + " is not authorized to operate account " + accountId);
        }
    }
}
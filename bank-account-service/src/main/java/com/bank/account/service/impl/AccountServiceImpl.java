package com.bank.account.service.impl;

import com.bank.account.client.UserLookupClient;
import com.bank.account.dto.request.AccountOpeningRequest;
import com.bank.account.dto.response.AccountResponse;
import com.bank.account.entity.Account;
import com.bank.account.entity.AccountHolder;
import com.bank.account.enums.AccountStatus;
import com.bank.account.enums.AccountType;
import com.bank.account.enums.HolderRelation;
import com.bank.account.enums.OperationMode;
import com.bank.account.exception.AccountNotFoundException;
import com.bank.account.exception.InvalidAccountOperationException;
import com.bank.account.mapper.AccountMapper;
import com.bank.account.repository.AccountHolderRepository;
import com.bank.account.repository.AccountRepository;
import com.bank.account.service.AccountService;
import com.bank.common.constants.AppConstants;
import com.bank.common.util.IdGenerator;
import com.bank.user.dto.response.UserResponse;
import com.bank.user.enums.CustomerType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class AccountServiceImpl implements AccountService {

    private final AccountRepository accountRepository;
    private final AccountHolderRepository accountHolderRepository;
    private final UserLookupClient userLookupClient;
    private final AccountMapper accountMapper;

    public AccountServiceImpl(AccountRepository accountRepository,
                              AccountHolderRepository accountHolderRepository,
                              UserLookupClient userLookupClient,
                              AccountMapper accountMapper) {
        this.accountRepository = accountRepository;
        this.accountHolderRepository = accountHolderRepository;
        this.userLookupClient = userLookupClient;
        this.accountMapper = accountMapper;
    }

    @Override
    @Transactional
    public AccountResponse openAccount(AccountOpeningRequest request) {
        UserResponse primaryUser = userLookupClient.getUser(request.getPrimaryUserId());

        Account account = buildAccount(request, primaryUser);
        account = accountRepository.save(account);

        List<AccountHolder> holders = buildHolders(request, primaryUser, account.getId());
        accountHolderRepository.saveAll(holders);

        return accountMapper.toAccountResponse(account, holders);
    }

    @Override
    public AccountResponse getAccountById(UUID accountId) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new AccountNotFoundException("Account not found with id: " + accountId));
        List<AccountHolder> holders = accountHolderRepository.findByAccountId(accountId);
        return accountMapper.toAccountResponse(account, holders);
    }

    @Override
    public AccountResponse getAccountByNumber(String accountNumber) {
        Account account = accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new AccountNotFoundException("Account not found: " + accountNumber));
        List<AccountHolder> holders = accountHolderRepository.findByAccountId(account.getId());
        return accountMapper.toAccountResponse(account, holders);
    }

    private Account buildAccount(AccountOpeningRequest request, UserResponse primaryUser) {
        validateCustomerTypeMatchesRequest(request, primaryUser);

        Account account = new Account();
        account.setAccountNumber(generateUniqueAccountNumber());
        account.setAccountType(request.getAccountType());
        account.setStatus(AccountStatus.ACTIVE);
        if (request.getCurrency() != null) {
            account.setCurrency(request.getCurrency());
        }

        BigDecimal minBalance = resolveMinimumBalance(request.getAccountType());
        account.setMinimumBalance(minBalance);

        BigDecimal initialDeposit = request.getInitialDeposit() != null ? request.getInitialDeposit() : BigDecimal.ZERO;
        if (initialDeposit.compareTo(minBalance) < 0) {
            throw new InvalidAccountOperationException(
                    "Initial deposit (" + initialDeposit + ") is below the minimum balance requirement (" + minBalance + ") for " + request.getAccountType());
        }
        account.setBalance(initialDeposit);
        account.setOperationMode(resolveOperationMode(request, primaryUser));

        boolean isBusiness = primaryUser.getCustomerType() == CustomerType.BUSINESS
                || primaryUser.getCustomerType() == CustomerType.CORPORATE;
        account.setDailyTransferLimit(BigDecimal.valueOf(
                isBusiness ? AppConstants.DAILY_TRANSFER_LIMIT_BUSINESS : AppConstants.DAILY_TRANSFER_LIMIT_INDIVIDUAL));

        return account;
    }

    private void validateCustomerTypeMatchesRequest(AccountOpeningRequest request, UserResponse primaryUser) {
        if (primaryUser.getCustomerType() == CustomerType.MINOR && request.getGuardianUserId() == null) {
            throw new InvalidAccountOperationException("Guardian user ID is required to open an account for a MINOR customer");
        }
        if (primaryUser.getCustomerType() == CustomerType.JOINT
                && (request.getJointHolderUserIds() == null || request.getJointHolderUserIds().isEmpty())) {
            throw new InvalidAccountOperationException("At least one joint holder is required for a JOINT account");
        }
    }

    private OperationMode resolveOperationMode(AccountOpeningRequest request, UserResponse primaryUser) {
        if (primaryUser.getCustomerType() == CustomerType.JOINT) {
            if (request.getOperationMode() == null) {
                throw new InvalidAccountOperationException("Operation mode is required for JOINT accounts");
            }
            try {
                return OperationMode.valueOf(request.getOperationMode());
            } catch (IllegalArgumentException e) {
                throw new InvalidAccountOperationException("Invalid operation mode: " + request.getOperationMode());
            }
        }
        return OperationMode.SINGLE;
    }

    private BigDecimal resolveMinimumBalance(AccountType accountType) {
        return switch (accountType) {
            case SAVINGS -> BigDecimal.valueOf(AppConstants.MIN_SAVING_BALANCE);
            case CURRENT -> BigDecimal.valueOf(AppConstants.MIN_CURRENT_BALANCE);
            default -> BigDecimal.ZERO;
        };
    }

    private List<AccountHolder> buildHolders(AccountOpeningRequest request, UserResponse primaryUser, UUID accountId) {
        List<AccountHolder> holders = new ArrayList<>();

        AccountHolder ownerHolder = new AccountHolder();
        ownerHolder.setAccountId(accountId);
        ownerHolder.setUserId(primaryUser.getId());
        ownerHolder.setHolderRelation(HolderRelation.OWNER);
        ownerHolder.setPrimaryHolder(true);
        ownerHolder.setCanOperate(primaryUser.getCustomerType() != CustomerType.MINOR);
        holders.add(ownerHolder);

        if (primaryUser.getCustomerType() == CustomerType.MINOR) {
            UserResponse guardian = userLookupClient.getUser(request.getGuardianUserId());
            if (guardian.getCustomerType() != CustomerType.INDIVIDUAL) {
                throw new InvalidAccountOperationException("Guardian must be a registered INDIVIDUAL customer");
            }

            AccountHolder guardianHolder = new AccountHolder();
            guardianHolder.setAccountId(accountId);
            guardianHolder.setUserId(guardian.getId());
            guardianHolder.setHolderRelation(HolderRelation.GUARDIAN);
            guardianHolder.setPrimaryHolder(false);
            guardianHolder.setCanOperate(true);
            holders.add(guardianHolder);
        }

        if (primaryUser.getCustomerType() == CustomerType.JOINT && request.getJointHolderUserIds() != null) {
            for (UUID jointUserId : request.getJointHolderUserIds()) {
                UserResponse jointUser = userLookupClient.getUser(jointUserId);

                AccountHolder jointHolder = new AccountHolder();
                jointHolder.setAccountId(accountId);
                jointHolder.setUserId(jointUser.getId());
                jointHolder.setHolderRelation(HolderRelation.JOINT_HOLDER);
                jointHolder.setPrimaryHolder(false);
                jointHolder.setCanOperate(true);
                holders.add(jointHolder);
            }
        }

        return holders;
    }

    private String generateUniqueAccountNumber() {
        String accountNumber;
        do {
            accountNumber = IdGenerator.generateAccountNumber();
        } while (accountRepository.existsByAccountNumber(accountNumber));
        return accountNumber;
    }
}
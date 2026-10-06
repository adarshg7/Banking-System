package com.bank.admin.service.impl;

import com.bank.account.dto.response.AccountResponse;
import com.bank.account.entity.Account;
import com.bank.account.enums.AccountStatus;
import com.bank.account.exception.AccountNotFoundException;
import com.bank.account.mapper.AccountMapper;
import com.bank.account.repository.AccountHolderRepository;
import com.bank.account.repository.AccountRepository;
import com.bank.admin.entity.AuditLog;
import com.bank.admin.exception.InvalidRoleException;
import com.bank.admin.repository.AuditLogRepository;
import com.bank.admin.service.AdminService;
import com.bank.common.response.PageResponse;
import com.bank.user.entity.User;
import com.bank.user.enums.Role;
import com.bank.user.exception.UserNotFoundException;
import com.bank.user.mapper.UserMapper;
import com.bank.user.repository.UserRepository;
import com.bank.user.dto.response.UserResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class AdminServiceImpl implements AdminService {

    private final UserRepository userRepository;
    private final AccountRepository accountRepository;
    private final AccountHolderRepository accountHolderRepository;
    private final UserMapper userMapper;
    private final AccountMapper accountMapper;
    private final AuditLogRepository auditLogRepository;

    public AdminServiceImpl(UserRepository userRepository,
                            AccountRepository accountRepository,
                            AccountHolderRepository accountHolderRepository,
                            UserMapper userMapper,
                            AccountMapper accountMapper,
                            AuditLogRepository auditLogRepository) {
        this.userRepository = userRepository;
        this.accountRepository = accountRepository;
        this.accountHolderRepository = accountHolderRepository;
        this.userMapper = userMapper;
        this.accountMapper = accountMapper;
        this.auditLogRepository = auditLogRepository;
    }

    @Override
    @Transactional
    public UserResponse updateUserRole(UUID userId, String newRole) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found: " + userId));

        Role role;
        try {
            role = Role.valueOf(newRole.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new InvalidRoleException("Invalid role: " + newRole +
                    ". Valid roles: CUSTOMER, MAKER, CHECKER, AUTHORIZER, VIEWER, ADMIN");
        }

        user.setRole(role);
        user = userRepository.save(user);

        return userMapper.toUserResponse(user);
    }

    @Override
    @Transactional
    public AccountResponse freezeAccount(UUID accountId) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new AccountNotFoundException("Account not found: " + accountId));

        account.setStatus(AccountStatus.FROZEN);
        account = accountRepository.save(account);

        List<com.bank.account.entity.AccountHolder> holders = accountHolderRepository.findByAccountId(accountId);
        return accountMapper.toAccountResponse(account, holders);
    }

    @Override
    @Transactional
    public AccountResponse unfreezeAccount(UUID accountId) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new AccountNotFoundException("Account not found: " + accountId));

        account.setStatus(AccountStatus.ACTIVE);
        account = accountRepository.save(account);

        List<com.bank.account.entity.AccountHolder> holders = accountHolderRepository.findByAccountId(accountId);
        return accountMapper.toAccountResponse(account, holders);
    }

    @Override
    public PageResponse<UserResponse> getAllUsers(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<User> userPage = userRepository.findAll(pageable);
        return PageResponse.from(userPage.map(userMapper::toUserResponse));
    }

    @Override
    public Page<AuditLog> getLogs(String entityName, int page, int size) {

        Pageable pageable = PageRequest.of(page, size);

        return auditLogRepository
                .findByEntityNameOrderByTimestampDesc(entityName, pageable);
    }
}
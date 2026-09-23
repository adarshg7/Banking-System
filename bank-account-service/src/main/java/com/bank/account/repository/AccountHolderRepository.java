package com.bank.account.repository;

import com.bank.account.entity.AccountHolder;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface AccountHolderRepository extends JpaRepository<AccountHolder, UUID> {
    List<AccountHolder> findByAccountId(UUID accountId);
    List<AccountHolder> findByUserId(UUID userId);
    boolean existsByAccountIdAndUserIdAndCanOperateTrue(UUID accountId, UUID userId);
}
package com.bank.admin.service;

import com.bank.account.dto.response.AccountResponse;
import com.bank.admin.entity.AuditLog;
import com.bank.common.response.PageResponse;
import com.bank.user.dto.response.UserResponse;
import org.springframework.data.domain.Page;

import java.util.UUID;

public interface AdminService {

    UserResponse updateUserRole(UUID userId, String newRole);

    AccountResponse freezeAccount(UUID accountId);

    AccountResponse unfreezeAccount(UUID accountId);

    PageResponse<UserResponse> getAllUsers(int page, int size);

    Page<AuditLog> getLogs(String entityName, int page, int size);
}
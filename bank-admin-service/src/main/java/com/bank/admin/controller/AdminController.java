package com.bank.admin.controller;

import com.bank.account.dto.response.AccountResponse;
import com.bank.admin.dto.request.RoleUpdateRequest;
import com.bank.admin.entity.AuditLog;
import com.bank.admin.service.AdminService;
import com.bank.common.response.ApiResponse;
import com.bank.common.response.PageResponse;
import com.bank.user.dto.response.UserResponse;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final AdminService adminService;

    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    @PutMapping("/users/{userId}/role")
    public ApiResponse<UserResponse> updateRole(@PathVariable UUID userId,
                                                @Valid @RequestBody RoleUpdateRequest request) {
        UserResponse response = adminService.updateUserRole(userId, request.getRole());
        return ApiResponse.success(response, "User role updated successfully");
    }

    @PutMapping("/accounts/{accountId}/freeze")
    public ApiResponse<AccountResponse> freeze(@PathVariable UUID accountId) {
        AccountResponse response = adminService.freezeAccount(accountId);
        return ApiResponse.success(response, "Account frozen");
    }

    @PutMapping("/accounts/{accountId}/unfreeze")
    public ApiResponse<AccountResponse> unfreeze(@PathVariable UUID accountId) {
        AccountResponse response = adminService.unfreezeAccount(accountId);
        return ApiResponse.success(response, "Account unfrozen");
    }

    @GetMapping("/users")
    public ApiResponse<PageResponse<UserResponse>> getAllUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        PageResponse<UserResponse> response = adminService.getAllUsers(page, size);
        return ApiResponse.success(response, "Users fetched successfully");
    }

    @GetMapping("/audit-logs")
    public ApiResponse<Page<AuditLog>> getAuditLogs(
            @RequestParam String entityName,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        Page<AuditLog> logs = adminService.getLogs(entityName, page, size);

        return ApiResponse.success(logs, "Audit logs fetched");
    }
}
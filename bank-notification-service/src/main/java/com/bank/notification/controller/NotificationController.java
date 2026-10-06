package com.bank.notification.controller;

import com.bank.common.response.ApiResponse;
import com.bank.common.response.PageResponse;
import com.bank.notification.dto.response.NotificationResponse;
import com.bank.notification.service.NotificationService;
import com.bank.security.util.SecurityUtils;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/notifications")
public class NotificationController {

    private final NotificationService notificationService;

    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @GetMapping
    public ApiResponse<PageResponse<NotificationResponse>> getMyNotifications(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        UUID currentUserId = SecurityUtils.getCurrentUserId();
        PageResponse<NotificationResponse> response = notificationService.getMyNotifications(currentUserId, page, size);
        return ApiResponse.success(response, "Notifications fetched");
    }

    @PutMapping("/{id}/read")
    public ApiResponse<Void> markAsRead(@PathVariable UUID id) {
        notificationService.markAsRead(id);
        return ApiResponse.success(null, "Marked as read");
    }
}
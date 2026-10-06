package com.bank.notification.service;

import com.bank.common.response.PageResponse;
import com.bank.notification.dto.response.NotificationResponse;

import java.util.UUID;

public interface NotificationService {
    PageResponse<NotificationResponse> getMyNotifications(UUID userId, int page, int size);
    void markAsRead(UUID notificationId);
}
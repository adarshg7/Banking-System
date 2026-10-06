package com.bank.notification.service.impl;

import com.bank.common.response.PageResponse;
import com.bank.notification.entity.Notification;
import com.bank.notification.repository.NotificationRepository;
import com.bank.notification.service.NotificationService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import com.bank.notification.dto.response.NotificationResponse;

import java.util.UUID;

@Service
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;

    public NotificationServiceImpl(NotificationRepository notificationRepository){
        this.notificationRepository = notificationRepository;
    }

    @Override
    public PageResponse<NotificationResponse> getMyNotifications(UUID userId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdDate"));
        Page<Notification> notifications = notificationRepository.findByUserIdOrderByCreatedDateDesc(userId, pageable);
        return PageResponse.from(notifications.map(this::toResponse));
    }

    @Override
    public void markAsRead(UUID notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new com.bank.notification.exception.NotificationNotFoundException("Notification not found: " + notificationId));
        notification.setRead(true);
        notificationRepository.save(notification);
    }

    private NotificationResponse toResponse(Notification n) {
        NotificationResponse response = new NotificationResponse();
        response.setId(n.getId());
        response.setTitle(n.getTitle());
        response.setMessage(n.getMessage());
        response.setType(n.getType());
        response.setRead(n.isRead());
        response.setCreatedDate(n.getCreatedDate());
        return response;
    }}

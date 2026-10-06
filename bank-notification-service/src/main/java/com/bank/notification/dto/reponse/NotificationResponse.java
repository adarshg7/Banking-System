package com.bank.notification.dto.response;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
public class NotificationResponse {
    private UUID id;
    private String title;
    private String message;
    private String type;
    private boolean read;
    private LocalDateTime createdDate;
}
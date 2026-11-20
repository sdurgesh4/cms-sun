package com.inturn.suncomputer.notification.dto;

import com.inturn.suncomputer.notification.entity.NotificationPriority;
import com.inturn.suncomputer.notification.entity.NotificationType;

import java.time.LocalDateTime;

public record NotificationResponse(

        Long id,

        Long recipientUserId,

        String title,

        String message,

        NotificationType type,

        NotificationPriority priority,

        boolean read,

        LocalDateTime readAt,

        LocalDateTime createdAt

) {
}
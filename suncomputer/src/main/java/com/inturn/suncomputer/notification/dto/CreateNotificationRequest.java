package com.inturn.suncomputer.notification.dto;

import com.inturn.suncomputer.notification.entity.NotificationPriority;
import com.inturn.suncomputer.notification.entity.NotificationType;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CreateNotificationRequest(

        @NotNull(
                message = "Recipient user ID is required"
        )
        Long recipientUserId,

        @NotBlank(
                message = "Notification title is required"
        )
        @Size(max = 200)
        String title,

        @NotBlank(
                message = "Notification message is required"
        )
        @Size(max = 2000)
        String message,

        NotificationType type,

        NotificationPriority priority

) {
}
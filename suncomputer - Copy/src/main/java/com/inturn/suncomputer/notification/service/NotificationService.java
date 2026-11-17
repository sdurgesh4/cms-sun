package com.inturn.suncomputer.notification.service;

import com.inturn.suncomputer.notification.dto.CreateNotificationRequest;
import com.inturn.suncomputer.notification.dto.NotificationResponse;

import java.util.List;

public interface NotificationService {

    NotificationResponse createNotification(
            CreateNotificationRequest request
    );

    List<NotificationResponse> getMyNotifications(
            String username
    );

    List<NotificationResponse> getMyUnreadNotifications(
            String username
    );

    long getMyUnreadCount(
            String username
    );

    void markAsRead(
            Long notificationId,
            String username
    );

    void markAllAsRead(
            String username
    );

    void deleteNotification(
            Long notificationId,
            String username
    );
}
package com.inturn.suncomputer.notification.service;

import com.inturn.suncomputer.common.exception.ResourceNotFoundException;
import com.inturn.suncomputer.notification.dto.CreateNotificationRequest;
import com.inturn.suncomputer.notification.dto.NotificationResponse;
import com.inturn.suncomputer.notification.entity.Notification;
import com.inturn.suncomputer.notification.entity.NotificationPriority;
import com.inturn.suncomputer.notification.entity.NotificationType;
import com.inturn.suncomputer.notification.repository.NotificationRepository;
import com.inturn.suncomputer.user.entity.User;
import com.inturn.suncomputer.user.repository.UserRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class NotificationServiceImpl
        implements NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;

    public NotificationServiceImpl(
            NotificationRepository notificationRepository,
            UserRepository userRepository
    ) {
        this.notificationRepository = notificationRepository;
        this.userRepository = userRepository;
    }

    @Override
    public NotificationResponse createNotification(
            CreateNotificationRequest request
    ) {

        User recipient =
                userRepository.findById(
                        request.recipientUserId()
                ).orElseThrow(() ->
                        new ResourceNotFoundException(
                                "User not found with id: "
                                        + request.recipientUserId()
                        )
                );

        Notification notification =
                new Notification();

        notification.setRecipient(recipient);

        notification.setTitle(
                request.title()
        );

        notification.setMessage(
                request.message()
        );

        notification.setType(
                request.type() != null
                        ? request.type()
                        : NotificationType.GENERAL
        );

        notification.setPriority(
                request.priority() != null
                        ? request.priority()
                        : NotificationPriority.NORMAL
        );

        notification.setRead(false);

        return mapToResponse(
                notificationRepository.save(notification)
        );
    }

    @Override
    @Transactional(readOnly = true)
    public List<NotificationResponse> getMyNotifications(
            String username
    ) {

        User user = getUser(username);

        return notificationRepository
                .findByRecipientIdOrderByCreatedAtDesc(
                        user.getId()
                )
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<NotificationResponse>
    getMyUnreadNotifications(
            String username
    ) {

        User user = getUser(username);

        return notificationRepository
                .findByRecipientIdAndReadFalseOrderByCreatedAtDesc(
                        user.getId()
                )
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public long getMyUnreadCount(
            String username
    ) {

        User user = getUser(username);

        return notificationRepository
                .countByRecipientIdAndReadFalse(
                        user.getId()
                );
    }

    @Override
    public void markAsRead(
            Long notificationId,
            String username
    ) {

        User user = getUser(username);

        Notification notification =
                getNotification(notificationId);

        verifyOwnership(
                notification,
                user
        );

        if (!notification.isRead()) {

            notification.setRead(true);

            notification.setReadAt(
                    LocalDateTime.now()
            );
        }
    }

    @Override
    public void markAllAsRead(
            String username
    ) {

        User user = getUser(username);

        List<Notification> notifications =
                notificationRepository
                        .findByRecipientIdAndReadFalseOrderByCreatedAtDesc(
                                user.getId()
                        );

        LocalDateTime now =
                LocalDateTime.now();

        for (Notification notification :
                notifications) {

            notification.setRead(true);
            notification.setReadAt(now);
        }
    }

    @Override
    public void deleteNotification(
            Long notificationId,
            String username
    ) {

        User user = getUser(username);

        Notification notification =
                getNotification(notificationId);

        verifyOwnership(
                notification,
                user
        );

        notificationRepository.delete(
                notification
        );
    }

    private User getUser(
            String username
    ) {

        return userRepository
                .findByUsername(username)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Authenticated user not found"
                        )
                );
    }

    private Notification getNotification(
            Long notificationId
    ) {

        return notificationRepository
                .findById(notificationId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Notification not found with id: "
                                        + notificationId
                        )
                );
    }

    private void verifyOwnership(
            Notification notification,
            User user
    ) {

        if (
                !notification
                        .getRecipient()
                        .getId()
                        .equals(user.getId())
        ) {

            // Do not reveal that the notification exists.
            throw new ResourceNotFoundException(
                    "Notification not found"
            );
        }
    }

    private NotificationResponse mapToResponse(
            Notification notification
    ) {

        return new NotificationResponse(

                notification.getId(),

                notification
                        .getRecipient()
                        .getId(),

                notification.getTitle(),

                notification.getMessage(),

                notification.getType(),

                notification.getPriority(),

                notification.isRead(),

                notification.getReadAt(),

                notification.getCreatedAt()
        );
    }
}
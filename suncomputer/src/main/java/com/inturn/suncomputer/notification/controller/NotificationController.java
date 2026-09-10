package com.inturn.suncomputer.notification.controller;

import com.inturn.suncomputer.notification.dto.CreateNotificationRequest;
import com.inturn.suncomputer.notification.dto.NotificationResponse;
import com.inturn.suncomputer.notification.service.NotificationService;

import jakarta.validation.Valid;

import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    @PostMapping
    public ResponseEntity<NotificationResponse> createNotification(
            @Valid @RequestBody CreateNotificationRequest request
    ) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                        notificationService
                                .createNotification(request)
                );
    }

    @GetMapping
    public ResponseEntity<List<NotificationResponse>>
    getMyNotifications(
            Authentication authentication
    ) {

        return ResponseEntity.ok(
                notificationService.getMyNotifications(
                        authentication.getName()
                )
        );
    }

    @GetMapping("/unread")
    public ResponseEntity<List<NotificationResponse>>
    getMyUnreadNotifications(
            Authentication authentication
    ) {

        return ResponseEntity.ok(
                notificationService.getMyUnreadNotifications(
                        authentication.getName()
                )
        );
    }

    @GetMapping("/unread/count")
    public ResponseEntity<Long> getMyUnreadCount(
            Authentication authentication
    ) {

        return ResponseEntity.ok(
                notificationService.getMyUnreadCount(
                        authentication.getName()
                )
        );
    }

    @PatchMapping("/{notificationId}/read")
    public ResponseEntity<Void> markAsRead(
            @PathVariable Long notificationId,
            Authentication authentication
    ) {

        notificationService.markAsRead(
                notificationId,
                authentication.getName()
        );

        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/read-all")
    public ResponseEntity<Void> markAllAsRead(
            Authentication authentication
    ) {

        notificationService.markAllAsRead(
                authentication.getName()
        );

        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{notificationId}")
    public ResponseEntity<Void> deleteNotification(
            @PathVariable Long notificationId,
            Authentication authentication
    ) {

        notificationService.deleteNotification(
                notificationId,
                authentication.getName()
        );

        return ResponseEntity.noContent().build();
    }
}
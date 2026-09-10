package com.inturn.suncomputer.notification.entity;

import com.inturn.suncomputer.user.entity.User;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "notifications",
        indexes = {
                @Index(
                        name = "idx_notification_recipient",
                        columnList = "recipient_user_id"
                ),
                @Index(
                        name = "idx_notification_read",
                        columnList = "is_read"
                ),
                @Index(
                        name = "idx_notification_created",
                        columnList = "created_at"
                )
        }
)
public class Notification {

    @Id
    @GeneratedValue(
            strategy = GenerationType.IDENTITY
    )
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "recipient_user_id",
            nullable = false
    )
    private User recipient;

    @Column(
            nullable = false,
            length = 200
    )
    private String title;

    @Column(
            nullable = false,
            length = 2000
    )
    private String message;

    @Enumerated(EnumType.STRING)
    @Column(
            nullable = false,
            length = 30
    )
    private NotificationType type =
            NotificationType.GENERAL;

    @Enumerated(EnumType.STRING)
    @Column(
            nullable = false,
            length = 20
    )
    private NotificationPriority priority =
            NotificationPriority.NORMAL;

    @Column(
            name = "is_read",
            nullable = false
    )
    private boolean read = false;

    @Column(
            name = "read_at"
    )
    private LocalDateTime readAt;

    @Column(
            name = "created_at",
            nullable = false,
            updatable = false
    )
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {

        createdAt =
                LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public User getRecipient() {
        return recipient;
    }

    public void setRecipient(User recipient) {
        this.recipient = recipient;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public NotificationType getType() {
        return type;
    }

    public void setType(NotificationType type) {
        this.type = type;
    }

    public NotificationPriority getPriority() {
        return priority;
    }

    public void setPriority(
            NotificationPriority priority
    ) {
        this.priority = priority;
    }

    public boolean isRead() {
        return read;
    }

    public void setRead(boolean read) {
        this.read = read;
    }

    public LocalDateTime getReadAt() {
        return readAt;
    }

    public void setReadAt(LocalDateTime readAt) {
        this.readAt = readAt;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}
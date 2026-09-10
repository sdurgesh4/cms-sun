CREATE TABLE notifications (

                               id BIGSERIAL PRIMARY KEY,

                               recipient_user_id BIGINT NOT NULL,

                               title VARCHAR(200) NOT NULL,

                               message VARCHAR(2000) NOT NULL,

                               type VARCHAR(30) NOT NULL,

                               priority VARCHAR(20) NOT NULL,

                               is_read BOOLEAN NOT NULL DEFAULT FALSE,

                               read_at TIMESTAMP NULL,

                               created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

                               CONSTRAINT fk_notification_recipient
                                   FOREIGN KEY (recipient_user_id)
                                       REFERENCES users(id)
);

CREATE INDEX idx_notification_recipient
    ON notifications(recipient_user_id);

CREATE INDEX idx_notification_read
    ON notifications(is_read);

CREATE INDEX idx_notification_created
    ON notifications(created_at);
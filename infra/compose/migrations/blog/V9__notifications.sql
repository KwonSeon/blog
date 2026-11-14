CREATE TABLE notifications (
 notification_id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
 identity_subject VARCHAR(64) CHARACTER SET ascii COLLATE ascii_bin NOT NULL,
 comment_id BIGINT NOT NULL,
 kind VARCHAR(20) NOT NULL,
 created_at DATETIME(6) NOT NULL,
 read_at DATETIME(6) NULL,
 UNIQUE KEY uk_notification_recipient_comment (identity_subject, comment_id),
 INDEX idx_notification_inbox (identity_subject, notification_id),
 INDEX idx_notification_unread (identity_subject, read_at, notification_id),
 INDEX idx_notification_retention (created_at, notification_id),
 CONSTRAINT fk_notification_comment FOREIGN KEY(comment_id) REFERENCES comments(comment_id) ON DELETE CASCADE,
 CONSTRAINT ck_notification_kind CHECK (kind IN ('COMMENT','REPLY'))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

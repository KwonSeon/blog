CREATE TABLE newsletter_subscribers (
 subscriber_id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
 email_hash CHAR(64) CHARACTER SET ascii COLLATE ascii_bin NOT NULL UNIQUE,
 email VARCHAR(254) NULL,
 status VARCHAR(20) NOT NULL,
 generation BIGINT NOT NULL DEFAULT 1,
 confirm_hash CHAR(64) CHARACTER SET ascii COLLATE ascii_bin NULL UNIQUE,
 unsubscribe_hash CHAR(64) CHARACTER SET ascii COLLATE ascii_bin NULL UNIQUE,
 unsubscribe_cipher TEXT NULL,
 token_expires DATETIME(6) NULL,
 confirmed_at DATETIME(6) NULL,
 created_at DATETIME(6) NOT NULL,
 updated_at DATETIME(6) NOT NULL,
 INDEX idx_newsletter_active (status,subscriber_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
CREATE TABLE newsletter_campaigns (
 post_id BIGINT NOT NULL PRIMARY KEY,
 created_at DATETIME(6) NOT NULL,
 recipient_cursor BIGINT NOT NULL DEFAULT 0,
 complete BOOLEAN NOT NULL DEFAULT FALSE,
 CONSTRAINT fk_newsletter_post FOREIGN KEY(post_id) REFERENCES posts(post_id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
CREATE TABLE newsletter_outbox (
 message_id CHAR(36) CHARACTER SET ascii COLLATE ascii_bin NOT NULL PRIMARY KEY,
 subscriber_id BIGINT NOT NULL,
 post_id BIGINT NULL,
 generation BIGINT NOT NULL,
 message_type VARCHAR(20) NOT NULL,
 dedupe_key VARCHAR(160) CHARACTER SET ascii COLLATE ascii_bin NOT NULL UNIQUE,
 payload_cipher MEDIUMTEXT NULL,
 status VARCHAR(20) NOT NULL,
 attempts INT NOT NULL DEFAULT 0,
 next_attempt_at DATETIME(6) NOT NULL,
 first_attempt_at DATETIME(6) NULL,
 sent_at DATETIME(6) NULL,
 created_at DATETIME(6) NOT NULL,
 error_code VARCHAR(40) NULL,
 INDEX idx_newsletter_due (status,next_attempt_at),
 CONSTRAINT fk_newsletter_outbox_subscriber FOREIGN KEY(subscriber_id) REFERENCES newsletter_subscribers(subscriber_id) ON DELETE CASCADE,
 CONSTRAINT fk_newsletter_outbox_campaign FOREIGN KEY(post_id) REFERENCES newsletter_campaigns(post_id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
CREATE TABLE newsletter_limits (
 limit_key VARCHAR(100) CHARACTER SET ascii COLLATE ascii_bin NOT NULL PRIMARY KEY,
 window_start DATETIME(6) NOT NULL,
 request_count INT NOT NULL DEFAULT 0
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

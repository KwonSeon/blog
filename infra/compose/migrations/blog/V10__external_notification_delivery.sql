CREATE TABLE notification_delivery_control (
 channel VARCHAR(20) CHARACTER SET ascii COLLATE ascii_bin PRIMARY KEY,
 active_config_hash CHAR(64) CHARACTER SET ascii COLLATE ascii_bin NULL,
 next_allowed_at DATETIME(6) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
INSERT INTO notification_delivery_control(channel,active_config_hash,next_allowed_at) VALUES('telegram',NULL,'2000-01-01 00:00:00');
CREATE TABLE notification_deliveries (
 delivery_id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
 notification_id BIGINT NOT NULL,
 config_hash CHAR(64) CHARACTER SET ascii COLLATE ascii_bin NOT NULL,
 state VARCHAR(20) NOT NULL DEFAULT 'QUEUED',
 attempts INT NOT NULL DEFAULT 0,
 next_attempt_at DATETIME(6) NOT NULL,
 claimed_at DATETIME(6) NULL,
 completed_at DATETIME(6) NULL,
 last_code VARCHAR(32) NULL,
 UNIQUE KEY uk_delivery_notification_config(notification_id,config_hash),
 INDEX idx_delivery_ready(state,next_attempt_at,delivery_id),
 INDEX idx_delivery_recovery(state,claimed_at),
 CONSTRAINT fk_delivery_notification FOREIGN KEY(notification_id) REFERENCES notifications(notification_id) ON DELETE CASCADE,
 CONSTRAINT ck_delivery_state CHECK(state IN ('QUEUED','SENDING','SENT','FAILED','UNKNOWN','SKIPPED'))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

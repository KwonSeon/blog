CREATE TABLE chat_rooms (
 room_id BIGINT NOT NULL PRIMARY KEY,
 title VARCHAR(80) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
INSERT INTO chat_rooms VALUES(1,'자유 대화');
CREATE TABLE chat_messages (
 message_id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
 room_id BIGINT NOT NULL,
 identity_subject VARCHAR(64) CHARACTER SET ascii COLLATE ascii_bin NOT NULL,
 display_name VARCHAR(40) NOT NULL,
 body VARCHAR(2000) NOT NULL,
 request_key CHAR(36) CHARACTER SET ascii COLLATE ascii_bin NOT NULL,
 request_hash CHAR(64) CHARACTER SET ascii COLLATE ascii_bin NOT NULL,
 created_at DATETIME(6) NOT NULL,
 deleted_at DATETIME(6) NULL,
 UNIQUE KEY uk_chat_request(identity_subject,request_key),
 INDEX idx_chat_room_page(room_id,message_id),
 INDEX idx_chat_retention(created_at,message_id),
 CONSTRAINT fk_chat_room FOREIGN KEY(room_id) REFERENCES chat_rooms(room_id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
CREATE TABLE chat_rate_limits (
 identity_subject VARCHAR(64) CHARACTER SET ascii COLLATE ascii_bin PRIMARY KEY,
 minute_start DATETIME(6) NOT NULL,
 minute_count INT NOT NULL,
 day_start DATE NOT NULL,
 day_count INT NOT NULL,
 last_write DATETIME(6) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

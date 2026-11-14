CREATE TABLE comments (
 comment_id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
 post_id BIGINT NULL,
 project_id BIGINT NULL,
 parent_id BIGINT NULL,
 depth INT NOT NULL DEFAULT 0,
 identity_subject VARCHAR(64) CHARACTER SET ascii COLLATE ascii_bin NOT NULL,
 display_name VARCHAR(40) NOT NULL,
 body TEXT NOT NULL,
 request_key CHAR(36) CHARACTER SET ascii COLLATE ascii_bin NOT NULL,
 request_hash CHAR(64) CHARACTER SET ascii COLLATE ascii_bin NOT NULL,
 created_at DATETIME(6) NOT NULL,
 updated_at DATETIME(6) NOT NULL,
 deleted_at DATETIME(6) NULL,
 UNIQUE KEY uk_comments_request (identity_subject,request_key),
 INDEX idx_comments_post_cursor (post_id,comment_id),
 INDEX idx_comments_project_cursor (project_id,comment_id),
 INDEX idx_comments_parent (parent_id,comment_id),
 INDEX idx_comments_member_time (identity_subject,created_at),
 CONSTRAINT ck_comments_target CHECK ((post_id IS NULL) <> (project_id IS NULL)),
 CONSTRAINT fk_comments_post FOREIGN KEY(post_id) REFERENCES posts(post_id) ON DELETE CASCADE,
 CONSTRAINT fk_comments_project FOREIGN KEY(project_id) REFERENCES projects(project_id) ON DELETE CASCADE,
 CONSTRAINT fk_comments_parent FOREIGN KEY(parent_id) REFERENCES comments(comment_id) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
CREATE TABLE comment_rate_limits (
 identity_subject VARCHAR(64) CHARACTER SET ascii COLLATE ascii_bin NOT NULL PRIMARY KEY,
 last_write DATETIME(6) NOT NULL,
 minute_start DATETIME(6) NOT NULL,
 minute_count INT NOT NULL,
 day_start DATE NOT NULL,
 day_count INT NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

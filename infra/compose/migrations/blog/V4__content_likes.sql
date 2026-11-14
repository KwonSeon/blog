CREATE TABLE post_likes (
 post_id BIGINT NOT NULL,
 identity_subject VARCHAR(64) CHARACTER SET ascii COLLATE ascii_bin NOT NULL,
 created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
 PRIMARY KEY (post_id, identity_subject),
 INDEX idx_post_likes_subject (identity_subject),
 CONSTRAINT fk_post_likes_post FOREIGN KEY (post_id) REFERENCES posts(post_id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
CREATE TABLE project_likes (
 project_id BIGINT NOT NULL,
 identity_subject VARCHAR(64) CHARACTER SET ascii COLLATE ascii_bin NOT NULL,
 created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
 PRIMARY KEY (project_id, identity_subject),
 INDEX idx_project_likes_subject (identity_subject),
 CONSTRAINT fk_project_likes_project FOREIGN KEY (project_id) REFERENCES projects(project_id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

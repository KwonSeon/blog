-- UTC daily deduplication keys; no IP, account ID or raw browser identifier.
CREATE TABLE post_views (
 post_id BIGINT NOT NULL,
 view_day DATE NOT NULL,
 visitor_hash CHAR(64) CHARACTER SET ascii COLLATE ascii_bin NOT NULL,
 PRIMARY KEY (post_id, view_day, visitor_hash),
 INDEX idx_post_views_day (view_day),
 CONSTRAINT fk_post_views_post FOREIGN KEY (post_id) REFERENCES posts(post_id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
CREATE TABLE project_views (
 project_id BIGINT NOT NULL,
 view_day DATE NOT NULL,
 visitor_hash CHAR(64) CHARACTER SET ascii COLLATE ascii_bin NOT NULL,
 PRIMARY KEY (project_id, view_day, visitor_hash),
 INDEX idx_project_views_day (view_day),
 CONSTRAINT fk_project_views_project FOREIGN KEY (project_id) REFERENCES projects(project_id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

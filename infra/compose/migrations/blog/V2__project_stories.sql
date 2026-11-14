CREATE TABLE project_stories (
 project_id BIGINT NOT NULL PRIMARY KEY,
 content_md LONGTEXT NOT NULL,
 CONSTRAINT fk_project_story_project FOREIGN KEY (project_id) REFERENCES projects(project_id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

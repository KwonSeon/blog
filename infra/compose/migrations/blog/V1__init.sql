CREATE TABLE users (
  user_id BIGINT NOT NULL,
  username VARCHAR(50) NOT NULL,
  email VARCHAR(255) NULL,
  password_hash VARCHAR(255) NOT NULL,
  role VARCHAR(20) NOT NULL DEFAULT 'ADMIN',
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (user_id),
  UNIQUE KEY uk_users_username (username),
  UNIQUE KEY uk_users_email (email)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE projects (
  project_id BIGINT NOT NULL,
  slug VARCHAR(120) NOT NULL,
  title VARCHAR(200) NOT NULL,
  summary VARCHAR(500) NULL,
  service_url VARCHAR(500) NULL,
  repo_url VARCHAR(500) NULL,
  visibility VARCHAR(20) NOT NULL DEFAULT 'PUBLIC',
  status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
  cover_media_asset_id BIGINT NULL, -- media_db.media_assets.media_asset_id (FK 없음)
  view_count BIGINT NOT NULL DEFAULT 0,
  like_count BIGINT NOT NULL DEFAULT 0,
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  published_at DATETIME NULL,
  PRIMARY KEY (project_id),
  UNIQUE KEY uk_projects_slug (slug),
  KEY idx_projects_visibility (visibility)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE posts (
  post_id BIGINT NOT NULL,
  slug VARCHAR(150) NOT NULL,
  title VARCHAR(300) NOT NULL,
  excerpt VARCHAR(600) NULL,
  content_md LONGTEXT NOT NULL,
  status VARCHAR(20) NOT NULL DEFAULT 'DRAFT',
  visibility VARCHAR(20) NOT NULL DEFAULT 'PUBLIC',
  lang VARCHAR(10) NOT NULL DEFAULT 'ko',
  cover_media_asset_id BIGINT NULL, -- media_db 참조(FK 없음)
  author_user_id BIGINT NULL,       -- 추후 auth 분리 대비(FK 없음 권장)
  view_count BIGINT NOT NULL DEFAULT 0,
  like_count BIGINT NOT NULL DEFAULT 0,
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  published_at DATETIME NULL,
  PRIMARY KEY (post_id),
  UNIQUE KEY uk_posts_slug (slug),
  KEY idx_posts_status_published (status, published_at),
  KEY idx_posts_visibility (visibility),
  KEY idx_posts_author (author_user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE tags (
  tag_id BIGINT NOT NULL,
  scope VARCHAR(20) NOT NULL,              -- POST | PROJECT
  kind VARCHAR(20) NOT NULL DEFAULT 'TAG', -- CATEGORY | TAG
  parent_tag_id BIGINT NULL,
  slug VARCHAR(120) NOT NULL,
  name VARCHAR(120) NOT NULL,
  description VARCHAR(255) NULL,
  sort_order INT NOT NULL DEFAULT 0,
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (tag_id),
  UNIQUE KEY uk_tags_scope_slug (scope, slug),
  KEY idx_tags_parent (parent_tag_id),
  CONSTRAINT fk_tags_parent
    FOREIGN KEY (parent_tag_id) REFERENCES tags(tag_id)
    ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE post_tags (
  post_id BIGINT NOT NULL,
  tag_id BIGINT NOT NULL,
  PRIMARY KEY (post_id, tag_id),
  KEY idx_post_tags_tag (tag_id),
  CONSTRAINT fk_post_tags_post
    FOREIGN KEY (post_id) REFERENCES posts(post_id)
    ON DELETE CASCADE,
  CONSTRAINT fk_post_tags_tag
    FOREIGN KEY (tag_id) REFERENCES tags(tag_id)
    ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE project_tags (
  project_id BIGINT NOT NULL,
  tag_id BIGINT NOT NULL,
  PRIMARY KEY (project_id, tag_id),
  KEY idx_project_tags_tag (tag_id),
  CONSTRAINT fk_project_tags_project
    FOREIGN KEY (project_id) REFERENCES projects(project_id)
    ON DELETE CASCADE,
  CONSTRAINT fk_project_tags_tag
    FOREIGN KEY (tag_id) REFERENCES tags(tag_id)
    ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE post_projects (
  post_id BIGINT NOT NULL,
  project_id BIGINT NOT NULL,
  sort_order INT NOT NULL DEFAULT 0,
  PRIMARY KEY (post_id, project_id),
  KEY idx_post_projects_project (project_id),
  CONSTRAINT fk_post_projects_post
    FOREIGN KEY (post_id) REFERENCES posts(post_id)
    ON DELETE CASCADE,
  CONSTRAINT fk_post_projects_project
    FOREIGN KEY (project_id) REFERENCES projects(project_id)
    ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

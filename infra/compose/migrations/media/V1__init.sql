CREATE TABLE media_assets (
  media_asset_id BIGINT NOT NULL,
  namespace VARCHAR(50) NOT NULL,
  bucket VARCHAR(63) NOT NULL,
  object_key VARCHAR(512) NOT NULL,
  original_filename VARCHAR(255) NULL,
  content_type VARCHAR(100) NULL,
  size_bytes BIGINT NOT NULL DEFAULT 0,
  checksum_sha256 CHAR(64) NULL,
  width INT NULL,
  height INT NULL,
  uploaded_by_user_id BIGINT NULL,  -- 추후 auth user_id (FK 없음)
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (media_asset_id),
  UNIQUE KEY uk_media_bucket_object (bucket, object_key),
  KEY idx_media_namespace_created (namespace, created_at),
  KEY idx_media_uploader (uploaded_by_user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

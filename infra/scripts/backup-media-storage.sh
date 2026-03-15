#!/bin/bash

set -euo pipefail

SCRIPT_DIR=$(CDPATH= cd -- "$(dirname "$0")" && pwd)
INFRA_DIR=$(CDPATH= cd -- "$SCRIPT_DIR/.." && pwd)
BACKUP_ROOT=${S_NOWK_BACKUP_ROOT:-"$INFRA_DIR/backups"}
TARGET_DIR="${1:-$BACKUP_ROOT/media-storage}"
TIMESTAMP=$(date +"%Y%m%d-%H%M%S")
ARCHIVE_NAME="media-minio-data-$TIMESTAMP.tar.gz"
TARGET_FILE="$TARGET_DIR/$ARCHIVE_NAME"
MEDIA_MINIO_VOLUME=${MEDIA_MINIO_VOLUME:-media_minio_data}

mkdir -p "$TARGET_DIR"

docker run --rm \
  -v "$MEDIA_MINIO_VOLUME:/source:ro" \
  -v "$TARGET_DIR:/backup" \
  alpine:3.20 \
  sh -lc "tar czf /backup/$ARCHIVE_NAME -C /source ."

shasum -a 256 "$TARGET_FILE" > "$TARGET_FILE.sha256"

echo "created: $TARGET_FILE"
echo "sha256:  $TARGET_FILE.sha256"

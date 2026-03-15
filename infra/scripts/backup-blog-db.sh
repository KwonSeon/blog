#!/bin/bash

set -euo pipefail

SCRIPT_DIR=$(CDPATH= cd -- "$(dirname "$0")" && pwd)
INFRA_DIR=$(CDPATH= cd -- "$SCRIPT_DIR/.." && pwd)
BACKUP_ROOT=${S_NOWK_BACKUP_ROOT:-"$INFRA_DIR/backups"}
TARGET_DIR="${1:-$BACKUP_ROOT/blog-db}"
TIMESTAMP=$(date +"%Y%m%d-%H%M%S")
TARGET_FILE="$TARGET_DIR/blog-db-$TIMESTAMP.sql.gz"

mkdir -p "$TARGET_DIR"

docker exec mysql_blog sh -lc \
  'exec mysqldump --single-transaction --quick --no-tablespaces --set-gtid-purged=OFF -u"$MYSQL_USER" -p"$MYSQL_PASSWORD" "$MYSQL_DATABASE"' \
  | gzip > "$TARGET_FILE"

shasum -a 256 "$TARGET_FILE" > "$TARGET_FILE.sha256"

echo "created: $TARGET_FILE"
echo "sha256:  $TARGET_FILE.sha256"

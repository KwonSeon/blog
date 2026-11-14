#!/bin/bash
set -euo pipefail
: "${S_NOWK_BACKUP_CONFIG:?Set the private backup configuration path}"
SCRIPT_DIR=$(CDPATH= cd -- "$(dirname "$0")" && pwd)
exec python3 "$SCRIPT_DIR/../../tools/backup.py" --config "$S_NOWK_BACKUP_CONFIG" backup

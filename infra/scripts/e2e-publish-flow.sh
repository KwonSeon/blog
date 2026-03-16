#!/bin/bash

set -euo pipefail

APP_BASE_URL=${APP_BASE_URL:-https://s-nowk.com}
ADMIN_USERNAME=${ADMIN_USERNAME:-ops-e2e-admin}
ADMIN_PASSWORD=${ADMIN_PASSWORD:-SNowk!e2e2026}
ADMIN_EMAIL=${ADMIN_EMAIL:-ops-e2e-admin@s-nowk.local}
ADMIN_USER_ID=${ADMIN_USER_ID:-900000000000000031}

TMP_DIR=$(mktemp -d)
COOKIE_JAR="$TMP_DIR/cookies.txt"
TIMESTAMP=$(date +"%Y%m%d-%H%M%S")
POST_SLUG=${POST_SLUG:-ops-e2e-$TIMESTAMP}
POST_TITLE=${POST_TITLE:-"OPS E2E $TIMESTAMP"}
SVG_FILE="$TMP_DIR/e2e-upload.svg"

POST_ID=""
MEDIA_ASSET_ID=""
MEDIA_PUBLIC_URL=""

require_command() {
  if ! command -v "$1" >/dev/null 2>&1; then
    echo "missing required command: $1" >&2
    exit 1
  fi
}

mysql_blog_exec() {
  docker exec -i mysql_blog sh -lc 'exec mysql -u"$MYSQL_USER" -p"$MYSQL_PASSWORD" "$MYSQL_DATABASE"'
}

cleanup() {
  local exit_code=$?

  set +e

  if [[ -n "$POST_ID" ]]; then
    curl -sS -o /dev/null \
      -b "$COOKIE_JAR" \
      -X DELETE \
      "$APP_BASE_URL/admin/api/posts/$POST_ID"
  fi

  if [[ -n "$MEDIA_ASSET_ID" ]]; then
    curl -sS -o /dev/null \
      -b "$COOKIE_JAR" \
      -X DELETE \
      "$APP_BASE_URL/admin/api/media/assets/$MEDIA_ASSET_ID"
  fi

  mysql_blog_exec <<SQL >/dev/null 2>&1
DELETE FROM users WHERE username = '${ADMIN_USERNAME}';
SQL

  rm -rf "$TMP_DIR"

  exit "$exit_code"
}

trap cleanup EXIT

require_command curl
require_command jq
require_command docker
require_command htpasswd
require_command grep
require_command mktemp

PASSWORD_HASH=$(htpasswd -nbBC 10 '' "$ADMIN_PASSWORD" | tr -d ':\n')

mysql_blog_exec <<SQL >/dev/null
INSERT INTO users (user_id, username, email, password_hash, role)
VALUES (${ADMIN_USER_ID}, '${ADMIN_USERNAME}', '${ADMIN_EMAIL}', '${PASSWORD_HASH}', 'ADMIN')
ON DUPLICATE KEY UPDATE
  email = VALUES(email),
  password_hash = VALUES(password_hash),
  role = 'ADMIN';
SQL

curl -fsS "$APP_BASE_URL/admin/login" >/dev/null

cat >"$SVG_FILE" <<'SVG'
<svg xmlns="http://www.w3.org/2000/svg" width="320" height="180" viewBox="0 0 320 180">
  <rect width="320" height="180" fill="#111827" />
  <rect x="16" y="16" width="288" height="148" rx="16" fill="#2563eb" />
  <text x="36" y="92" fill="#f8fafc" font-size="28" font-family="Arial, sans-serif">S-NOWK OPS E2E</text>
</svg>
SVG

SVG_SIZE_BYTES=$(wc -c <"$SVG_FILE" | tr -d ' ')

LOGIN_STATUS=$(curl -sS \
  -o "$TMP_DIR/login.json" \
  -w "%{http_code}" \
  -c "$COOKIE_JAR" \
  -H "Content-Type: application/json" \
  -H "Accept: application/json" \
  -d "$(jq -n --arg username "$ADMIN_USERNAME" --arg password "$ADMIN_PASSWORD" '{username:$username,password:$password}')" \
  "$APP_BASE_URL/admin/api/auth/login")

if [[ "$LOGIN_STATUS" != "200" ]]; then
  cat "$TMP_DIR/login.json" >&2
  exit 1
fi

PRESIGN_STATUS=$(curl -sS \
  -o "$TMP_DIR/presign.json" \
  -w "%{http_code}" \
  -b "$COOKIE_JAR" \
  -H "Content-Type: application/json" \
  -H "Accept: application/json" \
  -d "$(jq -n \
    --arg namespace "post-cover" \
    --arg originalFilename "ops-e2e.svg" \
    --arg contentType "image/svg+xml" \
    --argjson sizeBytes "$SVG_SIZE_BYTES" \
    '{namespace:$namespace,originalFilename:$originalFilename,contentType:$contentType,sizeBytes:$sizeBytes}')" \
  "$APP_BASE_URL/admin/api/media/uploads/presign")

if [[ "$PRESIGN_STATUS" != "200" ]]; then
  cat "$TMP_DIR/presign.json" >&2
  exit 1
fi

MEDIA_ASSET_ID=$(jq -r '.mediaAssetId' "$TMP_DIR/presign.json")
OBJECT_KEY=$(jq -r '.objectKey' "$TMP_DIR/presign.json")
UPLOAD_URL=$(jq -r '.uploadUrl' "$TMP_DIR/presign.json")
MEDIA_PUBLIC_URL="$APP_BASE_URL/media/assets/$MEDIA_ASSET_ID/content"

UPLOAD_STATUS=$(curl -sS \
  -o /dev/null \
  -w "%{http_code}" \
  -X PUT \
  -H "Content-Type: image/svg+xml" \
  --data-binary @"$SVG_FILE" \
  "$UPLOAD_URL")

case "$UPLOAD_STATUS" in
  200|201)
    ;;
  *)
    echo "upload failed with status $UPLOAD_STATUS" >&2
    exit 1
    ;;
esac

COMPLETE_STATUS=$(curl -sS \
  -o "$TMP_DIR/complete.json" \
  -w "%{http_code}" \
  -b "$COOKIE_JAR" \
  -H "Content-Type: application/json" \
  -H "Accept: application/json" \
  -d "$(jq -n \
    --argjson mediaAssetId "$MEDIA_ASSET_ID" \
    --arg objectKey "$OBJECT_KEY" \
    --arg originalFilename "ops-e2e.svg" \
    '{mediaAssetId:$mediaAssetId,objectKey:$objectKey,originalFilename:$originalFilename}')" \
  "$APP_BASE_URL/admin/api/media/uploads/complete")

if [[ "$COMPLETE_STATUS" != "200" ]]; then
  cat "$TMP_DIR/complete.json" >&2
  exit 1
fi

CONTENT_MD=$(cat <<MARKDOWN
# OPS E2E

자동 검증용 공개 글입니다.

![ops-e2e]($MEDIA_PUBLIC_URL)
MARKDOWN
)

CREATE_STATUS=$(curl -sS \
  -o "$TMP_DIR/create-post.json" \
  -w "%{http_code}" \
  -b "$COOKIE_JAR" \
  -H "Content-Type: application/json" \
  -H "Accept: application/json" \
  -d "$(jq -n \
    --arg slug "$POST_SLUG" \
    --arg title "$POST_TITLE" \
    --arg excerpt "운영 E2E 검증용 임시 공개 글입니다." \
    --arg contentMd "$CONTENT_MD" \
    --arg visibility "PUBLIC" \
    --arg status "DRAFT" \
    --arg lang "ko" \
    --argjson coverMediaAssetId "$MEDIA_ASSET_ID" \
    '{slug:$slug,title:$title,excerpt:$excerpt,contentMd:$contentMd,visibility:$visibility,status:$status,lang:$lang,coverMediaAssetId:$coverMediaAssetId}')" \
  "$APP_BASE_URL/admin/api/posts")

if [[ "$CREATE_STATUS" != "201" ]]; then
  cat "$TMP_DIR/create-post.json" >&2
  exit 1
fi

POST_ID=$(jq -r '.postId' "$TMP_DIR/create-post.json")

PUBLISH_STATUS=$(curl -sS \
  -o "$TMP_DIR/publish-post.json" \
  -w "%{http_code}" \
  -b "$COOKIE_JAR" \
  -X PATCH \
  -H "Content-Type: application/json" \
  -H "Accept: application/json" \
  -d '{"status":"PUBLISHED"}' \
  "$APP_BASE_URL/admin/api/posts/$POST_ID/status")

if [[ "$PUBLISH_STATUS" != "200" ]]; then
  cat "$TMP_DIR/publish-post.json" >&2
  exit 1
fi

MEDIA_STATUS=$(curl -sS -o /dev/null -w "%{http_code}" "$MEDIA_PUBLIC_URL")

if [[ "$MEDIA_STATUS" != "200" ]]; then
  echo "public media verification failed with status $MEDIA_STATUS" >&2
  exit 1
fi

PUBLIC_POST_URL="$APP_BASE_URL/posts/$POST_SLUG"
PUBLIC_LIST_URL="$APP_BASE_URL/posts"

for attempt in $(seq 1 10); do
  curl -sS "$PUBLIC_POST_URL" -o "$TMP_DIR/public-post.html"

  if grep -Fq "$POST_TITLE" "$TMP_DIR/public-post.html" \
    && grep -Fq "$MEDIA_PUBLIC_URL" "$TMP_DIR/public-post.html"; then
    break
  fi

  if [[ "$attempt" -eq 10 ]]; then
    echo "public post verification failed" >&2
    exit 1
  fi

  sleep 2
done

curl -sS "$PUBLIC_LIST_URL" -o "$TMP_DIR/public-list.html"

if ! grep -Fq "$POST_TITLE" "$TMP_DIR/public-list.html"; then
  echo "public list verification failed" >&2
  exit 1
fi

echo "login:        ok"
echo "upload:       ok (mediaAssetId=$MEDIA_ASSET_ID)"
echo "create draft: ok (postId=$POST_ID)"
echo "publish:      ok"
echo "public post:  ok ($PUBLIC_POST_URL)"
echo "public list:  ok ($PUBLIC_LIST_URL)"

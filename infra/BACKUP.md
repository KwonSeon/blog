# Backup And Restore

현재 최소 백업 범위는 아래 두 가지다.

- `blog_db` SQL dump
- `media` MinIO data volume archive

스크립트:

- `infra/scripts/backup-blog-db.sh`
- `infra/scripts/backup-media-storage.sh`

기본 출력 경로:

- `infra/backups/blog-db`
- `infra/backups/media-storage`

## Backup

blog DB dump:

```bash
./infra/scripts/backup-blog-db.sh
```

media MinIO volume:

```bash
./infra/scripts/backup-media-storage.sh
```

각 스크립트는 압축 파일과 `.sha256` 파일을 함께 만든다.

## Verify

blog DB dump:

```bash
gzip -t infra/backups/blog-db/<file>.sql.gz
gunzip -c infra/backups/blog-db/<file>.sql.gz | head
shasum -a 256 -c infra/backups/blog-db/<file>.sha256
```

media MinIO archive:

```bash
tar -tzf infra/backups/media-storage/<file>.tar.gz | head
shasum -a 256 -c infra/backups/media-storage/<file>.sha256
```

## Manual Restore

사전 조건:

- 복구 대상 컨테이너를 먼저 기동한다.
- 쓰기 트래픽이 있는 경우 복구 중에는 중지한다.

blog DB restore:

```bash
gunzip -c infra/backups/blog-db/<file>.sql.gz \
  | docker exec -i mysql_blog sh -lc 'mysql -u"$MYSQL_USER" -p"$MYSQL_PASSWORD" "$MYSQL_DATABASE"'
```

media MinIO data restore:

```bash
docker run --rm \
  -v media_minio_data:/target \
  -v "$PWD/infra/backups/media-storage:/backup:ro" \
  alpine:3.20 \
  sh -lc 'rm -rf /target/* && tar xzf /backup/<file>.tar.gz -C /target'
```

복구 후 확인:

- `docker ps` health 상태
- `https://s-nowk.com/media/assets/{mediaAssetId}/content`
- blog 공개 페이지와 관리자 업로드 경로

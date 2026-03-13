# Blog Work Progress

이 파일은 저장소에 유지하는 현재 작업 기록이다.
- 작업을 시작하거나 끝낼 때마다 현재 단계와 다음 단계를 갱신한다.
- 구현은 한 번에 하나의 단계만 진행한다.
- 세부 단계가 끝나기 전에는 다음 구현으로 넘어가지 않는다.
- 세부 단계는 가능하면 `패키지 -> 클래스 -> 메서드` 단위까지 쪼개서 기록한다.
- 세부 단계는 가능하면 `메서드 하나`를 기준으로 관련 계층(`port -> adapter -> jpa`, `query -> result -> usecase`, `service -> response -> controller`)을 묶어서 기록한다.
- 구현 체크는 "무슨 파일을 만든다"보다 "어떤 메서드를 추가/수정한다" 기준으로 남긴다.
- application 계층 작업은 가능하면 `query -> result -> usecase method` 순서로 기록한다.

현재 작업 주제
- `P0-013-MEDIA-1 MinIO 연결 + 버킷/키 규칙 확정`

최근 완료 작업
- `P0-012-BE-7 QueryDSL 기반 필터/검색` 완료
- 완료 범위
  - QueryDSL 의존성/annotation processor 추가
  - `JPAQueryFactory` 설정 추가
  - 공개 글 목록 `/api/posts`에 `q`, `lang` 검색 추가
  - QueryDSL 검색용 custom repository 추가
  - 검색 repository/service/controller 테스트 작성
- 구현 기준
  - 첫 적용 대상은 공개 글 목록만
  - 첫 query parameter는 `q`, `lang`
  - `q`는 `title`, `excerpt`, `contentMd` 부분 검색
  - 정렬은 기존 공개 목록 규칙 `publishedAt desc -> postId desc` 유지
  - `contentMd(LONGTEXT/CLOB)`는 cast 후 비교
- 최종 확인: `blog/apps/api`에서 `./gradlew --no-daemon test` 통과

현재 확인된 상태
- `blog_db`와 별도로 `media_db` 스키마와 `media_assets` 테이블 마이그레이션은 이미 있다.
- `media_assets`에는 `namespace`, `bucket`, `object_key`, `checksum_sha256`, `uploaded_by_user_id` 등이 정의돼 있다.
- `posts`, `projects`에는 `cover_media_asset_id` 컬럼이 이미 있다.
- `apps/api`에는 아직 `media` 패키지, MinIO client 설정, presign API가 없다.
- `apps/api/build.gradle`에는 아직 MinIO/S3 SDK 의존성이 없다.
- `infra/compose/docker-compose.yml`에는 `mysql_media`, `flyway_media`는 있지만, 현재 기준 MinIO service 정의는 보이지 않는다.

현재 확정 범위
- 이번 단계에서는 `MinIO 연결`과 `버킷/오브젝트 키 규칙 확정`까지만 다룬다.
- presign 업로드 URL 발급 API와 `media_assets` 저장 로직은 `P0-014-MEDIA-2` 범위다.
- 이번 단계에서는 업로드/다운로드 API보다 먼저 `연결 방식`, `환경변수`, `bucket/namespace/object key` 규칙을 고정한다.
- 현재 compose에 MinIO service 정의가 없는 상태라, 연결 설정 전에 infra 기준도 함께 다시 확인해야 한다.

세부 단계
- [ ] MEDIA-01 요구사항/기반 확인
  - [ ] MEDIA-01-1 `README.md`의 `P0-013-MEDIA-1` 범위와 제외 범위 다시 확인
  - [ ] MEDIA-01-2 `docker-compose.yml`, `migrations/media/V1__init.sql`에서 기존 media/minio 기반 확인
  - [ ] MEDIA-01-3 `apps/api` 기준 media 패키지/MinIO 설정 부재 확인
- [ ] MEDIA-02 MinIO 연결 정책 확정
  - [ ] MEDIA-02-1 MinIO endpoint/credential/property 이름 확정
  - [ ] MEDIA-02-2 SDK 선택과 client bean 위치 확정
  - [ ] MEDIA-02-3 compose에 MinIO service를 둘지, 외부 MinIO를 붙일지 기준 확정
- [ ] MEDIA-03 버킷/키 규칙 확정
  - [ ] MEDIA-03-1 `media_assets.namespace` 허용값 초안 정리
  - [ ] MEDIA-03-2 bucket 전략(단일/분리) 확정
  - [ ] MEDIA-03-3 object key 템플릿 규칙 확정
- [ ] MEDIA-04 백엔드 설정 골격 추가
  - [ ] MEDIA-04-1 property class 추가
  - [ ] MEDIA-04-2 MinIO client config 추가
  - [ ] MEDIA-04-3 `./gradlew --no-daemon compileJava`로 설정 클래스 컴파일 확인

계획 메모
- `media_assets` 테이블이 이미 있으므로, 다음 단계는 API보다 먼저 MinIO 연결 정책과 key naming 규칙을 고정하는 쪽이 맞다.
- MinIO service가 현재 compose에 보이지 않으므로, infra 기준 확인이 선행돼야 한다.
- `cover_media_asset_id`는 이미 post/project에 있으므로, media 업로드 흐름이 생기면 이후 연결은 자연스럽게 붙일 수 있다.

다음 시작 지점
- `MEDIA-01-1`
- 다음 구현은 `README.md`의 P0-013-MEDIA-1 범위와 제외 범위를 다시 확인하는 것이다.

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
- `P0-015-FE-PUB-1 홈 UI(프로젝트/블로그 섹션 분리)`

최근 완료 작업
- `P0-015-FE-PUB-1` 사전 준비로 `frontexample` 기준 공개 홈 보일러플레이트 이식
- 완료 범위
  - `apps/web` 공개 라우트 그룹 `app/(public)` 및 기본 metadata/layout 정리
  - `src/shared/ui` 공통 UI 레이어(`container`, `section-header`, `surface-card`, `cta-button`, `promo-slot`, `status-badge`) 추가
  - `src/entities/project`, `src/entities/post` 타입/카드 컴포넌트 추가
  - `siteConfig`, `mock home data`, `lib/utils` 추가
  - 실제 홈 화면 조립(`hero`, `projects`, `posts`, `cta`)은 아직 시작하지 않음
- `P0-013-MEDIA-1`, `P0-014-MEDIA-2`는 별도 `s-nowk/media` 저장소로 이관
- 이관 범위
  - `media_db`, `media_assets` 마이그레이션
  - MinIO/Flyway/MySQL media compose 기준
  - presign/media metadata 저장 API 구현 예정 범위
- blog 저장소에는 `cover_media_asset_id` 참조와 media 연동 계약만 남김
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
- `posts`, `projects`에는 `cover_media_asset_id` 컬럼이 이미 있다.
- `apps/api`에는 아직 `media` 패키지, MinIO client 설정, presign API가 없다.
- `apps/api/build.gradle`에는 아직 MinIO/S3 SDK 의존성이 없다.
- blog 저장소는 더 이상 `media_db`나 media compose를 소유하지 않는다.
- media 관련 인프라와 마이그레이션의 source of truth는 별도 `s-nowk/media` 저장소다.
- `apps/web`에는 이제 공개 레이아웃, 공통 UI, entity card, mock data 보일러플레이트가 있다.
- 홈 화면 위젯 조립과 디자인 고도화는 아직 남아 있다.

현재 확정 범위
- blog 저장소의 다음 범위는 다시 blog 기능 구현으로 복귀한다.
- media 업로드/저장/infra 작업은 `s-nowk/media` 저장소에서 진행한다.
- blog 저장소에서는 이후 media 연동 API 계약과 `cover_media_asset_id` 활용 흐름만 다룬다.

세부 단계
- [x] FE-PUB-01 홈 요구사항/정보구조 정리
  - [x] FE-PUB-01-1 README 기준 홈/프로젝트/블로그 섹션 요구사항 다시 확인
  - [x] FE-PUB-01-2 현재 `apps/web` 상태와 초기 스캐폴드 여부 확인
  - [x] FE-PUB-01-3 홈 화면에 필요한 API/더미 데이터 기준 정리
- [x] FE-PUB-02 공개 홈 보일러플레이트 이식
  - [x] FE-PUB-02-1 `app/(public)` 레이아웃/metadata 베이스 정리
  - [x] FE-PUB-02-2 `shared/ui`, `siteConfig`, `mock data`, `lib/utils` 추가
  - [x] FE-PUB-02-3 entity card 컴포넌트와 public layout 공통 컴포넌트 추가
- [ ] FE-PUB-03 홈 화면 조립 시작
  - [ ] FE-PUB-03-1 Hero 위젯 조립 및 카피 반영
  - [ ] FE-PUB-03-2 프로젝트 섹션 위젯 조립
  - [ ] FE-PUB-03-3 최신 글 섹션 위젯 조립
  - [ ] FE-PUB-03-4 하단 CTA/promo slot 위치 정리

계획 메모
- blog 저장소는 media 자체 구현보다 media 서비스와의 연동 포인트를 명확히 유지하는 쪽이 맞다.
- `cover_media_asset_id`는 이미 post/project에 있으므로, media 서비스 API가 준비되면 이후 연결은 자연스럽게 붙일 수 있다.
- blog 기능 구현은 홈/공개 화면/관리 화면 작업으로 다시 복귀한다.

다음 시작 지점
- `FE-PUB-03-1`
- 다음 구현은 `shared/ui`와 entity card를 사용해 Hero 위젯을 조립하는 것이다.

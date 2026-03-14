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
- `P0-017-FE-PUB-3 글 목록(필터/검색 UI)`

최근 완료 작업
- `P0-016-FE-PUB-2 프로젝트 목록/상세(서비스 랜딩 UX)` 완료
- 완료 범위
  - 공개 프로젝트 목록 `/projects`와 프로젝트 상세 `/projects/[slug]` 화면 구현
  - metadata, canonical, not-found, 관련 글, 하단 CTA까지 포함한 공개 화면 흐름 정리
  - `ProjectDetailHero`, `ProjectTagList` 분리와 `ProjectCard` 목록 전용 재사용 범위 정리
  - `blog/apps/web`에서 `npm run lint`, `npm run build` 통과
- `P0-015-FE-PUB-1 홈 UI(프로젝트/블로그 섹션 분리)` 완료
- 완료 범위
  - 공개 홈 `page.tsx`에서 `hero -> project -> promo slot -> posts -> cta` 순으로 SSR 친화 구조 조립
  - `HomeHero`, `HomeProjectsSection`, `HomePostsSection`, `HomeCTA` 위젯 구현
  - `ProjectCard`, `PostCard`, `shared/ui` 공통 표현 레이어 정리
  - 홈 더미 데이터 확장 및 카피 보정
  - `blog/apps/web`에서 `npm run lint`, `npm run build` 통과
- `P0-010-BE-5 공개 프로젝트 조회 API(목록/상세 slug)` 완료
- 완료 범위
  - 공개 프로젝트 목록 `GET /api/projects`
  - 공개 프로젝트 상세 `GET /api/projects/{slug}`

현재 확인된 상태
- `apps/web` 공개 라우트에는 홈 `/`, 프로젝트 목록 `/projects`, 프로젝트 상세 `/projects/[slug]`가 있고 `/posts` 목록 화면은 아직 없다.
- `apps/web/src/entities/post/ui/post-card.tsx`는 홈 최신 글 카드 언어로 이미 구현되어 있다.
- `apps/web/src/shared/ui`에는 `container`, `section-header`, `surface-card`, `cta-button`, `promo-slot`, `status-badge`가 있다.
- `apps/web/src/shared/lib/mock/home-data.ts`에는 post category, tags, readingTime, relatedProjectSlug 기반 mock data가 있다.
- `apps/web/src/shared/config/site.ts`의 메인 내비게이션은 이미 `/projects`, `/posts` 링크를 모두 가리킨다.
- 글 목록 화면은 `PostCard`, `Container`, `SectionHeader`, `CTAButton`, `SurfaceCard`, `StatusBadge`를 그대로 재사용할 수 있다.
- 공개 글 API 목록 응답은 `postId`, `slug`, `title`, `excerpt`, `lang`, `coverMediaAssetId`, `publishedAt`, `createdAt`, `updatedAt`만 제공한다.
- 공개 글 API 목록 query는 현재 `q`, `lang`만 지원하고 category/tag 필터는 아직 없다.
- 현재 프론트 `Post` 타입은 `category`, `tags`, `readingTime`, `relatedProjectSlug`, `relatedProjectTitle` 중심이라 공개 API 응답과 바로 일치하지 않는다.
- `posts`, `projects`에는 `cover_media_asset_id` 컬럼이 이미 있다.
- blog 저장소는 더 이상 `media_db`나 media compose를 소유하지 않고, media 관련 source of truth는 별도 `s-nowk/media` 저장소다.

현재 확정 범위
- blog 저장소의 다음 범위는 글 목록 공개 화면 구현으로 넘어간다.
- 이번 단계의 목표는 `/posts` 목록 화면을 검색 유입 관점의 공개 아카이브로 구현하는 것이다.
- 홈에서 만든 최신 글 카드 언어는 가능한 범위에서 재사용하되, 검색/필터 summary와 hero는 목록 전용 표현으로 추가할 수 있다.
- API 연동은 이미 가능하지만, 우선 공개 route 구조와 검색/필터 UI 정보를 먼저 정리한 뒤 mock 유지 또는 API 전환 지점을 정한다.
- `P0-017`에서는 우선 mock data 기반으로 검색/필터 UI를 조립하되, 공개 API의 `q`, `lang` query 구조와 맞는 view model 책임을 먼저 잡는 편이 맞다.
- category/tag/filter chips는 현재 공개 API 계약과 다르므로 첫 구현에서는 URL query 기반 mock filtering 또는 UI 표현 유지 중 하나를 선택해야 한다.
- `/posts/[slug]` 상세는 다음 단계 `P0-018`에서 구현하므로 이번 단계는 목록 화면과 링크 흐름 정리까지가 범위다.

세부 단계
- [x] FE-POST-01 글 목록 공개 화면 요구사항/정보구조 정리
  - [x] FE-POST-01-1 README 기준 글 목록 공개 화면 목표 다시 확인
  - [x] FE-POST-01-2 현재 `PostCard`/`shared/ui` 재사용 범위 확인
  - [x] FE-POST-01-3 `/api/posts` query 구조와 mock filtering 기준 정리
- [x] FE-POST-02 글 목록 route와 hero/filter shell 조립
  - [x] FE-POST-02-1 `app/(public)/posts/page.tsx` route 및 metadata 베이스 추가
  - [x] FE-POST-02-2 글 목록 소개 hero, 검색 form, filter chips 영역 구성
  - [x] FE-POST-02-3 목록 summary card와 section header 배치
- [ ] FE-POST-03 글 grid/empty state/query-param 흐름 조립
  - [ ] FE-POST-03-1 mockPosts 기반 search/category filtering helper 추가
  - [ ] FE-POST-03-2 글 grid, 결과 개수, active filter summary 조립
  - [ ] FE-POST-03-3 empty state와 reset 흐름 정리
- [ ] FE-POST-04 공통 표현 정리 및 검증
  - [ ] FE-POST-04-1 `PostCard` 재사용 범위와 목록 전용 표현 분리
  - [ ] FE-POST-04-2 heading hierarchy/metadata/link 구조 확인
  - [ ] FE-POST-04-3 `blog/apps/web`에서 `npm run lint`, `npm run build` 확인

계획 메모
- 글 목록 화면은 홈보다 검색 유입과 아카이브 탐색 성격을 더 분명히 가져간다.
- `cover_media_asset_id` 실제 연결 전까지는 placeholder 또는 gradient cover 영역으로 버틸 수 있어야 한다.
- 공개 API는 이미 있으므로, route 구조와 query-param 책임이 먼저 흔들리지 않게 잡는 편이 맞다.

다음 시작 지점
- `FE-POST-03-1`
- 다음 구현은 mockPosts 기반 search/category filtering helper를 추가하는 것이다.

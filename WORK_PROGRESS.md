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
- `P0-018-FE-PUB-4 글 상세(마크다운 렌더 + 관련 프로젝트)`

최근 완료 작업
- `P0-017-FE-PUB-3 글 목록(필터/검색 UI)` 완료
- 완료 범위
  - 공개 글 목록 `/posts` route, metadata, 검색 form, category chips, query-param 기반 결과 흐름 구현
  - `filterMockPosts`, `PostsArchiveHero`, `PostsResultsSection`, `PostTagList` 분리와 `PostCard` 재사용 범위 정리
  - 결과 grid, active filter summary, empty state, reset 흐름 구현
  - `blog/apps/web`에서 `npm run lint`, `npm run build` 통과
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
- `apps/web` 공개 라우트에는 홈 `/`, 글 목록 `/posts`, 프로젝트 목록 `/projects`, 프로젝트 상세 `/projects/[slug]`가 있고 공개 목록/랜딩 흐름이 한 번 완성됐다.
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
- blog 저장소의 다음 범위는 글 상세 공개 화면 구현으로 넘어간다.
- 이번 단계의 목표는 `/posts/[slug]` 상세 화면을 markdown renderer와 관련 프로젝트 흐름 기준으로 구현하는 것이다.
- 글 목록에서 만든 `PostCard`, `PostsArchiveHero`, `PostsResultsSection` 구조는 유지하고, 상세 화면은 article 중심 서술형 레이아웃을 별도로 가진다.
- API 연동은 이미 가능하지만, 우선 공개 detail route 구조와 content renderer 책임을 먼저 정리한 뒤 mock 유지 또는 API 전환 지점을 정한다.
- 공개 글 상세 API는 `contentMd`, `lang`, `coverMediaAssetId`, `publishedAt`까지 제공하므로, 상세 단계에서는 목록 타입과 분리된 view model 또는 mock detail 구조가 필요할 수 있다.
- 관련 프로젝트 연결은 현재 `relatedProjectSlug`, `relatedProjectTitle` mock 기준이 있으므로, 상세 화면에서 project CTA와 연동할 수 있는 구조를 먼저 잡는 편이 맞다.
- markdown renderer, heading hierarchy, metadata, 관련 프로젝트 CTA를 함께 확인해야 다음 SEO 단계와 연결하기 쉽다.

세부 단계
- [x] FE-POST-01 글 목록 공개 화면 요구사항/정보구조 정리
  - [x] FE-POST-01-1 README 기준 글 목록 공개 화면 목표 다시 확인
  - [x] FE-POST-01-2 현재 `PostCard`/`shared/ui` 재사용 범위 확인
  - [x] FE-POST-01-3 `/api/posts` query 구조와 mock filtering 기준 정리
- [x] FE-POST-02 글 목록 route와 hero/filter shell 조립
  - [x] FE-POST-02-1 `app/(public)/posts/page.tsx` route 및 metadata 베이스 추가
  - [x] FE-POST-02-2 글 목록 소개 hero, 검색 form, filter chips 영역 구성
  - [x] FE-POST-02-3 목록 summary card와 section header 배치
- [x] FE-POST-03 글 grid/empty state/query-param 흐름 조립
  - [x] FE-POST-03-1 mockPosts 기반 search/category filtering helper 추가
  - [x] FE-POST-03-2 글 grid, 결과 개수, active filter summary 조립
  - [x] FE-POST-03-3 empty state와 reset 흐름 정리
- [x] FE-POST-04 공통 표현 정리 및 검증
  - [x] FE-POST-04-1 `PostCard` 재사용 범위와 목록 전용 표현 분리
  - [x] FE-POST-04-2 heading hierarchy/metadata/link 구조 확인
  - [x] FE-POST-04-3 `blog/apps/web`에서 `npm run lint`, `npm run build` 확인

계획 메모
- 글 목록 화면은 홈보다 검색 유입과 아카이브 탐색 성격을 더 분명히 가져간다.
- `cover_media_asset_id` 실제 연결 전까지는 placeholder 또는 gradient cover 영역으로 버틸 수 있어야 한다.
- 공개 API는 이미 있으므로, route 구조와 query-param 책임이 먼저 흔들리지 않게 잡는 편이 맞다.

다음 시작 지점
- `P0-018-FE-PUB-4`
- 다음 구현은 글 상세 공개 화면의 markdown renderer와 관련 프로젝트 영역을 구성하는 것이다.

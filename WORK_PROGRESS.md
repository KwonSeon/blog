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
- `P0-025-FE-PUB-5 공개 화면 실제 API 연동(mock -> public API, Server fetch + searchParams + Route Handler)`

최근 완료 작업
- `P0-024-DEP-1 운영용 compose 분리(dev/prod) + restart/healthcheck` 완료
- 완료 범위
  - blog 전용 base/dev/prod compose와 root edge compose를 분리하고, 공용 `nginx`/`cloudflared`와 blog 앱/DB의 운영 경계를 정리
  - `s-nowk.com`을 blog 대표 주소로 고정하고 `blog.s-nowk.com` redirect, `s-nowk.com/api/*` blog API 연결, `stock.s-nowk.com/api/*` stock API 연결 기준 정리
  - `mysql_blog`, `blog_api`, `blog_web`, `s-nowk-nginx` healthcheck와 `service_healthy` 기반 `depends_on` 조건 보강
  - `blog/apps/api`에서 `./gradlew --no-daemon test` 통과, compose config 검증 통과, 실제 컨테이너 `healthy` 확인
- `P0-023-SEO-2 sitemap.xml / robots.txt 제공` 완료
- 완료 범위
  - `public-routes` helper로 공개 route inventory와 제외 경로 기준 정리
  - `app/sitemap.ts`에서 홈, 프로젝트 목록/상세, 글 목록/상세 공개 경로와 우선순위 기본값 제공
  - `app/robots.ts`에서 공개 경로 허용, admin 계열 차단, sitemap/host 연결
  - `blog/apps/web`에서 `npm run lint`, `npm run build` 통과
- `P0-022-SEO-1 메타/OG 적용(글/프로젝트)` 완료
- 완료 범위
  - `siteConfig.seo`, `buildPublicMetadata` helper, root metadata fallback 정리
  - 프로젝트 목록/상세, 글 목록/상세 metadata를 공통 helper 기준으로 재구성하고 OG type, canonical, keywords, article metadata를 보강
  - 글 목록 query-param 결과에 canonical `/posts` 유지와 `robots: noindex, follow` 적용
  - `blog/apps/web`에서 `npm run lint`, `npm run build` 통과
- `P0-021-FE-ADM-3 이미지 업로드(presign) + 본문 삽입` 완료
- 완료 범위
  - `admin-media-api`, `mediaApiBaseUrl`, presign/complete/upload helper와 editor upload state 구성
  - 관리자 글 에디터에서 cover image 업로드 후 `coverMediaAssetId` 자동 반영, inline image 업로드 후 markdown 본문 삽입 구현
  - `AdminMediaUploadPanel` 분리와 `PostMarkdown` image block 렌더링 추가로 preview/public markdown 표현 정리
  - `blog/apps/web`에서 `npm run lint`, `npm run build` 통과
- `P0-020-FE-ADM-2 마크다운 에디터(작성/미리보기/발행)` 완료
- 완료 범위
  - 관리자 새 글 작성 `/admin/posts/new` route, noindex metadata, editor shell, preview pane 구성
  - `AdminPostEditor`, `admin-post-api`, `AdminPostRecord` 타입과 markdown preview, draft save, publish 흐름 구현
  - `FormSelect`, `FormTextarea` 추가와 `PostMarkdown`의 `#` heading preview 지원으로 editor 표현 정리
  - `blog/apps/web`에서 `npm run lint`, `npm run build` 통과
- `P0-019-FE-ADM-1 관리자 로그인 화면` 완료
- 완료 범위
  - 관리자 로그인 `/admin/login` route, noindex metadata, 전용 shell, 입력 form 구현
  - `runtimeConfig`, `requestAdminLogin`, `admin-session` helper와 `AdminSessionGuard` 기반 보호 라우트 흐름 구현
  - 로그인 성공 후 `/admin` placeholder로 이동하고 `AdminLogoutButton`으로 세션을 정리할 수 있게 구성
  - `blog/apps/web`에서 `npm run lint`, `npm run build` 통과
- `P0-018-FE-PUB-4 글 상세(마크다운 렌더 + 관련 프로젝트)` 완료
- 완료 범위
  - 공개 글 상세 `/posts/[slug]` route, metadata, not-found, article 중심 레이아웃 구현
  - `PostDetailHero`, `PostMarkdown`, `PostRelatedProjectCta` 분리와 목록용 `PostCard` 경계 정리
  - `mockPostDetails`, `getMockPostDetailBySlug`, minimal markdown renderer, 관련 프로젝트 CTA 흐름 구현
  - `blog/apps/web`에서 `npm run lint`, `npm run build` 통과
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
- 공개 홈, 프로젝트 목록/상세, 글 목록/상세 route와 metadata/OG/sitemap/robots 구조는 이미 있다.
- 공개 route의 실제 데이터 소스는 아직 `shared/lib/mock/home-data.ts`이며, 홈/프로젝트/글 목록/상세와 sitemap slug source of truth도 같은 mock 데이터를 사용하고 있다.
- 백엔드에는 공개 프로젝트 목록/상세 `GET /api/projects`, `GET /api/projects/{slug}`와 공개 글 목록/상세/검색 API가 이미 있다.
- `apps/web`에는 `/admin/login`, `/admin`, `/admin/posts/new`가 있고 관리자 인증 뒤 새 글 작성까지 한 번 이어지는 흐름이 완성됐다.
- `apps/web/src/shared/lib/auth`에는 `requestAdminLogin`, `admin-session` helper가 있고 세션은 `localStorage` 기준으로 관리된다.
- `apps/web/src/widgets/admin-login-form`, `admin-login-shell`, `admin-session-guard`, `admin-post-editor`, `admin-post-editor-shell`, `admin-media-upload-panel`이 나뉘어 있어 로그인, 보호 라우트, editor, upload 표현 책임이 분리되어 있다.
- `apps/web/src/shared/ui`에는 `FormField`, `FormMessage`, `FormSelect`, `FormTextarea`가 있어 관리자 입력 화면 표현을 공통화할 수 있다.
- 백엔드에는 `POST /api/admin/auth/login`, `POST /api/admin/posts`, `PUT /api/admin/posts/{postId}`, `PATCH /api/admin/posts/{postId}/status`, `GET /api/admin/posts`, `GET /api/admin/posts/{postId}`가 이미 있다.
- 글 작성 request에는 `slug`, `title`, `excerpt`, `contentMd`, `visibility`, `status`, `lang`, `coverMediaAssetId`가 들어가고, 수정 request에는 `status`가 빠진다.
- 상태 변경은 별도 `PATCH /api/admin/posts/{postId}/status`로 처리해야 하므로, create/update와 publish 전환을 프론트에서 분리해서 다뤄야 한다.
- `visibility`는 `PUBLIC | PRIVATE`, `status`는 `DRAFT | PUBLISHED`만 사용한다.
- `coverMediaAssetId`는 media 업로드 성공 시 complete 응답의 `mediaAssetId`를 자동 반영할 수 있고, 여전히 수동 입력도 가능하다.
- `README`에는 presign 업로드/다운로드 규약을 media 서비스가 소유하고 blog는 API 계약 기준으로만 연동한다고 명시돼 있다.
- `apps/web/src/shared/lib/api/admin-media-api.ts`에는 admin session을 재사용하는 presign/complete/upload helper가 있고, media public content URL builder도 함께 있다.
- `apps/web/src/entities/post/ui/post-markdown.tsx`는 heading/list/blockquote/code뿐 아니라 markdown image block까지 렌더링해 preview와 공개 글 상세에서 같은 이미지를 보여줄 수 있다.
- 공개 홈, 프로젝트 목록/상세, 글 목록/상세는 모두 `buildPublicMetadata` helper 또는 `generateMetadata` 기반 title/description/canonical/OG 구조를 갖고 있다.
- root layout에는 `applicationName`, `authors`, `creator`, `keywords`, 기본 openGraph/twitter fallback이 들어가 있다.
- 글 목록은 query-param이 붙을 때 canonical을 `/posts`로 유지하면서 `robots: noindex, follow`를 내려 중복 색인 기준을 먼저 정리했다.
- 현재 공개 mock data에는 `coverMediaAssetId`가 포함돼 있지 않아 OG는 우선 title/description/text fallback 중심으로 정리돼 있다.
- 공개 route inventory는 `/`, `/projects`, `/projects/[slug]`, `/posts`, `/posts/[slug]`로 고정돼 있고, `sitemap.xml`과 `robots.txt`도 제공된다.
- 현재 실제 운영 기준은 `https://s-nowk.com` blog 대표 주소, `https://s-nowk.com/api/*` blog API, `https://stock.s-nowk.com/api/*` stock API, `https://media.s-nowk.com` media 공개 주소다.
- `blog.s-nowk.com`은 대표 주소가 아니라 `https://s-nowk.com`으로 리다이렉트되는 보조 진입점이다.
- stock 프론트는 브라우저 기준 `/api`를 사용하고, `stock-api.s-nowk.com` 공개 진입은 제거된 상태다.
- `blog/infra/compose/docker-compose.yml`은 `mysql_blog`, `flyway_blog`, `blog_api`, `blog_web`만 담는 blog 전용 base compose로 정리됐고, healthcheck와 `service_healthy` 기반 `depends_on`도 붙어 있다.
- 루트 `s-nowk/infra/compose`는 공용 edge base로 분리됐고, `nginx`/`cloudflared`는 shared network 기준으로 blog와 stock 서비스에 붙는다.
- 다음 공개 API 연동은 `Server Component fetch/DAL` + `searchParams` + `Next.js API proxy(Route Handler)`를 기본 전략으로 잡고 진행한다.
- 검색/필터의 source of truth는 URL query string으로 유지하고, 서버 원본 데이터는 가능한 한 서버에서 가져오는 편이 맞다.
- 클라이언트 재검증이 실제로 필요할 때만 `SWR`를 제한적으로 검토하고, `Zustand`는 query string/props로 표현되지 않는 shared UI interaction 상태가 생길 때만 도입한다.
- 제한적 `Zustand` 도입 후보는 모바일 필터 패널 열림/닫힘, 검색 입력 debounced draft 값, 결과 toolbar와 filter panel이 공유해야 하는 정렬 preset 정도다.
- 관리자 목록/조회 화면은 SEO보다 상호작용과 최신화가 중요하므로 `SWR`를 기본 read-fetch 계층으로 가져가는 편이 맞다.
- 향후 인증 확장은 `Spring`을 source of truth로 두고, 프론트는 `Next.js Route Handler + HttpOnly cookie`로 붙인 뒤 `Auth.js`를 프론트 세션/guard 통합 계층으로 올리는 순서가 맞다.

현재 확정 범위
- blog 저장소의 다음 범위는 `P0-025-FE-PUB-5`, 즉 공개 화면의 데이터 소스를 mock에서 실제 public API로 전환하는 것이다.
- 이번 단계의 목표는 `/`, `/projects`, `/projects/[slug]`, `/posts`, `/posts/[slug]`를 실제 공개 API 기준으로 다시 조립하면서도 현재 SEO/SSR 구조를 유지하는 것이다.
- 우선순위는 Next.js API proxy 경로 기준 고정, mock 타입과 실제 응답 차이 정리, 공개 목록/상세 데이터 흐름 교체, sitemap/slug source 전환이다.
- 브라우저 공개 요청은 same-origin Next.js Route Handler proxy로 모으고, 공개 데이터는 서버 fetch를 우선 사용한다.
- blog 저장소 기준 직접 수정 범위는 주로 `apps/web/app/(public)`, `apps/web/app/api`, `src/shared/lib/api`, `src/shared/lib/seo/public-routes.ts`와 view model mapper 계층이다. `src/shared/store`는 실제 shared UI 상태가 확인될 때만 추가한다.
- 현재 단계에서 같이 고정하는 후속 기준은 관리자 목록/조회 같은 read-heavy 화면에 `SWR`를 기본 read-fetch 계층으로 적용하는 것이다.
- 다음 단계가 끝나면 그 이후 주제는 `P0-026-DEP-2`, 즉 공개 경로와 reverse proxy 책임 정리로 다시 이어간다.

세부 단계
- [ ] PUBLIC-API-01 공개 API 연동 기준 정리
  - [ ] PUBLIC-API-01-1 공개 API 응답 스펙과 현재 mock/view model 차이 정리
  - [ ] PUBLIC-API-01-2 Next.js API proxy 경로와 same-origin 호출 기준 정리
  - [ ] PUBLIC-API-01-3 URL query source of truth와 제한적 `Zustand` 도입 조건 정리
- [ ] PUBLIC-API-02 proxy/data 계층 추가
  - [ ] PUBLIC-API-02-1 projects/posts 공개 read용 Route Handler proxy 추가
  - [ ] PUBLIC-API-02-2 public API client와 view model mapper 추가
  - [ ] PUBLIC-API-02-3 에러 fallback과 빈 결과 처리 기준 정리
- [ ] PUBLIC-API-03 공개 route 실제 API 전환
  - [ ] PUBLIC-API-03-1 홈과 프로젝트 목록/상세 데이터를 mock에서 실제 API 기준으로 교체
  - [ ] PUBLIC-API-03-2 글 목록/상세 데이터를 mock에서 실제 API 기준으로 교체
  - [ ] PUBLIC-API-03-3 관련 글/관련 프로젝트 흐름을 실제 응답 기준으로 재조립
- [ ] PUBLIC-API-04 클라이언트 상태와 SEO 보강
  - [ ] PUBLIC-API-04-1 글 목록 filter/search 상태를 URL query 중심으로 정리하고 필요 시 shared UI 상태만 제한적 `Zustand` 도입
  - [ ] PUBLIC-API-04-2 sitemap과 공개 slug source를 mock에서 실제 데이터 기준으로 전환
  - [ ] PUBLIC-API-04-3 metadata, not-found, loading fallback 기준 정리
- [ ] PUBLIC-API-05 검증 및 문서 반영
  - [ ] PUBLIC-API-05-1 public route, proxy route, `npm run lint`, `npm run build` 검증
  - [ ] PUBLIC-API-05-2 README, WORK_PROGRESS 완료 상태 반영
  - [ ] PUBLIC-API-05-3 다음 시작 지점을 `P0-026-DEP-2`로 전환

계획 메모
- 현재 공개 MVP의 빈 곳은 UI/SEO/배포가 아니라 공개 프론트와 실제 public API 사이의 마지막 연결이다.
- 관리자 화면은 이미 실제 API를 쓰고 있으므로, 이번 단계는 공개 화면 read path를 같은 방식으로 통일하는 작업에 가깝다.
- 기본은 `Server Component + searchParams + Route Handler`이고, `SWR`는 클라이언트 재검증이 필요할 때만, `Zustand`는 shared UI interaction 상태가 확인될 때만 도입하는 편이 공식 흐름에 가깝다.
- 반대로 관리자 목록/조회는 `SWR`를 먼저 검토하는 편이 자연스럽다. 이후 `/admin/posts`, `/admin/projects` 같은 read-heavy 화면에서 같은 기준을 재사용한다.
- proxy 계층에는 응답 정규화와 에러 매핑을 넣고, 필요 시 `zod`로 응답 계약 검증을 추가하는 편이 이후 라우팅/DNS 작업에서도 덜 흔들린다.
- 인증 로드맵도 같은 원칙으로 가져간다. P1에서는 Spring 기반 일반 사용자 인증과 refresh/cookie 기준을 먼저 닫고, 그 다음 프론트 세션 계층으로 `Auth.js`를 얹는 편이 책임 분리가 명확하다.
- 공개 화면이 실제 API 기준으로 전환돼야 이후 `P0-026-DEP-2`, `P0-031-E2E-1`도 의미 있는 검증이 된다.

다음 시작 지점
- `PUBLIC-API-01-1`
- 다음 구현은 공개 API 응답 스펙과 현재 mock/view model 차이를 먼저 정리하는 것이다.

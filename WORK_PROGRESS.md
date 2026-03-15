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
- `P0-024-DEP-1 운영용 compose 분리(dev/prod) + restart/healthcheck`

최근 완료 작업
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
- 현재 공개 mock data에는 `coverMediaAssetId`가 포함돼 있지 않아, 이번 단계 OG는 우선 title/description/text fallback 중심으로 정리됐다.
- 현재 공개 route inventory는 `/`, `/projects`, `/projects/[slug]`, `/posts`, `/posts/[slug]`로 고정돼 있고, `sitemap.xml`과 `robots.txt`도 제공된다.
- 공개 slug source of truth는 `mockProjects`, `mockPostDetails`라서 sitemap도 같은 mock 데이터를 기준으로 생성한다.
- 현재 `blog/infra/compose/docker-compose.yml`은 `mysql_blog`, `flyway_blog`, `blog_api`, `blog_web`만 담는 blog 전용 base compose로 정리됐다.
- 현재 루트 `s-nowk/infra/compose/docker-compose.yml`은 공용 edge base로 분리됐고, `nginx`는 shared network를 기준으로 blog 컨테이너에 붙는 구조로 옮기고 있다.
- compose 서비스 다수에는 `restart: unless-stopped`가 있지만, healthcheck는 아직 없고 dev/prod 분리는 이제 base/dev/prod override 뼈대까지 들어온 상태다.
- 따라서 다음 단계는 SEO가 아니라 운영 배포 구성을 분리하고 healthcheck를 붙이는 작업에 가깝다.
- 현재 공용 edge/runtime 성격의 파일은 루트 `s-nowk/infra`로 옮기고, blog 전용 서비스 compose는 `blog/infra` 아래에 남기는 방향이 맞다.
- 현재 compose에는 blog web/api 서비스 정의가 들어왔고, 남은 과제는 prod에서 root edge와 blog 서비스가 shared network로 만나는 조건을 정리하는 것이다.
- 현재 blog compose는 `name: blog`로 고정했고, 내부 기동 순서는 `mysql_blog -> flyway_blog -> blog_api -> blog_web` 기준 `depends_on` 조건으로 정리 중이다.
- root edge compose는 `name: edge`로 고정했고, prod에서는 shared network `s-nowk-edge`를 먼저 만든 뒤 blog prod 서비스가 여기에 붙고, 마지막에 `cloudflared`가 붙는 순서로 보는 편이 맞다.
- 현재 blog 전용 env는 `blog/infra/compose/.env`와 `.env.example` 기준으로 정리했고, stock 서비스용 env는 `stock/infra/compose/.env`와 `.env.example`로 분리했다.
- 공용 edge용 env도 루트 `s-nowk/infra/compose/.env`와 `.env.example`로 따로 두는 방향으로 정리했다.
- 루트 `s-nowk/infra` 디렉터리는 실제로 생성됐고, 공용 `nginx`, `cloudflared` 설정은 그쪽에서 별도 compose 파일로 관리하는 구조를 잡기 시작했다.
- 즉 현재 blog 운영 경계는 blog 서비스 compose와 공용 edge compose는 분리됐고, 남은 일은 dev/prod override, healthcheck, shared network/depends_on 기준을 보강하는 것이다.
- `media` 저장소는 이미 `infra/compose/.env.example`, `.env.prod.example`, `docker-compose.yml` 구조를 쓰고 있어, 루트 infra도 비슷한 운영 예시 파일 구조를 따라가는 편이 일관된다.

현재 확정 범위
- blog 저장소의 다음 범위는 운영용 compose를 dev/prod 기준으로 분리하고 restart/healthcheck를 붙이는 것이다.
- 이번 단계의 목표는 현재 단일 compose를 운영 관점에서 재정리하고, blog web/api/nginx/mysql/flyway 기준 기동 구조를 명확히 하는 것이다.
- 우선순위는 dev/prod 파일 분리, blog 관련 서비스 경계 명확화, healthcheck 추가, restart 정책 재검토다.
- stock 관련 서비스는 `stock/infra/compose`로 분리하는 방향을 잡았고, blog 작업 범위에서는 blog 운영 구성과 공용 edge 구성을 먼저 닫는 편이 맞다.
- nginx는 현재 blog와 stock 도메인을 함께 다루고 있으므로, compose 분리 전에 어떤 파일이 blog 전용이고 어떤 파일이 공용 edge인지 기준을 먼저 고정해야 한다.
- 현재 기준으로 blog 전용 compose 후보는 `mysql_blog`, `flyway_blog`, 이후 추가될 `blog_api`, `blog_web`이고, `nginx`, `cloudflared`, 공용 compose 진입 파일은 루트 `s-nowk/infra`로 이동하는 공용 edge 후보로 보는 편이 자연스럽다.
- blog 서비스는 `blog/infra/compose/docker-compose.yml` base 위에 `docker-compose.dev.yml`, `docker-compose.prod.yml` override를 두고, 공용 edge는 루트 `s-nowk/infra/compose`에서 별도 base/prod 파일로 관리하는 편이 맞다.
- `dev`는 로컬 개발 기준이라 blog 쪽에서 localhost port binding과 dev용 public URL만 열고, `cloudflared`와 공개 도메인 edge는 기본 범위에서 제외하는 편이 맞다.
- `prod`는 blog web/api가 shared network로 공용 `nginx`에 붙고, 공용 edge 쪽은 루트 `docker-compose.prod.yml`에서 `cloudflared`를 따로 올리는 편이 맞다.
- 공용 edge 범위는 루트 `s-nowk/infra`가 소유하고, blog 저장소는 blog 앱 컨테이너, DB, migration, blog 전용 override를 유지하는 방향으로 정리하는 편이 안전하다.
- compose로 강제 가능한 의존 순서는 blog 내부까지이고, root edge와 blog prod의 교차 의존은 별도 compose 프로젝트라 `depends_on` 대신 shared network와 기동 순서 문서화로 다루는 편이 맞다.

세부 단계
- [ ] DEPLOY-01 운영 compose 목표/경계 정리
  - [x] DEPLOY-01-1 README 기준 운영 compose 분리 목표 다시 확인
  - [x] DEPLOY-01-2 현재 compose/nginx/env에서 blog 관련 서비스 경계 정리
  - [x] DEPLOY-01-3 dev/prod 파일 분리와 공용 edge 유지 범위 결정
- [ ] DEPLOY-02 compose 파일 분리
  - [x] DEPLOY-02-1 blog 전용 compose 초안 추가
  - [x] DEPLOY-02-2 dev/prod override 또는 별도 파일 구조 정리
  - [x] DEPLOY-02-3 blog web/api/mysql/flyway 의존 순서 정리
- [ ] DEPLOY-03 restart/healthcheck 보강
  - [x] DEPLOY-03-1 blog web/api/nginx healthcheck 추가
  - [x] DEPLOY-03-2 restart 정책과 depends_on 조건 정리
  - [x] DEPLOY-03-3 운영 환경 변수와 예시 파일 정리
- [ ] DEPLOY-04 검증 및 문서 반영
  - [ ] DEPLOY-04-1 compose config 또는 기동 검증
  - [ ] DEPLOY-04-2 README, WORK_PROGRESS 완료 상태 반영
  - [ ] DEPLOY-04-3 다음 라우팅 작업 기준 정리

계획 메모
- 배포 단계는 코드 구현보다 인프라 경계 정리가 먼저라서, compose 파일을 바로 쪼개기 전에 현재 공용 edge와 blog 전용 서비스 범위를 먼저 고정하는 편이 안전하다.
- 현재 stack은 stock과 blog가 같이 묶여 있으므로, blog 전용 배포 기준을 정리할 때 기존 stock 동작을 깨지 않게 파일 분리 전략을 먼저 세워야 한다.
- 특히 공용 infra를 `blog/infra`에 남겨두는 방향이 아니라 루트 `s-nowk/infra`로 승격하는 방향을 먼저 전제로 잡아야 이후 분리가 덜 꼬인다.
- healthcheck는 단순 추가보다 서비스별 readiness 기준이 필요하므로, web/api/nginx에 어떤 확인 경로를 쓸지 함께 정리해야 한다.
- 즉 다음 구현은 서비스별 health/readiness 기준을 실제로 붙이고, restart와 env 예시를 운영 기준으로 보강하는 단계로 바로 이어가면 된다.

다음 시작 지점
- `DEPLOY-04-1`
- 다음 구현은 compose config와 실제 기동 상태를 다시 검증하고 README/WORK_PROGRESS 완료 상태를 반영하는 것이다.

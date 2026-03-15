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
- `P0-029-OPS-1 포트포워딩/방화벽(80/443만) + 내부 포트 차단 확인`

최근 완료 작업
- `P0-028-DEP-3 Tunnel 기준 HTTPS 강제 + 리다이렉트` 완료
- 완료 범위
  - `cloudflared`의 `media.s-nowk.com`도 `nginx`를 통하도록 정리해서 공개 host의 redirect 책임을 `nginx`로 일원화
  - `nginx`에서 `CF-Visitor`/`X-Forwarded-Proto` 기준 `http -> https` redirect 적용
  - 외부 기준 `http://s-nowk.com`, `http://stock.s-nowk.com`, `http://media.s-nowk.com`, `http://blog.s-nowk.com`, `http://www.s-nowk.com`이 모두 `301 https://...`로 전환되는 것 확인
  - `https://s-nowk.com/media/assets/{mediaAssetId}/content`가 계속 `200`으로 응답하는 것 확인
- `P0-027-DNS-1 Cloudflare DNS 연결(CNAME to Tunnel) + Proxy 정책 확정` 완료
- 완료 범위
  - 실제 Cloudflare DNS record가 `A 레코드`가 아니라 proxied `CNAME -> 539fabab-e780-4e48-94e3-0ca7c24f1d42.cfargotunnel.com` 기준임을 API로 확인
  - `s-nowk`, `blog`, `www`, `stock`, `media`, `portainer` 공개 host만 유지하고 `db` ingress는 제거
  - 문서 기준도 direct origin `A 레코드`가 아니라 `Cloudflare Tunnel CNAME + Proxy`로 전환
- `P0-026-DEP-2 Nginx 라우팅(/ -> web, /api -> api, /media -> storage)` 완료
- 완료 범위
  - root `nginx`에 `s-nowk.com/media/* -> media public content` 라우팅 추가
  - `blog` 공개 media URL을 `https://s-nowk.com/media/assets/{mediaAssetId}/content` 기준으로 분리
  - `media` 서비스의 public metadata URL 조립 기준을 `/media` 공개 경로로 맞추고 `./gradlew --no-daemon test` 통과
  - `blog_web`, `media-api`, `nginx`를 재기동하고 외부 `https://s-nowk.com/media/assets/{mediaAssetId}/content` 응답 확인
- `P0-025-FE-PUB-5 공개 화면 실제 API 연동(mock -> public API, Server fetch + searchParams + Route Handler)` 완료
- 완료 범위
  - 공개 프로젝트/글 route를 mock 대신 실제 public API 기반 page-data helper와 view model mapper로 전환
  - projects/posts 공개 read용 Route Handler proxy 추가와 q/lang 중심 검색 흐름, 관련 콘텐츠 fallback, custom not-found/loading 처리 정리
  - sitemap의 slug source를 실제 public API 기준으로 전환하고 `blog/apps/web`에서 `npm run lint`, `npm run build` 통과
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
- `s-nowk.com`, `blog.s-nowk.com`, `stock.s-nowk.com`, `media.s-nowk.com`, `portainer.s-nowk.com`은 모두 Cloudflare Tunnel target `CNAME + Proxy` 기준으로 운영 중이다.
- 현재 Cloudflare DNS에는 `A 레코드`가 아니라 `539fabab-e780-4e48-94e3-0ca7c24f1d42.cfargotunnel.com`을 가리키는 proxied `CNAME`이 연결돼 있다.
- `cloudflared ingress`도 위 5개 공개 host와 `www`만 남겨 DNS inventory와 일치한다.
- `s-nowk.com`은 `blog_web`, `s-nowk.com/api/*`는 `blog_api`, `s-nowk.com/media/*`는 `media-api`의 public content로 분기된다.
- `blog.s-nowk.com`과 `www.s-nowk.com`은 대표 주소 `https://s-nowk.com`으로 리다이렉트된다.
- `media.s-nowk.com/api/*`는 media admin/public API origin으로 유지하고, 공개 asset URL만 `https://s-nowk.com/media/*`로 통일했다.
- 외부 기준 `https://s-nowk.com/media/assets/{mediaAssetId}/content`는 실제 media content를 내려주고 있다.
- `http://s-nowk.com`, `http://stock.s-nowk.com`, `http://media.s-nowk.com`, `http://blog.s-nowk.com`, `http://www.s-nowk.com`은 모두 `https://...`로 리다이렉트된다.

현재 확정 범위
- 현재 범위는 `P0-029-OPS-1`, 즉 외부 공개 포트와 내부 서비스 포트가 의도대로 닫혀 있는지 다시 확인하는 것이다.
- `LE`나 `Cloudflare Origin` 기반 direct origin HTTPS는 현재 공인 IP 변동성 때문에 1차 범위에서 제외하고, Tunnel edge HTTPS를 유지하는 방향으로 계속 간다.

세부 단계
- [ ] OPS-01 공개 포트와 내부 포트 점검
  - [ ] OPS-01-1 외부 공개 진입 포트와 Cloudflare/Tunnel 경로 정리
  - [ ] OPS-01-2 DB/내부 서비스 포트가 외부에 직접 노출되지 않는지 확인
  - [ ] OPS-01-3 운영 기준 문서 반영
- [ ] OPS-02 최소 백업 기준 확인
  - [ ] OPS-02-1 blog DB dump 경로와 media 볼륨 백업 기준 정리
  - [ ] OPS-02-2 수동 복구 가능 기준 확인
  - [ ] OPS-02-3 다음 시작 지점을 `P0-031-E2E-1`로 전환

계획 메모
- 현재 운영은 direct origin 노출이 아니라 Cloudflare Tunnel edge가 공개 진입점이다.
- 공인 IP가 계속 바뀔 수 있으므로 `A 레코드 + 고정 origin` 방향은 1차 운영 기준으로 맞지 않는다.
- 따라서 `P0-027`과 `P0-028`은 `CNAME to Tunnel + Proxy`와 edge HTTPS 강제 기준으로 정리하는 편이 현재 운영 방식과 맞다.
- `/media` 공개 경로는 대표 도메인 아래에 유지하되, media admin/public API origin은 `media.s-nowk.com/api/*`로 분리해도 괜찮다.

다음 시작 지점
- `OPS-01-1`
- 다음 구현은 Cloudflare/Tunnel 공개 진입과 내부 서비스 포트 노출 범위를 다시 점검하는 것이다.

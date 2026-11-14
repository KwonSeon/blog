# 2026-11-14 member public chat deployed; next: recommendations

- P2-007..010 deployed dev→main→production at `25fad1a`. Exact CI dev43591156345/main43591427495 succeeded. API254/web16 tests plus lint/build passed; final development WebSocket connection/empty history verified.
- Production switch94/94 HTTP probes passed, with actual promotion inside the sample window. Blog dev API/web only paused about58seconds for capacity and restored healthy; production remained available. No production test messages were created. Anonymous production API401, login entry, noindex/nofollow and empty DB verified.
- Both environments have correctly encoded UTF-8 lobby titles and zero chat fixtures. Manual MySQL commands must use `--default-character-set=utf8mb4`; CLI's default charset initially corrupted only the SQL-seeded fixture/lobby title, which was corrected and HEX-verified. Real API Korean text passed independently.
- Private edge/tunnel/runtime config remains outside Git. Controller18 tests cover reused-container drain time and WebSocket route preservation. Telegram remains disabled; production external recipients are unconfigured.
- Next ordered topic is P2-011 rules-based related-post recommendations, followed by P2-012 embedding preparation. These are not implemented by this chat release.

# 2026-11-14 current: member public chat

- User chose a shared public room for members, replacing the initial private-inquiry assumption. P2-007..010: authenticated shared lobby, persistent paged messages, own/editor deletion, idempotent writes/rate limits, STOMP invalidation plus HTTP recovery, Astryx UI.
- API254/web16 tests, lint/build passed. Broker heartbeat is isolated from ordinary scheduled DB/mail jobs; regression test passed. Dev CI43590054880 and rollout `f56993d` succeeded; V11 followed verified backup `20261114T112444Z-595e8df7`. Browser two-account delivery~2.9s, eight concurrent retries→one message, owner/editor deletion and other-member404, literal HTML safety, actual socket reconnect, HTTP fallback with sockets blocked, 50+10 disjoint history pages and390px no overflow passed. Removed60 synthetic messages and both test rate rows; anonymous401/cross-origin403 and logout verified. Removed duplicate Astryx character counter; lint/build passed again. Main rollout follows.
- External notifications documentation revision `73f60eb` is now dev/main/production. Telegram remains disabled; no real sends.

# 2026-11-14 external notifications completed; next: real-time chat

- P2-005 optional Telegram delivery and P2-006 Kakao decision deployed dev then main at `d68c3a5`. CI dev43587869405/main43588223295 succeeded. API236/web14 tests and lint/build passed.
- Development browser verified default-off status, unconfigured activation400, member/anonymous denial, cross-origin403, retry recovery, mobile390px no overflow. Configured start/stop/consent UI was tested with browser-local mock responses, then restored; no live messages sent. Synthetic editor logged out.
- V10 applied to dev/production after verified backups. Both remain disabled with queue0. Real MySQL guard contention serialized. Live Telegram credentials, recipient consent and actual delivery roundtrip remain unconfigured/unverified.
- Production availability182/182 passed, covering the switch. Initial memory gates blocked before changes; only dev app containers were paused about60seconds and restored healthy. Development recreate also briefly returned502 before readiness; no zero-downtime claim for dev.
- Private production retention fix: repeated reused API references now use the latest drain time only; 17 controller tests passed. Active/immediate rollback remain protected. Infrastructure and credentials remain outside Git.
- Next ordered implementation: P2-007..010 real-time chat (server, persistence, authenticated protocol/history, UI). Not implemented by this notification release.

# 2026-11-14 roadmap: external notifications

- P2-001..004 deployed dev/main at `7b68c07`. API220/web14 tests and lint/build passed. V9 applied dev then production after verified backups. Real MySQL uniqueness/check/cascade probes passed; browser two-account owner/reply notifications, five concurrent replays→one alert, read/read-all, other-owner404, anonymous401, cross-origin403, private-content hiding and mobile layout passed. Synthetic post/comments/notifications removed; logged out. Production switch120/120 requests succeeded.
- P2-005/006 implementation: optional Telegram delivery preparation with explicit enablement and reviewed recipient bindings; Kakao official prerequisites/decision. Neither environment has Telegram/BizMessage credentials. No external message, paid broadcast, subscription or business account registration is authorized or attempted by this implementation work. Live activation remains dependent on configured/consenting recipients.

- Validation: API236 tests, web14 tests/lint/build passed. Dev V10 applied after verified backup `20261114T105851Z-d973878d`; default off and queue0. Two real MySQL guard transactions serialized. Telegram HTTP is mocked; no real sends. Admin browser QA and dev/main rollout follow.

# 2026-11-14 roadmap: member notifications

- P1-025 + requested Astryx skeletons deployed dev/main at `a7a949d`; lint/build/web14 tests, all five slow-response route transitions, reduced motion, mobile/desktop no overflow, retry recovery and visibility checks passed. Production switch90/90 requests succeeded; API reused, web replaced. Temporary QA servers stopped and fixture removed.
- API220 tests and web14/lint/build passed. H2 constant-set CHECK expressions retained a closed DDL session; the isolated test datasource now keeps that session alive, with constraints intact. Real development MySQL V9 applied after a verified private backup.
- Current P2-001..004: durable in-app comment/reply notifications using signed Identity subjects and aliases, transactional domain events, owner-scoped list/unread/read APIs, cursor pagination and accessible inbox. No external messages in this step. Runtime editor bindings are the only mapping from local content author to Identity recipient.

# 2026-11-14 roadmap: performance and UX

- P1-024 deployed dev/main at `2cc10d8`. CI passed, development browser/origin healthy. Main controller recorded the tools-only revision without restarting either app container; exact start times/restart counts unchanged. Daily03:20 backup and Sunday04:20 isolated-restore LaunchAgents registered. First launchd backup and restore commands both exit0.
- Backups: three InnoDB DBs (21/2/12 tables), 233 object archive files; restore and hashes passed, no temporary containers/volumes remain. Eight backup regression tests plus five monitor tests. Same-machine/private files only, no offsite encryption or atomic cross-service snapshot.
- Skeleton steering: actual Astryx Skeleton shapes for home/posts/post detail/projects/project detail, matching responsive columns and administrator layout/section preferences. Local public-only slow proxy (4.5s) verified all five fallback→content transitions; 390px/1280px no overflow, reduced motion animation=none and zero interactive placeholder controls. Lint/build/web14 tests passed.
- Current P1-025: request-scoped detail memoization and bounded API waits; independent data fetching in parallel; lazy Markdown images; accessible loading/retry/reduced-motion behavior. Preserve Astryx shell skip-link behavior and existing private/public visibility semantics.
- Development QA passed at `2d144bc`: comment request blocked then retry recovered, lazy/async Markdown image attributes and successful image load, 390px no overflow, Astryx skip link focuses main, PUBLIC→PRIVATE and PUBLIC+DRAFT hide the content with noindex. Synthetic post deleted and editor logged out. Removed misleading loading text after a failed request; lint/build passed again.
- GitHub existing keychain PAT now has `repo, workflow` after user reauthentication; no token rotation or extra scopes.
- Baseline browser samples: development home DOMready542ms, post510ms/load1986ms; production empty home823ms. Earlier isolated privacy cold load19s did not repeat (1782ms). These are individual samples, not field Core Web Vitals or a speedup claim.

# 2026-11-14 roadmap: automated backups

- P1-023 deployed dev/main at `b1481c1`; both five-minute monitors registered and healthy (dev 8 checks, production 13). Local failure/recovery records only; no external alert delivery configured.
- Current P1-024: private scheduled SQL backups for Blog/shared Identity/Media and a read-only object volume archive, integrity manifests, retention after successful backup, and isolated restore rehearsal. Online DB/object archives are not a cross-service point-in-time snapshot.

- Backup and actual isolated restore passed: Blog 21 tables, shared Media 2, shared Identity 12; 233 archive files (53,776,952 bytes) extracted/hash checked. Temporary DB/volume absent after cleanup. MySQL image lacks mysqlcheck; use SQL CHECK TABLE and wait for the final mysqld process rather than its temporary initializer. Operations regression suite13/13; web test entry14/14. Scheduling follows dev/main CI.

# 2026-11-14 roadmap: operations monitoring

- P1-021/022 implementation deployed dev/main at `47d1d92`. Public about/contact/editorial/privacy links and editor-only advertising readiness screen validated; mobile overflow absent. Web tests13/13, lint/build and CI passed. Existing public publisher ID is private runtime configuration; source has no account identifier. Advertising remains disabled: actual site approval, slot and published/tested consent messages are not complete.
- Production switch monitoring: 36 requests, zero failures. A development privacy-page cold chunk took ~19 seconds once; page completed successfully and loaded zero Google ad scripts. Keep this observation for P1-025 performance checks.
- Current P1-023: development healthy probes and a safe unavailable-endpoint → recovery rehearsal passed; five operations tests run through the existing web test discovery in CI. A new workflow job was rejected because the Git token lacks workflow scope; the existing workflow file is unchanged. Private deployer now recognizes unchanged API/web trees and runtime for docs/tools-only revisions (15 safety tests); it retains the last distinct app rollback and avoids restarting apps.
- Scope: bounded periodic public/origin health probes, container/release health and private local failure records. No automatic messages to third parties or destructive remediation.

# 2026-11-14 roadmap: site readiness and advertising

- P1-020 deployed dev/main at `01a7043`: WebSite, BlogPosting, visible BreadcrumbList and project WebPage markup; safe JSON serialization, real timestamps/images, public all-language sitemap and canonical URLs. Web tests 11/11 and lint/build passed. Browser verifies schemas, canonical, development noindex and private-content not-found/noindex. Next streamed not-found pages return HTTP 200 with noindex; do not claim page-level 404.
- Production SEO monitoring: 40 requests, zero failures. API code/config unchanged, so the strengthened private release controller reused the current API; container name, start time and restart count remained identical. Safety tests14/14.
- Current topic P1-021/022: public about/contact/editorial pages, privacy navigation and opt-in advertising plumbing. Advertising remains off until valid publisher/slot and actual consent-platform setup are ready; do not claim AdSense approval or sufficient production content.

# 2026-11-14 roadmap: structured data

- Search P1-017..019 deployed to dev/main at `4b145d3`. 209 API tests, 8 web tests, lint/build passed. Real MySQL FULLTEXT plan verified; ngram boolean phrases avoid unrelated partial-token matches. Browser checks covered English/Korean relevance, public/draft filters, 100-row bound, literal wildcard and empty results, and invalid query 400. Removed all 107 synthetic fixtures.
- Search production switch monitoring: 42 HTTP requests, zero failures. V8 applied after verified backups; no runtime credentials entered Git.
- Current topic P1-020: truthful BlogPosting/WebSite/Breadcrumb structured data, safe JSON-LD serialization and canonical/sitemap consistency. Public content only; no invented ratings, authors, dates or images.

# 2026-11-14 roadmap: search

- Email subscriptions P1-014..016 deployed to dev and main at `cf49faa`. 205 API tests, 8 web tests, lint/build passed. Browser double opt-in, captured new-post mail, unsubscribe/address removal and mobile layout passed. Eight concurrent real MySQL requests queued exactly one mail; synthetic data and captured mail were removed.
- Production V7 followed a verified backup; existing Resend configuration remains private. No test mail was sent to real recipients. Production switch monitoring: 30 HTTP requests, zero failures. Production subscription readiness checked separately.
- Current topic P1-017..019: indexed public post search, bounded relevance results and privacy-conscious aggregate search diagnostics. Preserve public/published filtering, language filters and decimal-string IDs.

# 2026-11-14 roadmap: email subscriptions

- Comments P1-009..013 completed through dev/main/production at `139d0d0`. 193 API tests, 8 web tests and browser registration/reply/edit/moderation checks passed; eight concurrent browser retries produced one comment. QA comments were removed.
- Fixed actual MySQL driver datetime mapping and write-lock upgrade deadlocks before promotion. Actual MySQL probes passed 40-level parent deletion and 24 concurrent guard transactions.
- First production comments attempt hit Docker VM memory pressure. Candidates stayed unrouted; old active release was retained. Retired drained historical blog containers, bounded new candidate memory and added a >=1792MiB VM headroom gate + failed-candidate cleanup. Safety tests11/11. After successful retry, 32/32 internal/external stability checks passed. Earlier timeout evidence is retained privately; do not claim incident-free deployment.
- Current topic P1-014..016: double opt-in email subscriptions, explicit unsubscribe, durable new-public-post delivery. Reuse existing Resend credentials only in private runtime; development delivery goes exclusively to shared Mailpit.
- Requirements: generic subscribe responses, short-lived confirmation tokens stored hashed, encrypted queued token payloads, abuse limits, no historical-post blast, opt-out before dispatch, idempotent provider retries, no real-recipient test sends.

# 2026-11-14 roadmap: comments

- Views P1-006/007/008 completed through dev/main/production at `3d236c7`; 182 API tests and browser dedup/privacy checks passed, deployment monitoring 36/36 requests succeeded.
- Current scope P1-009..013: adjacency-based replies without a product depth limit, flat keyset pagination assembled into a tree without recursive API queries, authenticated writes, owner edits and owner/editor soft deletion.
- Abuse controls: database-serialized per-member write budgets, short write spacing, duplicate-text rejection, bounded plain text and idempotent create keys. Signed Identity aliases retain ownership; public responses never reveal account subjects.
- Feature branch `feature/roadmap-comments`; tests and dev rollout precede main promotion.

# 2026-11-14 roadmap: content views

- Likes (P1-003/004/005): dev and main CI passed; production `8670ab3` deployed. Synthetic member browser checks covered save/cancel/refresh, 8 concurrent requests, private/draft rejection and 390px layout. Test likes were removed.
- P1-006/007/008: public-only daily browser view deduplication, signed BFF ingestion, atomic lifetime totals and short identifier retention. No IP/account identity is stored.
- Views validation and dev rollout are in progress; main promotion follows development browser verification.
- Next ordered topic after views: comments (P1-009 through P1-013).

# 2026-11-14 roadmap: content likes

- P1-001/002 are fulfilled by shared Identity/OIDC instead of the historical Auth.js plan.
- P1-003/004/005 implementation: authenticated likes for public published posts and active projects, anonymous counts, idempotent desired-state PUT, parent-row locking, cascade cleanup and signed merged-subject reconciliation on interaction.
- Web: Astryx pressed-state button, pending/error feedback, same-origin BFF and signed allowlisted return path after login.
- Validation: API tests (including concurrent duplicate writes and member/anonymous authorization), web lint/tests/build passed. Development V4 applied after backup; dev browser verification precedes main promotion.
- Next ordered topic: P1-006/007/008 view counting. No later roadmap item is marked complete by this change.

# 2026-11-14 production deployment preparation

- Added a private main-only CI-gated blue/green controller on the Mac mini; current main is the baseline, dev remains unpromoted.
- Added schema compatibility gate, readiness checks, graceful Nginx reload, previous-static fallback, route rollback and interrupted-transaction blocking.
- Isolated same-production-image switch/rollback rehearsal: 195 monitored HTTP requests, zero failures; six safety tests passed. Production routing was unchanged.
- First dev-to-main promotion still requires compatible DB migration and production shared-Auth configuration review. See docs/DEVELOPMENT.md.

# 2026-11-14 development restart

Current work: latest compatible stack + Astryx components, shared Identity OIDC and Media integration, complete editor/list/edit flow, development-only CI/CD.

Implementation and boundaries: [development architecture](docs/DEVELOPMENT.md). Earlier progress below is retained as historical context; its local-admin-login and placeholder descriptions are superseded by shared Identity and the new Studio screens.

---

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
- `P1-001-AUTH-1 회원가입/로그인(일반 사용자) API + USER 권한 모델 + refresh/cookie 기준 정리`

최근 완료 작업
- `P0-031-E2E-1 외부 환경 E2E 재검증(작성→발행→업로드→공개)` 완료
- 완료 범위
  - `infra/scripts/e2e-publish-flow.sh` 기준으로 외부 `https://s-nowk.com` 경로에서 관리자 로그인, presign upload, complete, 글 작성, 발행, 공개 화면 확인까지 한 번에 검증
  - `blog` admin presign proxy가 내부 `minio` host를 그대로 노출하지 않도록 `https://media.s-nowk.com/media-assets/*` 기준으로 rewrite
  - root `nginx`에서 `media.s-nowk.com/api/* -> media-api`, `media.s-nowk.com/media-assets/* -> MinIO`로 분리
  - 임시 관리자 계정, 임시 글, 임시 media asset까지 cleanup되는 것 확인
- `P0-030-OPS-2 백업 최소 적용(DB 덤프 + MinIO 볼륨)` 완료
- 완료 범위
  - `infra/scripts/backup-blog-db.sh`로 `mysql_blog` 기준 SQL dump + gzip + sha256 생성 경로 정리
  - `infra/scripts/backup-media-storage.sh`로 `media_minio_data` volume archive + sha256 생성 경로 정리
  - `infra/BACKUP.md`에 백업, 검증, 수동 복구 절차 문서화
  - 실제 스크립트를 실행해 dump/archive 생성, checksum 검증, archive listing까지 확인
- `P0-029-OPS-1 포트포워딩/방화벽(80/443만) + 내부 포트 차단 확인` 완료
- 완료 범위
  - root edge에서 `cloudflared -> nginx`를 `host.docker.internal:8080`이 아니라 edge network의 `nginx:80` direct 연결 기준으로 정리
  - root edge `nginx` bind를 `127.0.0.1:8080`으로 제한해 공개 진입과 로컬 origin/debug 포트를 분리
  - `media`의 `mysql`, `minio api`, `minio console`을 각각 `127.0.0.1:3331`, `127.0.0.1:9100`, `127.0.0.1:9101`로 제한
  - `docker ps`, `lsof -nP -iTCP -sTCP:LISTEN` 기준으로 내부 포트가 모두 loopback만 듣는 것 확인
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
- `cloudflared`는 edge network의 `nginx:80`으로 직접 붙고, root edge `nginx`만 `127.0.0.1:8080`을 열어 로컬 origin/debug 경로로 유지한다.
- `s-nowk.com`은 `blog_web`, `s-nowk.com/api/*`는 `blog_api`, `s-nowk.com/media/*`는 `media-api`의 public content로 분기된다.
- `blog.s-nowk.com`과 `www.s-nowk.com`은 대표 주소 `https://s-nowk.com`으로 리다이렉트된다.
- `media.s-nowk.com/api/*`는 media admin/public API origin으로 유지하고, 공개 asset URL만 `https://s-nowk.com/media/*`로 통일했다.
- 외부 기준 `https://s-nowk.com/media/assets/{mediaAssetId}/content`는 실제 media content를 내려주고 있다.
- `http://s-nowk.com`, `http://stock.s-nowk.com`, `http://media.s-nowk.com`, `http://blog.s-nowk.com`, `http://www.s-nowk.com`은 모두 `https://...`로 리다이렉트된다.
- host listen 기준 내부 포트는 아래처럼 loopback만 허용한다.
  - `127.0.0.1:8080 -> nginx`
  - `127.0.0.1:8082 -> media-api`
  - `127.0.0.1:3331 -> media mysql`
  - `127.0.0.1:9100/9101 -> minio api/console`
- 최소 백업 경로는 아래처럼 정리돼 있다.
  - `infra/scripts/backup-blog-db.sh -> infra/backups/blog-db`
  - `infra/scripts/backup-media-storage.sh -> infra/backups/media-storage`
- 최근 실행 기준으로 blog DB dump와 media storage archive는 모두 checksum 검증이 통과했다.
- 외부 E2E 기준 `login -> upload -> complete -> create draft -> publish -> public post -> public list`가 모두 성공했다.
- presigned upload URL은 더 이상 내부 `minio` host가 아니라 `https://media.s-nowk.com/media-assets/*` 기준으로 응답한다.
- E2E 종료 후 임시 관리자 계정과 임시 공개 글은 DB에 남지 않는다.

현재 확정 범위
- 현재 범위는 `P1-001-AUTH-1`, 즉 일반 사용자 인증 모델과 refresh/cookie 기준을 정리하는 것이다.
- `LE`나 `Cloudflare Origin` 기반 direct origin HTTPS는 현재 공인 IP 변동성 때문에 1차 범위에서 제외하고, Tunnel edge HTTPS를 유지하는 방향으로 계속 간다.

세부 단계
- [x] OPS-01 공개 포트와 내부 포트 점검
  - [x] OPS-01-1 외부 공개 진입 포트와 Cloudflare/Tunnel 경로 정리
  - [x] OPS-01-2 DB/내부 서비스 포트가 외부에 직접 노출되지 않는지 확인
  - [x] OPS-01-3 운영 기준 문서 반영
- [x] OPS-02 최소 백업 기준 확인
  - [x] OPS-02-1 blog DB dump 경로와 media 볼륨 백업 기준 정리
  - [x] OPS-02-2 수동 복구 가능 기준 확인
  - [x] OPS-02-3 다음 시작 지점을 `P0-031-E2E-1`로 전환
- [x] E2E-01 외부 환경 작성/발행/업로드/공개 검증
  - [x] E2E-01-1 관리자 로그인과 작성 경로 검증
  - [x] E2E-01-2 이미지 업로드와 발행 경로 검증
  - [x] E2E-01-3 공개 화면 반영과 정리 기준 확인

계획 메모
- 현재 운영은 direct origin 노출이 아니라 Cloudflare Tunnel edge가 공개 진입점이다.
- 공인 IP가 계속 바뀔 수 있으므로 `A 레코드 + 고정 origin` 방향은 1차 운영 기준으로 맞지 않는다.
- 따라서 `P0-027`과 `P0-028`은 `CNAME to Tunnel + Proxy`와 edge HTTPS 강제 기준으로 정리하는 편이 현재 운영 방식과 맞다.
- `/media` 공개 경로는 대표 도메인 아래에 유지하되, media admin/public API origin은 `media.s-nowk.com/api/*`로 분리해도 괜찮다.
- `P0-031`은 실제 브라우저 대신 cookie jar와 HTTP 호출 기준으로 관리자 로그인, 글 작성, 발행, 업로드, 공개 반영을 확인해도 된다.

다음 시작 지점
- `AUTH-01-1`
- 다음 구현은 일반 사용자 회원가입/로그인 모델과 refresh/cookie 기준을 먼저 정리하는 것이다.

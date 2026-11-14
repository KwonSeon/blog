# Blog development architecture

The web uses Next.js App Router, React 19 and actual Astryx neutral-theme components (Button, TextInput, TextArea, Selector, Theme and LinkProvider). Existing public routes and semantic article markup remain. Tailwind handles layout; Astryx controls interactive component sizes, states and typography. The official CSS layer order and token bridge are imported in `app/globals.css`.

Node 24 LTS and Java 25 LTS are the build/runtime baseline. Next/React/Spring Boot use the current stable versions recorded in the lockfile/build.gradle. TypeScript 6 and ESLint 9 are retained for compatibility with the current Next ESLint plugins (TS 7 and ESLint 10 were checked and were incompatible). Astryx currently publishes beta releases; package versions are pinned.

## Shared accounts

`/api/auth/sso/start` and `/api/auth/sso/callback` use openid-client discovery, authorization code + PKCE, state, nonce and ID-token signature validation. Access/refresh tokens stay in HttpOnly cookies. Requests to protected BFF routes first check the central `/api/auth/me` session and then blog `/api/admin/session` authorization. Global logout revokes the central account sessions; ordinary logout revokes the app refresh token and clears local cookies. A still-active central browser session can immediately sign back in after ordinary logout.

New accounts can request and confirm email verification at `/register`; account creation is delegated to Identity. Registration tokens issued by the legacy Identity endpoint are never returned to the browser. Google/Kakao availability depends on the central login page's provider configuration; real provider-account verification is separate.

The API validates Identity issuer, JWKS and the `blog-api` audience. `BLOG_IDENTITY_EDITOR_BINDINGS` explicitly maps central subjects to existing local author IDs (`subject:authorId`, comma-separated). An unmapped central account gets no editor permissions, even if its token claims an ADMIN role. Do not link by email or auto-grant roles. Existing local author rows must exist. With shared Identity configured, local password login is disabled.

## Shared Media

Browser -> blog BFF -> Media presign -> browser PUT -> blog completion -> attach media ID. Presign accepts only the four blog namespaces and raster images up to 20MB. A signed/encrypted expiring HttpOnly upload-intent cookie binds subject, asset ID, object key and original filename before completion. Asset metadata/deletion checks the current subject and blog namespace. 64-bit IDs are preserved before JSON parsing can round them.

Media objects are public by URL; private blog visibility does not make uploaded Media objects private. These BFF checks do not retrofit the shared Media service's direct endpoints for all other clients. Shared-service authorization hardening remains a separate cross-project task.

## CI and development delivery

`.github/workflows/ci.yml` runs web lint/tests/build/production dependency audit and API tests/bootJar on GitHub-hosted runners for main, dev, feature pushes and pull requests. Actions are pinned by commit.

Mac mini deployment is controlled outside the public repository. The local deployment agent checks the exact dev head and a successful `Blog CI` push run, builds images tagged with that SHA, waits for development container health and records the deployed SHA. Failed container health rolls back application images to the previous successful SHA. GitHub has no production deployment job or self-hosted runner exposed to public pull requests. A separate local main deployment controller is described below.

Development URL: https://blog-dev.s-nowk.com. Runtime secrets, Compose, local deployment agent and DB remain outside Git. Development web/API bind only loopback; DB has no host port. Cloudflare restricts the development hostname to KR. Actual foreign-origin probing has not been performed.

Schema changes require explicit migration/backups; image rollback does not roll back database schema. Initial isolated DB is created from the existing V1 schema. The agent does not automatically apply future migrations.

## Checks

```sh
cd apps/web
npm ci
npm run lint
npm test
npm run build
npm audit --omit=dev
cd ../api
./gradlew test bootJar --no-daemon
```

Do not put real environment values or QA account credentials in this document. Production promotion remains a separate user request after development stabilization.

## Development verification (2026-11-14)

- API: 148 tests passed. Web: three authentication/ID regression tests, lint and production build passed.
- Browser: synthetic email verification and registration, central SSO callback, editor access, normal-member denial, global logout and subsequent 401 were exercised.
- Shared Media: real synthetic PNG presign/PUT/completion succeeded; the returned decimal ID exactly matched the object key. Post creation, publication and editing were exercised.
- Public detail parsing also preserves 64-bit IDs; ordinary JSON number parsing can otherwise break cover-image URLs even after a successful upload.
- Development CI/CD deployed the exact successful dev SHA. Production main and production services were not promoted.
- Real Google/Kakao provider accounts and a request originating outside Korea were not tested.
- The owner's editor binding requires an existing verified development Identity account. Do not create credentials or auto-grant editor access based on an unverified email.

## Publishing and data-driven projects

Projects are database records, not a compiled catalogue. `/admin/projects` manages names, slugs, summaries, service/repository links, visibility and activity. Adding or editing a project does not require rebuilding the web application. Repository links do not clone code or grant repository access. A private source repository may remain unlinked while the service's introduction is public.

Project stories store long-form Markdown and shared Media image references in `project_stories`. Apply `infra/compose/migrations/blog/V2__project_stories.sql` before starting this API revision. Development DB backup and migration were performed separately; production remains unchanged. Both the public project and story endpoints require PUBLIC + ACTIVE. Images remain public URLs: upload only screenshots approved for public viewing, even while a project is private.

The post editor offers formatting controls, an unsaved-change indicator and browser unload protection. Preview and public rendering share react-markdown with GFM support; raw HTML is skipped. Save supports publishing and returning an existing post to a draft.

## Administration and layout settings

`/admin` is the management dashboard; `/admin/posts`, `/admin/projects` and `/admin/settings` share a responsive navigation shell. Only mapped editors can read management data or save settings.

`blog_settings` stores the displayed blog name, home title/description, optional GitHub link, section order/visibility, grid/list post layout and standard/wide content width. Apply `V3__blog_settings.sql` before this API revision. The development DB was backed up before initialization. No production migration has run.

Layout editing is a local draft until Save. The preview iframe renders the same components at its own desktop/mobile viewport with public content; it is authenticated, noindex and noninteractive. Preview messages are accepted only from the same-origin parent. Public pages read saved settings without caching, so saving needs no rebuild. At least one home section must remain visible. A locked singleton row and revision check reject stale saves (409) instead of overwriting a newer configuration.

## Production release automation (2026-11-14)

A separate Mac mini controller checks main every 180 seconds and requires a successful Blog CI push run for the exact main SHA. The existing production revision is registered as its baseline; installing the controller does not promote dev or replace production containers. Main promotion remains a separate user-requested action.

The controller builds immutable SHA-tagged web/API images, starts a separate release pair with unique names and a distinct Snowflake worker, and checks API health, database-backed public lists and rendered pages. It validates the candidate Nginx configuration before gracefully reloading the existing edge. Web and API upstreams change together. The web calls its own release API. Next.js auth routes reach the web; missing static assets fall back to the previous web release.

After switching, the controller verifies the selected release header and public routes. Failed checks restore the previous route and block retrying that SHA. Old containers remain running for in-flight requests, previous static assets and rollback; cleanup and worker allocation are explicit maintenance tasks. Rollback changes routing, never database state. Interrupted transactions leave a recovery journal and prevent further automatic changes.

A migration fingerprint must match the reviewed production baseline. The controller does not run migrations. The current development V2/V3 schemas and shared-Auth production configuration need separate review before the first promotion. Database changes must remain compatible with both releases during the overlap. An arbitrary incompatible schema or authentication change cannot be made interruption-free by traffic switching alone.

Runtime scripts, configuration, environment values, deployment state and recovery files remain outside Git in the private Mac mini production deployment directory. The local controller supports a read-only check and an explicit rollback command. It runs in the logged-in user's LaunchAgent session and requires Docker to be available.

Verification: six deployment safety tests passed. An isolated loopback Nginx using the current production web/API images completed route switching and rollback while 195 monitored HTTP requests had zero failures. The actual production edge configuration and application containers were not replaced. This rehearsal verifies the routing mechanism, not a live promotion of the development release or every existing browser session.

## Content likes (P1-003 through P1-005)

Apply V4__content_likes.sql before this release. Separate post/project tables use a unique content/Identity-subject key, subject lookup index and cascading parent FK. Likes belong to central subjects, never local author IDs. Only PUBLIC + PUBLISHED posts and PUBLIC + ACTIVE projects expose counts or accept writes. Anonymous callers receive counts without member identities; ordinary authenticated members may like without editor rights.

Desired-state PUT is idempotent. A parent-row lock serializes writes and preserves the existing aggregate count; content updated_at does not change for reactions. Signed merged_subjects allow reading a previous account's like and collapse its duplicates onto the canonical subject when that member next interacts. Historical duplicates are not globally rewritten at account-merge time.

The browser uses /api/community/reactions through a no-store, same-origin BFF. Production edge must route /api/community/ to the web; direct Spring mutation endpoints require Bearer authentication. Login return paths are restricted to local post/project detail paths and carried inside the signed SSO transaction. Astryx ToggleButton exposes pressed/loading states; failed requests retain the last confirmed state.


## Content views (P1-006/007/008)

- Public, published posts and active projects expose counts. GET never increments. A visible detail page sends POST after two seconds; failed metrics do not block content.
- The same browser/content is counted once per UTC day. An HttpOnly, SameSite cookie expires at UTC midnight. Only a daily HMAC hash reaches SQL, never an IP, raw cookie or account ID. DNT=1 / Sec-GPC=1 requests read counts without recording or setting the visitor cookie.
- BFF enforces same-origin writes and signs kind, exact decimal ID, daily visitor hash and timestamp with `BLOG_VIEW_SIGNING_SECRET` (separate random secret per environment, >=32 characters, provided to API and web outside Git). API verifies HMAC and a 120-second window; missing configuration fails closed. Direct unsigned API writes are rejected.
- V5 adds independent post/project deduplication tables. Parent row locks serialize duplicates and deletion/publication races, lifetime `view_count` increments atomically and `updated_at` stays unchanged. Hourly retention deletes keys older than yesterday; lifetime totals persist. Parent deletion cascades keys.
- Counts are approximate browser/day engagement, not authenticated unique people. Cookie resets, multiple devices and deliberate automated requests through the BFF can inflate them; this is not an anti-fraud metric. Concurrent first visits before a new cookie is received can also use distinct identifiers.
- Apply the additive V5 migration after a DB backup before deployment, then approve the production schema fingerprint. Never log or commit runtime signing keys.


## Comments (P1-009 through P1-013)

- V6 stores parent IDs and depth without a product depth cap. Public reads use indexed 50-row keyset pages, not recursive/N+1 queries. IDs and cursors are decimal strings. The web builds the loaded tree iteratively; visual indentation stops at three levels while reply links retain ancestry.
- Writes require shared Identity. Only owners (including signed merged subjects) edit; owners and explicitly bound local editors may soft-delete. Public responses omit Identity subjects and emails. Deleted bodies/names are erased and replies remain. Parent FK uses SET NULL to avoid recursive delete chains; deleting the content cascades its comments directly. MySQL's cascade depth restriction is documented at https://dev.mysql.com/doc/refman/8.4/en/ansi-diff-foreign-keys.html.
- Each member's write budget is serialized in SQL: 3-second spacing, 10 writes/minute, 100/day. Duplicate text within five minutes is rejected. Create UUIDs make network retries idempotent; reusing a key for different content conflicts. Limits are per canonical account, not a global spam/identity-abuse guarantee. Editing cannot reassign ownership or parent IDs.
- Text is plain, bounded to 2,000 UTF-16 units and rendered through React escaping. BFF accepts only same-origin writes, retains HttpOnly credentials, bounds input and forwards Retry-After on throttling. Astryx TextArea/Button handle input, loading and focus; failures preserve drafts.
- Validation covers concurrent retries, ownership/merge aliases, moderator binding, private targets, cross-content replies, budgets, large IDs, pagination, soft deletion, content cascade and a 20,000-item client tree.


## Public pages and advertising readiness

`/about`, `/contact`, `/editorial`, `/privacy` and `/subscribe` are linked in the public footer. `/admin/monetization` requires the explicit editor binding and reports configuration readiness; it does not query or assert Google account approval.

Advertising is disabled by default and always disabled on development/localhost. Private web runtime settings are `BLOG_ADSENSE_CLIENT`, `BLOG_ADSENSE_ARTICLE_SLOT`, `BLOG_ADSENSE_SITE_READY`, `BLOG_ADSENSE_CMP_READY`, and `BLOG_ADSENSE_ENABLED`. All gates must pass before a manually placed article ad loads. A valid production publisher ID exposes the verification metadata and `/ads.txt` independently of ad delivery. No publisher IDs or credentials are hardcoded into source.

Before activation, verify the actual site is ready in AdSense, create the intended display ad unit, and publish/test the Google-certified consent message in Privacy & messaging. Keep Auto ads off when only the explicit article placement is desired. The privacy page offers Google's consent-revocation flow while advertising is enabled. Do not use synthetic IDs against the real ad network or click live ads during QA. Development QA verifies that no ad script or slot is rendered.

External approval and authentic published content remain operational prerequisites, not results of adding these pages. References: [site readiness](https://support.google.com/adsense/answer/7299563), [display ad units](https://support.google.com/adsense/answer/9274025), [Google consent messages](https://support.google.com/adsense/answer/10960768), [consent revocation](https://support.google.com/adsense/answer/10959060).


## Operations monitoring

`python3 tools/monitor.py --config /private/path/monitor.json` performs read-only, bounded probes. Configuration contains an `output` directory, one to twelve named `endpoints` (`url`, optional `headers`, `status`, `json_contains`), optional `containers`, `memory_container`, Docker binary path and deployment `release_file`. Do not put live configuration in Git. Runtime container names follow the release journal automatically.

The report covers public/origin HTTP responses, expected health JSON, redirects, container liveness/health/OOM/restart changes, Docker VM available memory and backup-disk free space. Requests run in four bounded workers; no service restart or deletion occurs. `status.json` is atomic, `history.jsonl` rotates at 10 MiB and local transition events at 1 MiB. Files are mode600 in a mode700 directory. Bodies, request headers and exception messages are never persisted. A nonzero exit means a failed check/configuration. Check the report timestamp and launchd exit status as well as the last health value; stale reports are not proof of health.

The private Mac mini launch agent runs every five minutes. It records local health and incident/recovery transitions; external notification channels are not configured or sent by this task.

The existing `npm test` discovery runs `tests/operations.test.mjs`, which executes the repository Python standard-library regression suite. This requires Python3 for local web test runs and uses the existing CI runner toolchain; no additional workflow or package dependency is required.

## Backup operations

See [backup and isolated restore](../infra/BACKUP.md). Runtime schedules, configuration, dumps and restore results stay outside Git. Operations tests cover corruption, traversal, failed-backup retention and insufficient-memory guards.

## Public rendering performance

Public parsed API reads and detail view models use request-scoped React cache, so metadata and content share work without keeping newly private/unpublished data across requests. Public API waits are capped at8s; site settings at10s. Post listing requests settings/content concurrently. Markdown images use native lazy loading/async decoding; arbitrary Markdown images still lack intrinsic dimensions, so this does not guarantee zero layout shift. Comment initial reads time out at15s and offer retry; cancellation on unmount remains separate. Astryx provides the keyboard skip link. Loading pulse and comment focus scrolling respect reduced motion.

Individual browser timings are diagnostic samples, not field Core Web Vitals or a measured percentage speedup. Reference: [Next data fetching](https://nextjs.org/docs/app/getting-started/fetching-data).

Route loading files provide Astryx Skeleton layouts for home, posts, post detail, projects and project detail. They follow administrator list/grid and home-section settings through the public layout; arbitrary content lengths/images cannot guarantee zero layout shift. Decorative shapes are hidden from assistive technology, one status label announces loading, and reduced-motion disables animation. No artificial delay is present in the application; QA delays ran only in a temporary loopback proxy.

## Member inbox

V9 adds in-app comment/reply notifications with recipient+comment uniqueness, inbox/unread/retention indexes and comment-delete cascade. A synchronous Spring domain subscriber participates in the comment transaction (MANDATORY): delivery failure rolls back the comment, and replay does not duplicate the inbox item. There is no external delivery or historical backfill in this step.

Replies notify the undeleted parent comment's signed Identity subject. Post comments notify subjects explicitly bound to that local author; projects have no individual owner column and notify bound editors. Canonical and signed merged actor subjects suppress self notifications; incoming recipient IDs are never accepted. Lists/read operations are recipient scoped including signed aliases, dynamically filter public published/active content and undeleted comments, expose no account IDs or copied comment bodies, and use private/no-store responses. IDs/cursors are strings.

Lists use descending keyset pages of50. Read-all is bounded by the page snapshot ID so later arrivals stay unread; the response returns the current unread count. The inbox supports refresh/retry/read/read-all/more and Astryx skeletons. Notifications older than90days are hidden immediately and deleted in hourly batches of1000. API and browser synthetic-account checks precede promotion.

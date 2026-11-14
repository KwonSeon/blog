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

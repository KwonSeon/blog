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

Mac mini deployment is controlled outside the public repository. The local deployment agent checks the exact dev head and a successful `Blog CI` push run, builds images tagged with that SHA, waits for development container health and records the deployed SHA. Failed container health rolls back application images to the previous successful SHA. There are no automatic production jobs and no self-hosted runner exposed to public pull requests.

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

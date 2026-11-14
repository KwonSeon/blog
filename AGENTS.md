# Blog development

- Develop on feature branches and dev. Do not merge to main or replace production without a separate user request.
- Reuse the Mac mini shared development Identity and Media. Keep production services and DBs separate.
- Runtime config, credentials, test accounts, deployment controller and backups live outside Git under `/Users/kwonseon/deployments/blog-dev`.
- Use actual Astryx components and neutral theme. Read installed component types/docs before changing API usage. Preserve keyboard, labels, disabled and loading semantics.
- Central Identity subject is not a local author ID or administrator role. Only explicit server-side bindings grant blog editor rights.
- Keep 64-bit resource IDs as decimal strings at browser boundaries.
- Run web lint/test/build and API Gradle tests for relevant changes. Verify SSO and Media using synthetic development accounts only.

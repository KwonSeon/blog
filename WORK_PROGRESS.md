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
- `P0-022-SEO-1 메타/OG 적용(글/프로젝트)`

최근 완료 작업
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
- 공개 홈, 프로젝트 목록/상세, 글 목록/상세는 모두 `metadata` 또는 `generateMetadata` 기반 기본 title/description/canonical 구조를 이미 갖고 있다.
- 따라서 다음 SEO 단계는 새 route를 만드는 작업이 아니라, 기존 공개 route의 metadata를 richer OG 기준으로 보강하는 작업에 가깝다.
- `apps/web/src/shared/config/site.ts`에는 site URL과 기본 SEO keyword/locale을 둘 수 있는 설정 지점이 있다.
- 현재 공개 mock data에는 `coverMediaAssetId`가 포함돼 있지 않아, 이번 단계의 OG는 우선 title/description/text fallback 중심으로 정리하는 편이 맞다.

현재 확정 범위
- blog 저장소의 다음 범위는 공개 글/프로젝트 metadata와 Open Graph 표현 정리로 넘어간다.
- 이번 단계의 목표는 기존 공개 route에 이미 있는 `metadata`, `generateMetadata`, canonical 구성을 SEO/OG 기준으로 보강하는 것이다.
- 우선순위는 홈보다 글 상세와 프로젝트 상세가 높고, 그다음 목록 route의 description/canonical/robots 표현을 정리하는 편이 맞다.
- media가 별도 서비스로 분리돼 있으므로, OG image는 현재 있는 `coverMediaAssetId`나 media public URL builder를 활용할 수 있는지 먼저 판단해야 한다.
- 공개 route는 이미 SSR 구조이므로 이번 단계는 렌더링 구조보다 metadata composition과 fallback 전략을 먼저 고정하는 것이 맞다.
- 프로젝트 상세 OG type은 서비스 랜딩 성격상 `website`, 글 상세 OG type은 `article`로 나누는 편이 적절하다.

세부 단계
- [ ] FE-SEO-01 SEO/OG 기준 정리
  - [x] FE-SEO-01-1 README 기준 글/프로젝트 메타와 OG 목표 다시 확인
  - [x] FE-SEO-01-2 현재 공개 route metadata와 cover image 사용 가능 범위 확인
  - [x] FE-SEO-01-3 title/description/canonical/robots/openGraph fallback 기준 정리
- [ ] FE-SEO-02 프로젝트 route metadata 보강
  - [x] FE-SEO-02-1 `/projects` 목록 metadata 설명과 canonical 보강
  - [x] FE-SEO-02-2 `/projects/[slug]` 상세 generateMetadata에 OG 필드 보강
  - [x] FE-SEO-02-3 프로젝트 상세 fallback title/description/image 규칙 정리
- [ ] FE-SEO-03 글 route metadata 보강
  - [x] FE-SEO-03-1 `/posts` 목록 metadata 설명과 query-param 대응 기준 정리
  - [x] FE-SEO-03-2 `/posts/[slug]` 상세 generateMetadata에 article/OG 필드 보강
  - [x] FE-SEO-03-3 글 상세 fallback title/description/image 규칙 정리
- [ ] FE-SEO-04 검증 및 문서 반영
  - [ ] FE-SEO-04-1 route별 metadata/link 구조 점검
  - [ ] FE-SEO-04-2 `blog/apps/web`에서 `npm run lint`, `npm run build` 확인
  - [ ] FE-SEO-04-3 README, WORK_PROGRESS 완료 상태 반영

계획 메모
- SEO 단계는 새 화면 구현보다 existing route metadata를 덮어쓰지 않고 보강하는 작업이라, 현재 metadata 구조와 fallback부터 먼저 고정하는 편이 안전하다.
- 프로젝트와 글 상세는 현재 mock 데이터에 `coverMediaAssetId`가 없어도 description/title 기반 fallback OG를 제공해야 한다.
- query-param이 붙는 목록 route는 canonical/robots 기준을 같이 정리하지 않으면 중복 색인이 생길 수 있어 이 부분을 먼저 확인해야 한다.

다음 시작 지점
- `FE-SEO-04-1`
- 다음 구현은 route별 metadata 구조 검증과 README/WORK_PROGRESS 완료 반영이다.

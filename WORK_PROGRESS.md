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
- `P0-021-FE-ADM-3 이미지 업로드(presign) + 본문 삽입`

최근 완료 작업
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
- `apps/web/src/widgets/admin-login-form`, `admin-login-shell`, `admin-session-guard`, `admin-post-editor`, `admin-post-editor-shell`이 나뉘어 있어 로그인, 보호 라우트, editor 책임이 분리되어 있다.
- `apps/web/src/shared/ui`에는 `FormField`, `FormMessage`, `FormSelect`, `FormTextarea`가 있어 관리자 입력 화면 표현을 공통화할 수 있다.
- 백엔드에는 `POST /api/admin/auth/login`, `POST /api/admin/posts`, `PUT /api/admin/posts/{postId}`, `PATCH /api/admin/posts/{postId}/status`, `GET /api/admin/posts`, `GET /api/admin/posts/{postId}`가 이미 있다.
- 글 작성 request에는 `slug`, `title`, `excerpt`, `contentMd`, `visibility`, `status`, `lang`, `coverMediaAssetId`가 들어가고, 수정 request에는 `status`가 빠진다.
- 상태 변경은 별도 `PATCH /api/admin/posts/{postId}/status`로 처리해야 하므로, create/update와 publish 전환을 프론트에서 분리해서 다뤄야 한다.
- `visibility`는 `PUBLIC | PRIVATE`, `status`는 `DRAFT | PUBLISHED`만 사용한다.
- `coverMediaAssetId`는 `@Positive`만 붙어 있어 현재 단계에서는 비워둘 수 있다.
- `README`에는 presign 업로드/다운로드 규약을 media 서비스가 소유하고 blog는 API 계약 기준으로만 연동한다고 명시돼 있다.
- 현재 blog 웹에는 아직 media 업로드 버튼, presign 요청 helper, 본문 이미지 삽입 UI가 없다.
- `posts`, `projects`에는 `cover_media_asset_id` 컬럼이 이미 있다.
- blog 저장소는 더 이상 `media_db`나 media compose를 소유하지 않고, media 관련 source of truth는 별도 `s-nowk/media` 저장소다.
- media 현재 관리자 업로드 계약은 `POST /api/admin/media/uploads/presign`과 `POST /api/admin/media/uploads/complete` 두 단계로 고정돼 있다.
- presign 요청 필드는 `namespace`, `originalFilename`, `contentType`, `sizeBytes`이고 응답은 `mediaAssetId`, `bucket`, `objectKey`, `uploadUrl`, `httpMethod`, `uploadStatus`, `expiresAt`를 준다.
- complete 요청 필드는 `mediaAssetId`, `objectKey`, `originalFilename`이고 응답에서 최종 `mediaAssetId`, `contentType`, `sizeBytes`, `mediaType`, `uploadStatus`를 다시 확인할 수 있다.
- 현재 허용 namespace는 `post-cover`, `project-cover`, `post-inline`, `project-inline`이고, 이번 단계에서는 글 작성 화면 기준으로 `post-cover`, `post-inline`만 쓰면 된다.
- media `README` 기준으로 blog 프론트는 media API를 직접 호출하고, presign URL로 object storage에 `PUT` 업로드한 뒤 complete를 다시 호출하는 흐름을 따른다.

현재 확정 범위
- blog 저장소의 다음 범위는 관리자 이미지 업로드와 본문 삽입 구현으로 넘어간다.
- 이번 단계의 목표는 media 서비스의 presign 업로드 계약을 호출하고, 업로드된 asset을 `coverMediaAssetId`나 markdown 본문 이미지 링크로 연결하는 것이다.
- blog 저장소는 media 자체를 소유하지 않으므로, 프론트는 presign 요청/업로드/삽입 UI와 blog API payload 연결에 집중하는 편이 맞다.
- 현재 에디터는 `coverMediaAssetId`를 수동 입력으로만 다루므로, 다음 단계에서는 업로드 결과를 이 필드와 본문 삽입 액션으로 연결해야 한다.
- 로그인 단계에서 만든 `localStorage + Authorization header` 기준은 그대로 재사용하고, media 계약도 별도 helper로 분리해두는 편이 맞다.
- 에디터 본문에는 markdown 이미지 문법 삽입, cover image 선택, 업로드 진행 상태, 실패 재시도 UX가 같이 필요하다.
- 커버 이미지 업로드 성공 시 complete 응답의 `mediaAssetId`를 `coverMediaAssetId`에 바로 반영하고, 본문 이미지는 `![alt](media public content url)` 문법으로 삽입하는 편이 맞다.
- 본문 이미지 URL은 media 공개 content endpoint 기준 `GET /api/public/media/assets/{mediaAssetId}/content`를 사용하면 raw storage URL을 직접 노출하지 않아도 된다.
- 업로드 상태는 `idle -> presigning -> uploading -> completing -> success | error`처럼 editor 본문 상태와 분리해서 관리하는 편이 안전하다.

세부 단계
- [ ] FE-UPLOAD-01 이미지 업로드 요구사항/정보구조 정리
  - [x] FE-UPLOAD-01-1 README 기준 presign 업로드와 본문 삽입 목표 다시 확인
  - [x] FE-UPLOAD-01-2 media 서비스 계약과 blog `coverMediaAssetId` 연결 기준 정리
  - [x] FE-UPLOAD-01-3 에디터 본문/커버 이미지 삽입 위치와 UX 기준 정리
- [ ] FE-UPLOAD-02 업로드 UI shell과 helper 구성
  - [x] FE-UPLOAD-02-1 presign 요청 helper와 업로드 상태 타입 추가
  - [x] FE-UPLOAD-02-2 cover image 선택/업로드 shell 배치
  - [x] FE-UPLOAD-02-3 본문 이미지 삽입 액션과 helper text 배치
- [ ] FE-UPLOAD-03 presign 업로드와 본문 삽입 흐름 구현
  - [ ] FE-UPLOAD-03-1 presign 요청과 실제 업로드 흐름 연결
  - [ ] FE-UPLOAD-03-2 업로드 성공 시 `coverMediaAssetId`와 markdown 이미지 링크 반영
  - [ ] FE-UPLOAD-03-3 업로드 오류/재시도/진행 상태 UX 정리
- [ ] FE-UPLOAD-04 공통 표현 정리 및 검증
  - [ ] FE-UPLOAD-04-1 업로드 전용 표현과 editor/shared 재사용 경계 정리
  - [ ] FE-UPLOAD-04-2 보호 라우트/metadata/link 구조 확인
  - [ ] FE-UPLOAD-04-3 `blog/apps/web`에서 `npm run lint`, `npm run build` 확인

계획 메모
- 이미지 업로드는 media 서비스 contract를 따르는 별도 흐름이므로 blog 프론트에서는 helper 분리와 진행 상태 관리가 먼저 흔들리지 않게 잡혀야 한다.
- 본문 삽입은 markdown 텍스트 조작과 cover image 선택이 같이 얽히므로 editor state와 업로드 state를 분리하는 편이 맞다.
- 업로드 성공 결과는 `coverMediaAssetId`와 markdown 이미지 링크 두 방향으로 나뉘어 반영될 수 있어야 한다.

다음 시작 지점
- `FE-UPLOAD-03-1`
- 다음 구현은 presign 요청과 실제 업로드 흐름을 editor shell에 연결하는 것이다.

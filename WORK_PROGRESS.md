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
- `P0-020-FE-ADM-2 마크다운 에디터(작성/미리보기/발행)`

최근 완료 작업
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
- `apps/web`에는 `/admin/login` 공개 route와 `/admin` 보호 placeholder route가 추가됐고, 관리자 인증 기본 흐름이 한 번 완성됐다.
- `apps/web/src/shared/lib/auth`에는 `requestAdminLogin`, `admin-session` helper가 있고 세션은 `localStorage` 기준으로 관리된다.
- `apps/web/src/widgets/admin-login-form`, `admin-login-shell`, `admin-session-guard`가 나뉘어 있어 로그인 form, shell, 보호 라우트 책임이 분리되어 있다.
- `apps/web/src/shared/ui`에는 로그인 화면에서 재사용 가능한 `FormField`, `FormMessage`가 추가됐다.
- 백엔드에는 `POST /api/admin/auth/login`, `POST /api/admin/posts`, `PUT /api/admin/posts/{postId}`, `PATCH /api/admin/posts/{postId}/status`, `GET /api/admin/posts`, `GET /api/admin/posts/{postId}`가 이미 있다.
- 글 작성 request에는 `slug`, `title`, `excerpt`, `contentMd`, `visibility`, `status`, `lang`, `coverMediaAssetId`가 들어가고, 수정 request에는 `status`가 빠진다.
- 상태 변경은 별도 `PATCH /api/admin/posts/{postId}/status`로 처리해야 하므로, create/update와 publish 전환을 프론트에서 분리해서 다뤄야 한다.
- `visibility`는 `PUBLIC | PRIVATE`, `status`는 `DRAFT | PUBLISHED`만 사용한다.
- `coverMediaAssetId`는 `@Positive`만 붙어 있어 현재 단계에서는 비워둘 수 있다.
- `apps/web/src/shared/config/site.ts`의 메인 내비게이션은 여전히 공개 화면 `/projects`, `/posts` 기준이다.
- `posts`, `projects`에는 `cover_media_asset_id` 컬럼이 이미 있다.
- blog 저장소는 더 이상 `media_db`나 media compose를 소유하지 않고, media 관련 source of truth는 별도 `s-nowk/media` 저장소다.

현재 확정 범위
- blog 저장소의 다음 범위는 관리자 글 작성 화면 구현으로 넘어간다.
- 이번 단계의 목표는 `/admin` 인증 흐름 위에서 새 글 작성 route와 마크다운 에디터 shell, 미리보기, 발행 동작을 구현하는 것이다.
- 백엔드 관리자 글 API 계약은 이미 있으므로, 프론트는 form state, markdown 편집/preview, visibility/status 선택, 발행 흐름을 먼저 정리하는 편이 맞다.
- P0 범위에서는 이미지 업로드 없이 텍스트 기반 markdown 작성과 발행까지 먼저 닫고, 이미지 presign 연동은 `P0-021-FE-ADM-3`에서 이어간다.
- 로그인 단계에서 만든 `localStorage + Authorization header` 기준은 그대로 재사용하고, 후속 관리자 글 API 호출 helper로 이어지게 잡는 편이 맞다.
- 이번 단계의 주 route는 `/admin/posts/new`로 두고, 이후 수정 화면은 같은 editor 표현을 재사용하는 `/admin/posts/{postId}` 또는 `/admin/posts/{postId}/edit`로 확장 가능하게 잡는 편이 맞다.
- 최초 저장은 `POST /api/admin/posts`, 이후 본문 수정은 `PUT /api/admin/posts/{postId}`, 발행 전환은 `PATCH /api/admin/posts/{postId}/status`를 조합하는 흐름이 현재 API 계약과 맞는다.

세부 단계
- [x] FE-EDITOR-01 관리자 글 작성 화면 요구사항/정보구조 정리
  - [x] FE-EDITOR-01-1 README 기준 관리자 에디터 화면 목표 다시 확인
  - [x] FE-EDITOR-01-2 `/api/admin/posts` create/update/status/list/get 계약과 필수 필드 정리
  - [x] FE-EDITOR-01-3 로그인 세션 재사용과 editor route 구조 기준 정리
- [x] FE-EDITOR-02 관리자 글 작성 route와 editor shell 조립
  - [x] FE-EDITOR-02-1 `app/admin/posts/new/page.tsx` route와 metadata 베이스 추가
  - [x] FE-EDITOR-02-2 slug/title/excerpt/contentMd/visibility/status/lang 입력 shell 구성
  - [x] FE-EDITOR-02-3 preview pane, helper text, submit CTA 배치
- [ ] FE-EDITOR-03 작성/미리보기/발행 흐름 구현
  - [ ] FE-EDITOR-03-1 editor form state와 markdown preview 연결
  - [ ] FE-EDITOR-03-2 create post submit과 성공 응답 처리 연결
  - [ ] FE-EDITOR-03-3 publish/status 변경 흐름과 오류/재시도 UX 정리
- [ ] FE-EDITOR-04 공통 표현 정리 및 검증
  - [ ] FE-EDITOR-04-1 editor 전용 표현과 shared/ui 재사용 경계 정리
  - [ ] FE-EDITOR-04-2 보호 라우트/metadata/link 구조 확인
  - [ ] FE-EDITOR-04-3 `blog/apps/web`에서 `npm run lint`, `npm run build` 확인

계획 메모
- 관리자 에디터는 로그인 성공 이후 바로 이어지는 작업 화면이므로 `/admin` 보호 흐름을 그대로 재사용해야 한다.
- 이미지 업로드는 다음 단계로 미루고, 이번 단계에서는 markdown 본문 작성과 preview/publish 흐름 자체를 먼저 흔들리지 않게 잡는 편이 맞다.
- create/update/status API가 이미 있으므로, 프론트는 editor state와 request payload 매핑을 먼저 분명히 해야 한다.
- `POST -> PUT -> PATCH(status)` 흐름을 분리해두면 초안 저장과 발행 전환을 동시에 다루기 쉽다.

다음 시작 지점
- `FE-EDITOR-03-1`
- 다음 구현은 editor form state와 markdown preview, 저장/발행 submit 흐름을 연결하는 것이다.

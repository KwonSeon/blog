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
- `P0-019-FE-ADM-1 관리자 로그인 화면`

최근 완료 작업
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
- `apps/web` 공개 라우트에는 홈 `/`, 글 목록 `/posts`, 글 상세 `/posts/[slug]`, 프로젝트 목록 `/projects`, 프로젝트 상세 `/projects/[slug]`가 있고 공개 탐색 흐름이 한 번 완성됐다.
- `apps/web/src/entities/post`에는 목록용 `PostCard`, 상세용 `PostDetailHero`, `PostMarkdown`, `PostRelatedProjectCta`, 공통 `PostTagList`가 분리되어 있다.
- `apps/web/src/shared/lib/mock/home-data.ts`에는 목록용 `mockPosts`와 상세용 `mockPostDetails`, `getMockPostDetailBySlug`가 함께 있다.
- `apps/web/src/shared/config/site.ts`의 메인 내비게이션은 이미 `/projects`, `/posts` 링크를 모두 가리킨다.
- `apps/web`에는 아직 `/admin/login` route, 관리자 layout, auth storage helper, admin 보호 라우트가 없다.
- 백엔드에는 `POST /api/admin/auth/login`과 `/api/admin/**` 보호구역 설정이 이미 있다.
- `apps/api/src/main/resources/application.yaml`에는 `"/api/admin/auth/login:POST"`가 public request matcher로 등록돼 있어 로그인 요청 자체는 토큰 없이 호출할 수 있다.
- 관리자 로그인 request는 `username`, `password`이고 response는 `accessToken`, `expiresAt`이다.
- 관리자 후속 API 호출은 `Authorization: Bearer <accessToken>` 기준으로 붙는 구조로 보는 것이 맞다.
- 공개 글 API 상세 응답은 `contentMd`, `lang`, `coverMediaAssetId`, `publishedAt`까지 제공하므로 이후 상세 mock을 API view model로 바꾸는 경로가 열려 있다.
- `posts`, `projects`에는 `cover_media_asset_id` 컬럼이 이미 있다.
- blog 저장소는 더 이상 `media_db`나 media compose를 소유하지 않고, media 관련 source of truth는 별도 `s-nowk/media` 저장소다.

현재 확정 범위
- blog 저장소의 다음 범위는 관리자 로그인 화면 구현으로 넘어간다.
- 이번 단계의 목표는 `/admin/login` 공개 진입 화면과 로그인 form 제출 흐름을 먼저 구현하는 것이다.
- 백엔드 로그인 API 계약은 이미 있으므로, 프론트는 입력 검증, submit 상태, 성공 시 토큰 저장 방식, 보호구역 진입 흐름을 먼저 정리하는 편이 맞다.
- P0 범위에서는 일반 사용자 로그인 없이 관리자 전용 로그인만 고려하면 된다.
- P0에서는 별도 BFF나 httpOnly cookie 세팅 없이 `accessToken`을 `localStorage`에 저장하고, 이후 관리자 화면은 client guard로 token 존재 여부를 먼저 확인하는 쪽이 현재 구조에 가장 단순하다.
- `expiresAt`은 만료 토큰 정리와 재로그인 유도 기준으로 사용하고, 관리자 API 호출 helper는 이후 `Authorization` 헤더를 붙이는 방향으로 이어지게 잡는 편이 맞다.
- 로그인 완료 후 이어질 관리자 작성 화면은 `P0-020-FE-ADM-2`이므로, 라우트 구조와 auth 상태 보관 방식을 지금부터 일관되게 잡아야 한다.

세부 단계
- [x] FE-ADM-01 관리자 로그인 요구사항/정보구조 정리
  - [x] FE-ADM-01-1 README 기준 관리자 로그인 화면 목표 다시 확인
  - [x] FE-ADM-01-2 `/api/admin/auth/login` request/response와 보호구역 기준 정리
  - [x] FE-ADM-01-3 로그인 성공 후 토큰 저장/보호 라우트 진입 기준 정리
- [ ] FE-ADM-02 관리자 로그인 route와 form shell 조립
  - [ ] FE-ADM-02-1 `app/admin/login/page.tsx` route와 metadata 베이스 추가
  - [ ] FE-ADM-02-2 로그인 hero, form shell, helper text 조립
  - [ ] FE-ADM-02-3 validation/error/success message 표현 기준 정리
- [ ] FE-ADM-03 로그인 submit/auth state 흐름 구현
  - [ ] FE-ADM-03-1 로그인 form state와 submit pending 흐름 구현
  - [ ] FE-ADM-03-2 성공 시 token 저장과 후속 라우트 이동 기준 연결
  - [ ] FE-ADM-03-3 실패 응답과 재시도 UX 정리
- [ ] FE-ADM-04 공통 표현 정리 및 검증
  - [ ] FE-ADM-04-1 로그인 전용 표현과 shared/ui 재사용 경계 정리
  - [ ] FE-ADM-04-2 접근 제어/metadata/link 구조 확인
  - [ ] FE-ADM-04-3 `blog/apps/web`에서 `npm run lint`, `npm run build` 확인

계획 메모
- 관리자 로그인 화면은 공개 UI와 별도로 단정한 진입 화면을 가지되, 이후 작성 화면으로 자연스럽게 이어져야 한다.
- 토큰 저장 위치와 보호 라우트 진입 방식은 `P0-020-FE-ADM-2`까지 고려해 지금부터 일관되게 정해야 한다.
- 로그인 API는 이미 있으므로, 프론트는 입력/에러/토큰 저장 책임을 먼저 흔들리지 않게 잡는 편이 맞다.
- 초기 관리자 인증은 `localStorage + client-side guard + Authorization header` 조합으로 먼저 닫고, 보안 강화를 위한 cookie 전략은 후속 단계에서 별도로 검토한다.

다음 시작 지점
- `FE-ADM-02-1`
- 다음 구현은 관리자 로그인 route와 metadata 베이스를 먼저 추가하는 것이다.

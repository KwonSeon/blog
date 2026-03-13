# Blog Work Progress

이 파일은 저장소에 유지하는 현재 작업 기록이다.
- 작업을 시작하거나 끝낼 때마다 현재 단계와 다음 단계를 갱신한다.
- 구현은 한 번에 하나의 단계만 진행한다.
- 세부 단계가 끝나기 전에는 다음 구현으로 넘어가지 않는다.
- 세부 단계는 가능하면 `패키지 -> 클래스 -> 메서드` 단위까지 쪼개서 기록한다.
- 구현 체크는 "무슨 파일을 만든다"보다 "어떤 메서드를 추가/수정한다" 기준으로 남긴다.
- application 계층 작업은 가능하면 `query -> result -> usecase method` 순서로 기록한다.

현재 작업 주제
- `P0-011-BE-6 공개 글 조회 API(목록/상세 slug)`

최근 완료 작업
- `P0-010-BE-5 공개 프로젝트 조회 API(목록/상세 slug)` 완료
- 완료 범위
  - `GET /api/projects`
  - `GET /api/projects/{slug}`
  - project 공개용 query/usecase/result/service/controller/response
  - 공개 프로젝트 repository 조회 메서드
  - 공개 endpoint permitAll 설정
  - project 공개 조회 관련 repository/service/controller/security 테스트
- 구현 기준
  - 공개 노출 대상은 `visibility=PUBLIC && status=ACTIVE`
  - 공개 상세 slug 조회도 같은 조건을 만족하는 프로젝트만 반환
  - 비공개/비활성 프로젝트 slug 조회는 `PROJECT_NOT_FOUND`로 통일
  - 공개 목록 정렬은 `publishedAt desc -> projectId desc`
- 최종 확인: `blog/apps/api`에서 `./gradlew --no-daemon test` 통과
- `P0-009-BE-4 글 CRUD(Admin) + DRAFT/PUBLISHED 전환` 완료
- 완료 범위: `/api/admin/posts` create/detail/list/update/delete/status change, post 도메인/리포지토리/서비스/컨트롤러/DTO, post 관련 테스트
- 구현 기준
  - `authorUserId`는 인증 principal의 `userId` 기준으로 설정
  - `publishedAt`은 서버가 상태 기준으로 관리
  - 글 수정은 본문 필드 중심으로 처리하고, 상태 변경은 `PATCH /api/admin/posts/{postId}/status`로 분리
- 추가 정리
  - `Visibility` -> `common/domain`
  - `BaseTimeEntity` -> `common/persistence`
  - `postProjects`, `childTags` -> `List` + `@OrderBy`
  - 인증 구조 -> `global/security`(principal, authentication, converter, annotation)
- 최종 확인: `blog/apps/api`에서 `./gradlew --no-daemon test` 통과

현재 확인된 상태
- 관리자 글 API는 완료됐다.
  - `/api/admin/posts`
  - `POST / GET(detail) / GET(list) / PUT / PATCH(status) / DELETE`
- 공개 프로젝트 조회 API는 완료됐다.
  - `/api/projects`
  - `GET(list) / GET(detail by slug)`
- 공개 글 조회 API는 아직 없다.
- `post` 도메인과 관리자 글 CRUD는 이미 있어서, 다음 단계는 공개 엔드포인트와 공개 노출 조건만 추가하면 된다.

현재 확정 범위
- 이번 단계에서는 공개 글 `목록조회/상세조회(slug)`까지만 다룬다.
- 관리자 CRUD와 상태 전환 규칙은 이번 단계에 포함하지 않는다.
- 공개 노출 대상은 `visibility=PUBLIC && status=PUBLISHED`로 본다.
- 공개 상세 slug 조회도 같은 공개 조건을 만족하는 글만 반환한다.
- 비공개/미발행 글 slug 조회는 존재 유무를 노출하지 않고 `POST_NOT_FOUND`로 통일한다.
- 공개 목록 정렬은 `publishedAt desc -> postId desc` 기준으로 간다.

세부 단계
- [ ] POST-PUB-01 요구사항/재사용 구조 확인
  - [ ] POST-PUB-01-1 `README.md`의 `P0-011-BE-6` 범위와 제외 범위 다시 확인
  - [ ] POST-PUB-01-2 `AdminPostController`, `GetPostService`, `ListPostsService`에서 재사용 가능한 응답 필드 확인
  - [ ] POST-PUB-01-3 공개 응답에서 숨길 필드(`visibility`, `status`, `authorUserId`) 여부 확정
- [ ] POST-PUB-02 공개 노출 규칙 확정
  - [ ] POST-PUB-02-1 공개 목록 포함 조건을 `visibility=PUBLIC && status=PUBLISHED`로 확정
  - [ ] POST-PUB-02-2 공개 상세 slug 조회도 동일 노출 조건으로 제한할지 확정
  - [ ] POST-PUB-02-3 비공개/미발행 글 slug는 `POST_NOT_FOUND`로 통일할지 확정
  - [ ] POST-PUB-02-4 공개 목록 정렬 기준을 `publishedAt desc -> postId desc`로 갈지 확정
- [ ] POST-PUB-03 공개 목록 repository 메서드 연결
  - [ ] POST-PUB-03-1-1 `PostRepositoryPort.findPublicPosts()` 추가
  - [ ] POST-PUB-03-1-2 `PostRepositoryAdapter.findPublicPosts()` 구현
  - [ ] POST-PUB-03-1-3 `PostJpaRepository.findAllByVisibilityAndStatus(Visibility visibility, PostStatus status)` 추가
- [ ] POST-PUB-03-2 공개 상세 repository 메서드 연결
  - [ ] POST-PUB-03-2-1 `PostRepositoryPort.findPublicPostBySlug(String slug)` 추가
  - [ ] POST-PUB-03-2-2 `PostRepositoryAdapter.findPublicPostBySlug(String slug)` 구현
  - [ ] POST-PUB-03-2-3 `PostJpaRepository.findBySlugAndVisibilityAndStatus(String slug, Visibility visibility, PostStatus status)` 추가
- [ ] POST-PUB-04 application query/result/usecase 추가
  - [ ] POST-PUB-04-1 `ListPublicPostsQuery` 추가
  - [ ] POST-PUB-04-2 `ListPublicPostsResult` 추가
  - [ ] POST-PUB-04-3 `ListPublicPostsUseCase.listPosts(ListPublicPostsQuery query)` 추가
  - [ ] POST-PUB-04-4 `GetPublicPostQuery` 추가
  - [ ] POST-PUB-04-5 `GetPublicPostResult` 추가
  - [ ] POST-PUB-04-6 `GetPublicPostUseCase.getPost(GetPublicPostQuery query)` 추가
- [ ] POST-PUB-05 service 메서드 구현
  - [ ] POST-PUB-05-1 `ListPublicPostsService.listPosts(ListPublicPostsQuery query)` 구현
  - [ ] POST-PUB-05-2 `ListPublicPostsService.toItem(Post post)` 구현
  - [ ] POST-PUB-05-3 `ListPublicPostsService.PUBLIC_LIST_ORDER` 정렬 기준 반영
  - [ ] POST-PUB-05-4 `GetPublicPostService.getPost(GetPublicPostQuery query)` 구현
  - [ ] POST-PUB-05-5 `GetPublicPostService.toResult(Post post)` 구현
- [ ] POST-PUB-06 response/controller 메서드 구현
  - [ ] POST-PUB-06-1 `ListPublicPostsResponse.from(ListPublicPostsResult result)` 구현
  - [ ] POST-PUB-06-2 `GetPublicPostResponse.from(GetPublicPostResult result)` 구현
  - [ ] POST-PUB-06-3 `PublicPostController.listPosts()` 구현
  - [ ] POST-PUB-06-4 `PublicPostController.getPost(String slug)` 구현
  - [ ] POST-PUB-06-5 공개 엔드포인트를 `/api/posts`, `/api/posts/{slug}`로 연결
- [ ] POST-PUB-07 테스트 메서드 작성/보정
  - [ ] POST-PUB-07-1 `PostJpaRepositoryTest.findAllByVisibilityAndStatus_returnsOnlyPublicPublishedPosts()` 작성
  - [ ] POST-PUB-07-2 `PostJpaRepositoryTest.findBySlugAndVisibilityAndStatus_returnsPost_whenPublicPublished()` 작성
  - [ ] POST-PUB-07-3 `ListPublicPostsServiceTest.listPosts_returnsOnlyPublicPublishedPostsInOrder()` 작성
  - [ ] POST-PUB-07-4 `GetPublicPostServiceTest.getPost_returnsPost_whenSlugIsPublic()` 작성
  - [ ] POST-PUB-07-5 `GetPublicPostServiceTest.getPost_throws_whenSlugIsNotPublic()` 작성
  - [ ] POST-PUB-07-6 `PublicPostControllerTest.listPosts_returns200()` 작성
  - [ ] POST-PUB-07-7 `PublicPostControllerTest.getPost_returns200()` 작성
  - [ ] POST-PUB-07-8 `PublicPostControllerTest.getPost_returns404_whenPostIsHidden()` 작성
  - [ ] POST-PUB-07-9 `./gradlew --no-daemon test` 확인
- [ ] POST-PUB-08 README와 진행 상태 갱신

계획 메모
- 공개 글 응답은 관리자 응답보다 작게 유지하는 쪽을 우선 검토한다.
- slug 기반 상세 조회를 우선 구현하고, postId 기반 공개 조회는 이번 단계 범위에서 제외한다.
- 검색/필터는 `P0-012-BE-7`에서 별도 진행한다.
- 공개 구현은 기존 관리자 조회 코드를 그대로 재사용하지 말고, 공개 전용 query/result/response를 분리하는 쪽으로 간다.

다음 시작 지점
- `POST-PUB-01-1`
- 다음 구현은 `README 기준으로 P0-011-BE-6 범위와 제외 범위를 다시 확인하는 것`이다.

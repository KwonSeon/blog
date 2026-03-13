# Blog Work Progress

이 파일은 저장소에 유지하는 현재 작업 기록이다.
- 작업을 시작하거나 끝낼 때마다 현재 단계와 다음 단계를 갱신한다.
- 구현은 한 번에 하나의 단계만 진행한다.
- 세부 단계가 끝나기 전에는 다음 구현으로 넘어가지 않는다.
- 세부 단계는 가능하면 `패키지 -> 클래스 -> 메서드` 단위까지 쪼개서 기록한다.
- 구현 체크는 "무슨 파일을 만든다"보다 "어떤 메서드를 추가/수정한다" 기준으로 남긴다.

현재 작업 주제
- `P0-010-BE-5 공개 프로젝트 조회 API(목록/상세 slug)`

최근 완료 작업
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
- `P0-008-BE-3 프로젝트 CRUD(Admin)` 완료
- 완료 범위: `/api/admin/projects` create/detail/list/update/delete, service/controller/DTO, project 관련 테스트
- 최종 확인: project 관련 테스트 통과 + `blog/apps/api`에서 `./gradlew --no-daemon test` 통과
- `P0-007-BE-2 관리자 로그인 API + ADMIN 보호구역 설정` 완료
- 완료 범위: `/api/admin/auth/login`, JWT 발급/검증, `/api/admin/** -> ADMIN` 인가 규칙, 서비스/컨트롤러/인가 테스트
- 최종 확인: `blog/apps/api`에서 `./gradlew --no-daemon test` 통과

현재 확인된 상태
- 관리자 글 API는 완료됐다.
  - `/api/admin/posts`
  - `POST / GET(detail) / GET(list) / PUT / PATCH(status) / DELETE`
- 공개 프로젝트 조회 API는 아직 없다.
- 관리자 프로젝트 CRUD와 slug 저장 구조는 이미 있어서, 다음 단계는 공개 엔드포인트와 공개 노출 조건만 추가하면 된다.
- README 기준에서 다음 단계 `P0-010-BE-5`는 공개 프로젝트 조회만 담당하고, 공개 글 조회는 `P0-011-BE-6`에서 별도 진행한다.

현재 확정 범위
- 이번 단계에서는 공개 프로젝트 `목록조회/상세조회(slug)`까지만 다룬다.
- 관리자 CRUD와 인증 규칙은 이번 단계에 포함하지 않는다.
- 공개 노출 대상은 프로젝트의 `visibility`, `status`, `slug` 기준으로 확정한다.
- 정렬/반환 필드 범위는 관리자 목록 API와 별도로 다시 정리한다.

세부 단계
- [ ] PROJ-PUB-01 요구사항/재사용 구조 확인
  - [ ] PROJ-PUB-01-1 `README.md`의 `P0-010-BE-5` 범위와 제외 범위 다시 확인
  - [ ] PROJ-PUB-01-2 `AdminProjectController`, `GetProjectService`, `ListProjectsService`에서 재사용 가능한 응답 필드 확인
  - [ ] PROJ-PUB-01-3 공개 응답에서 숨길 필드(`visibility`, `status`) 여부 확정
- [ ] PROJ-PUB-02 공개 노출 규칙 확정
  - [ ] PROJ-PUB-02-1 공개 목록 포함 조건을 `visibility=PUBLIC && status=ACTIVE`로 확정
  - [ ] PROJ-PUB-02-2 공개 상세 slug 조회도 동일 노출 조건으로 제한할지 확정
  - [ ] PROJ-PUB-02-3 비공개/비활성 프로젝트 slug는 `PROJECT_NOT_FOUND`로 통일할지 확정
  - [ ] PROJ-PUB-02-4 공개 목록 정렬 기준을 `publishedAt desc -> projectId desc`로 갈지 확정
- [ ] PROJ-PUB-03 repository 메서드 설계/추가
  - [ ] PROJ-PUB-03-1 `ProjectRepositoryPort.findPublicProjects()` 추가
  - [ ] PROJ-PUB-03-2 `ProjectRepositoryPort.findPublicProjectBySlug(String slug)` 추가
  - [ ] PROJ-PUB-03-3 `ProjectRepositoryAdapter.findPublicProjects()` 구현
  - [ ] PROJ-PUB-03-4 `ProjectRepositoryAdapter.findPublicProjectBySlug(String slug)` 구현
  - [ ] PROJ-PUB-03-5 `ProjectJpaRepository.findAllByVisibilityAndStatus(Visibility visibility, ProjectStatus status)` 추가
  - [ ] PROJ-PUB-03-6 `ProjectJpaRepository.findBySlugAndVisibilityAndStatus(String slug, Visibility visibility, ProjectStatus status)` 추가
- [ ] PROJ-PUB-04 application query/usecase/result 추가
  - [ ] PROJ-PUB-04-1 `ListPublicProjectsQuery` 추가
  - [ ] PROJ-PUB-04-2 `GetPublicProjectQuery` 추가
  - [ ] PROJ-PUB-04-3 `ListPublicProjectsUseCase.listProjects(ListPublicProjectsQuery query)` 추가
  - [ ] PROJ-PUB-04-4 `GetPublicProjectUseCase.getProject(GetPublicProjectQuery query)` 추가
  - [ ] PROJ-PUB-04-5 `ListPublicProjectsResult` 추가
  - [ ] PROJ-PUB-04-6 `GetPublicProjectResult` 추가
- [ ] PROJ-PUB-05 service 메서드 구현
  - [ ] PROJ-PUB-05-1 `ListPublicProjectsService.listProjects(ListPublicProjectsQuery query)` 구현
  - [ ] PROJ-PUB-05-2 `ListPublicProjectsService.toItem(Project project)` 구현
  - [ ] PROJ-PUB-05-3 `ListPublicProjectsService.PUBLIC_LIST_ORDER` 정렬 기준 반영
  - [ ] PROJ-PUB-05-4 `GetPublicProjectService.getProject(GetPublicProjectQuery query)` 구현
  - [ ] PROJ-PUB-05-5 `GetPublicProjectService.toResult(Project project)` 구현
- [ ] PROJ-PUB-06 response/controller 메서드 구현
  - [ ] PROJ-PUB-06-1 `ListPublicProjectsResponse.from(ListPublicProjectsResult result)` 구현
  - [ ] PROJ-PUB-06-2 `GetPublicProjectResponse.from(GetPublicProjectResult result)` 구현
  - [ ] PROJ-PUB-06-3 `PublicProjectController.listProjects()` 구현
  - [ ] PROJ-PUB-06-4 `PublicProjectController.getProject(String slug)` 구현
  - [ ] PROJ-PUB-06-5 공개 엔드포인트를 `/api/projects`, `/api/projects/{slug}`로 연결
- [ ] PROJ-PUB-07 테스트 메서드 작성/보정
  - [ ] PROJ-PUB-07-1 `ProjectJpaRepositoryTest.findAllByVisibilityAndStatus_returnsOnlyPublicActiveProjects()` 작성
  - [ ] PROJ-PUB-07-2 `ProjectJpaRepositoryTest.findBySlugAndVisibilityAndStatus_returnsProject_whenPublicActive()` 작성
  - [ ] PROJ-PUB-07-3 `ListPublicProjectsServiceTest.listProjects_returnsOnlyPublicActiveProjectsInOrder()` 작성
  - [ ] PROJ-PUB-07-4 `GetPublicProjectServiceTest.getProject_returnsProject_whenSlugIsPublic()` 작성
  - [ ] PROJ-PUB-07-5 `GetPublicProjectServiceTest.getProject_throws_whenSlugIsNotPublic()` 작성
  - [ ] PROJ-PUB-07-6 `PublicProjectControllerTest.listProjects_returns200()` 작성
  - [ ] PROJ-PUB-07-7 `PublicProjectControllerTest.getProject_returns200()` 작성
  - [ ] PROJ-PUB-07-8 `PublicProjectControllerTest.getProject_returns404_whenProjectIsHidden()` 작성
  - [ ] PROJ-PUB-07-9 `./gradlew --no-daemon test` 확인
- [ ] PROJ-PUB-08 README와 진행 상태 갱신

계획 메모
- 공개 프로젝트 응답은 관리자 응답보다 작게 유지하는 쪽을 우선 검토한다.
- slug 기반 상세 조회를 우선 구현하고, projectId 기반 공개 조회는 이번 단계 범위에서 제외한다.
- 검색/필터는 `P0-012-BE-7`에서 별도 진행한다.
- 공개 구현은 기존 관리자 조회 코드를 그대로 재사용하지 말고, 공개 전용 query/result/response를 분리하는 쪽으로 간다.

다음 시작 지점
- `PROJ-PUB-01-1`
- 다음 구현은 `README 범위 재확인 -> 공개 노출 규칙 확정 -> repository 공개 조회 메서드 이름/계약 확정` 순서로 진행하면 된다.

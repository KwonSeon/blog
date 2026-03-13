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
- `P0-012-BE-7 QueryDSL 기반 필터/검색`

최근 완료 작업
- `P0-011-BE-6 공개 글 조회 API(목록/상세 slug)` 완료
- 완료 범위
  - `GET /api/posts`
  - `GET /api/posts/{slug}`
  - post 공개용 query/usecase/result/service/controller/response
  - 공개 post repository 조회 메서드
  - 공개 endpoint permitAll 설정
  - post 공개 조회 관련 repository/service/controller/security 테스트
- 구현 기준
  - 공개 노출 대상은 `visibility=PUBLIC && status=PUBLISHED`
  - 공개 상세 slug 조회도 같은 조건을 만족하는 글만 반환
  - 비공개/미발행 글 slug 조회는 `POST_NOT_FOUND`로 통일
  - 공개 목록 정렬은 `publishedAt desc -> postId desc`
  - 공개 응답에는 `createdAt`, `updatedAt`를 포함
- 최종 확인: `blog/apps/api`에서 `./gradlew --no-daemon test` 통과
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
- 공개 글 조회 API도 완료됐다.
  - `/api/posts`
  - `GET(list) / GET(detail by slug)`
- QueryDSL 기반 필터/검색은 아직 없다.
- `apps/api/build.gradle`에는 QueryDSL 의존성/설정이 아직 없고, 관련 코드도 없다.

현재 확정 범위
- 이번 단계에서는 `QueryDSL` 도입과 공개 조회용 필터/검색 기반을 우선 다룬다.
- 현재 기준으로 QueryDSL 의존성, Q 타입 생성 설정, `JPAQueryFactory` bean은 전부 없다.
- 검색/필터 대상이 글 목록 중심인지, 프로젝트까지 포함할지는 첫 단계에서 다시 확정한다.
- 기존 공개 조회 API(`/api/posts`, `/api/projects`)의 계약을 깨지 않는 범위로 확장한다.

세부 단계
- [ ] QUERY-01 요구사항/대상 범위 확인
  - [ ] QUERY-01-1 `README.md`의 `P0-012-BE-7`과 현재 공개 API 범위 다시 확인
  - [ ] QUERY-01-2 검색/필터 대상을 `post`만 우선할지 `project`까지 포함할지 확정
  - [ ] QUERY-01-3 첫 구현의 query parameter 범위를 정리
- [ ] QUERY-02 QueryDSL 도입 기반 추가
  - [ ] QUERY-02-1 `build.gradle`에 QueryDSL 의존성과 annotation processor 추가
  - [ ] QUERY-02-2 `JPAQueryFactory` bean 위치와 패키지 확정
  - [ ] QUERY-02-3 `./gradlew --no-daemon compileJava`로 Q 타입 생성 확인
- [ ] QUERY-03 공개 글 목록 검색 repository 초안 연결
  - [ ] QUERY-03-1 검색/필터 query 객체 추가
  - [ ] QUERY-03-2 `PostRepositoryPort` 검색 메서드 시그니처 추가
  - [ ] QUERY-03-3 QueryDSL adapter/custom repository 구현 위치 확정
- [ ] QUERY-04 테스트 전략 확정
  - [ ] QUERY-04-1 repository/service/controller 중 어디까지 우선 검증할지 정리
  - [ ] QUERY-04-2 검색 정렬/빈 결과/부분일치 테스트 기준 정리

계획 메모
- QueryDSL 도입 전에는 검색 범위와 query parameter를 먼저 확정한다.
- 공개 조회 계약은 이미 열려 있으므로, 다음 단계는 기존 endpoint 확장과 신규 repository 계층 추가를 중심으로 본다.
- `post`가 우선 후보지만, `project`까지 포함할지는 첫 단계에서 다시 확인한다.

다음 시작 지점
- `QUERY-01-1`
- 다음 구현은 `README 기준으로 P0-012-BE-7 범위와 현재 공개 API 상태를 다시 확인하는 것이다.`

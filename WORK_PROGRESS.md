# Blog Work Progress

이 파일은 저장소에 유지하는 현재 작업 기록이다.
- 작업을 시작하거나 끝낼 때마다 현재 단계와 다음 단계를 갱신한다.
- 구현은 한 번에 하나의 단계만 진행한다.
- 세부 단계가 끝나기 전에는 다음 구현으로 넘어가지 않는다.

현재 작업 주제
- `P0-009-BE-4 글 CRUD(Admin) + DRAFT/PUBLISHED 전환`

최근 완료 작업
- `P0-008-BE-3 프로젝트 CRUD(Admin)` 완료
- 완료 범위: `/api/admin/projects` create/detail/list/update/delete, service/controller/DTO, project 관련 테스트
- 최종 확인: project 관련 테스트 통과 + `blog/apps/api`에서 `./gradlew --no-daemon test` 통과
- `P0-007-BE-2 관리자 로그인 API + ADMIN 보호구역 설정` 완료
- 완료 범위: `/api/admin/auth/login`, JWT 발급/검증, `/api/admin/** -> ADMIN` 인가 규칙, 서비스/컨트롤러/인가 테스트
- 최종 확인: `blog/apps/api`에서 `./gradlew --no-daemon test` 통과

현재 확인된 상태
- `P0-008-BE-3`은 구현, 테스트, README 반영까지 완료했다.
- 관리자 프로젝트 CRUD는 `/api/admin/projects` 아래에서 아래 기능까지 동작한다.
  - create
  - detail
  - list
  - update
  - delete
- 공개 프로젝트 조회는 여전히 `P0-010-BE-5`에서 별도 진행한다.

현재 확정 범위
- 이번 단계에서는 관리자 프로젝트 `생성/단건조회/목록조회/수정/삭제`까지만 다룬다.
- 공개 프로젝트 조회 API는 이번 단계에 포함하지 않는다.
- 관리자 경로는 기존 보안 체인을 재사용해 `/api/admin/**` 아래에서 구현한다.
- 관리자 CRUD 입력/수정 대상은 아래 필드까지만 우선 포함한다.
  - `slug`
  - `title`
  - `summary`
  - `serviceUrl`
  - `repoUrl`
  - `visibility`
  - `status`
  - `coverMediaAssetId`
  - `publishedAt`
- `viewCount`, `likeCount`, `projectTags`, `postProjects`는 이번 단계의 입력/수정 범위에서 제외한다.

세부 단계
- [x] PROJ-01 README 요구사항과 현재 `project` 구조 재확인
- [x] PROJ-02 관리자 프로젝트 CRUD 범위 확정
  - [x] PROJ-02-1 포함 기능 확정: create / detail / list / update / delete
  - [x] PROJ-02-2 제외 기능 확정: public read / tag 연동 / media 업로드 / QueryDSL 검색
- [x] PROJ-03 API 계약과 엔드포인트 설계
  - [x] PROJ-03-1 관리자 엔드포인트 경로 확정
  - [x] PROJ-03-2 create request/response DTO 필드 확정
  - [x] PROJ-03-3 detail/list response DTO 필드 확정
  - [x] PROJ-03-4 update request/response DTO 필드 확정
  - [x] PROJ-03-5 delete 응답 형태 확정
  - [x] PROJ-03-6 필드 검증 규칙 확정(`slug`, `title`, URL, enum)
  - [x] PROJ-03-7 상태코드/에러 케이스 확정
- [x] PROJ-04 도메인/리포지토리 보강
  - [x] PROJ-04-1 `Project` 생성 메서드 또는 팩토리 추가
  - [x] PROJ-04-2 `Project` 수정 메서드 추가
  - [x] PROJ-04-3 slug 중복 확인용 repository port 메서드 추가
  - [x] PROJ-04-4 관리자 단건 조회용 repository port 메서드 정리
  - [x] PROJ-04-5 관리자 목록 조회용 repository port 메서드 추가
  - [x] PROJ-04-6 삭제용 repository port 메서드 추가
  - [x] PROJ-04-7 JPA repository 메서드 추가/정리
  - [x] PROJ-04-8 adapter 구현 보강
- [x] PROJ-05 `createProject` 메서드 구현
  - [x] PROJ-05-1 `CreateProjectCommand` 정의
  - [x] PROJ-05-2 `CreateProjectResult` 정의
  - [x] PROJ-05-3 `CreateProjectUseCase` 정의
  - [x] PROJ-05-4 `createProject` 입력 필드 검증 규칙 확정
  - [x] PROJ-05-5 `createProject`에서 slug 중복 확인 처리
  - [x] PROJ-05-6 `Project.create(...)` 또는 동등한 생성 로직 구현
  - [x] PROJ-05-7 `createProject` 저장 로직 구현
  - [x] PROJ-05-8 `CreateProjectRequest` DTO 구현
  - [x] PROJ-05-9 `CreateProjectResponse` DTO 구현
  - [x] PROJ-05-10 `POST /api/admin/projects` -> `createProject` 컨트롤러 구현
  - [x] PROJ-05-11 `createProject` service 성공 테스트 작성
  - [x] PROJ-05-12 `createProject` service 실패 테스트 작성(slug 중복/검증 실패)
  - [x] PROJ-05-13 `createProject` controller 성공 테스트 작성
  - [x] PROJ-05-14 `createProject` controller 검증/에러 테스트 작성
- [x] PROJ-06 `getProject` / `listProjects` 메서드 구현
  - [x] PROJ-06-1 `GetProjectQuery` 정의
  - [x] PROJ-06-2 `GetProjectResult` 정의
  - [x] PROJ-06-3 `GetProjectUseCase` 정의
  - [x] PROJ-06-4 `getProject` 조회 실패 규칙 확정(not found)
  - [x] PROJ-06-5 `getProject` service 구현
  - [x] PROJ-06-6 `GetProjectResponse` DTO 구현
  - [x] PROJ-06-7 `GET /api/admin/projects/{projectId}` -> `getProject` 컨트롤러 구현
  - [x] PROJ-06-8 `getProject` service 테스트 작성
  - [x] PROJ-06-9 `getProject` controller 테스트 작성
  - [x] PROJ-06-10 `ListProjectsQuery` 정의
  - [x] PROJ-06-11 `ListProjectsResult` 정의
  - [x] PROJ-06-12 `ListProjectsUseCase` 정의
  - [x] PROJ-06-13 `listProjects` 정렬/반환 범위 규칙 확정
  - [x] PROJ-06-14 `listProjects` service 구현
  - [x] PROJ-06-15 `ListProjectsResponse` DTO 구현
  - [x] PROJ-06-16 `GET /api/admin/projects` -> `listProjects` 컨트롤러 구현
  - [x] PROJ-06-17 `listProjects` service 테스트 작성
  - [x] PROJ-06-18 `listProjects` controller 테스트 작성
- [x] PROJ-07 `updateProject` 메서드 구현
  - [x] PROJ-07-1 `UpdateProjectCommand` 정의
  - [x] PROJ-07-2 `UpdateProjectResult` 정의
  - [x] PROJ-07-3 `UpdateProjectUseCase` 정의
  - [x] PROJ-07-4 `updateProject` 수정 가능 필드 확정
  - [x] PROJ-07-5 `updateProject` 기존 프로젝트 조회 처리
  - [x] PROJ-07-6 `updateProject` slug 중복 검증 처리
  - [x] PROJ-07-7 `Project.update(...)` 또는 동등한 수정 로직 구현
  - [x] PROJ-07-8 `updateProject` 저장 로직 구현
  - [x] PROJ-07-9 `UpdateProjectRequest` DTO 구현
  - [x] PROJ-07-10 `UpdateProjectResponse` DTO 구현
  - [x] PROJ-07-11 `PUT/PATCH /api/admin/projects/{projectId}` -> `updateProject` 컨트롤러 구현
  - [x] PROJ-07-12 `updateProject` service 성공 테스트 작성
  - [x] PROJ-07-13 `updateProject` service 실패 테스트 작성(not found/slug 중복)
  - [x] PROJ-07-14 `updateProject` controller 성공 테스트 작성
  - [x] PROJ-07-15 `updateProject` controller 검증/에러 테스트 작성
- [x] PROJ-08 `deleteProject` 메서드 구현
  - [x] PROJ-08-1 `DeleteProjectUseCase` 정의
  - [x] PROJ-08-2 `deleteProject` 삭제 정책 확정(하드 삭제/참조 무결성 영향 확인)
  - [x] PROJ-08-3 `deleteProject` 기존 프로젝트 조회 처리
  - [x] PROJ-08-4 `deleteProject` 삭제 로직 구현
  - [x] PROJ-08-5 `DELETE /api/admin/projects/{projectId}` -> `deleteProject` 컨트롤러 구현
  - [x] PROJ-08-6 `deleteProject` service 성공 테스트 작성
  - [x] PROJ-08-7 `deleteProject` service 실패 테스트 작성(not found)
  - [x] PROJ-08-8 `deleteProject` controller 성공 테스트 작성
  - [x] PROJ-08-9 `deleteProject` controller 에러 테스트 작성
- [x] PROJ-09 전체 테스트 실행 및 보정
  - [x] PROJ-09-1 project domain/repository 기존 테스트 영향 확인
  - [x] PROJ-09-2 project 관련 service 테스트 실행
  - [x] PROJ-09-3 project 관련 controller 테스트 실행
  - [x] PROJ-09-4 필요 시 repository/integration 테스트 보정
  - [x] PROJ-09-5 `./gradlew --no-daemon test` 전체 통과 확인
- [x] PROJ-10 README와 진행 상태 갱신
  - [x] PROJ-10-1 README의 `P0-008-BE-3` 상태 재판정
  - [x] PROJ-10-2 `WORK_PROGRESS.md` 완료 상태 정리
  - [x] PROJ-10-3 다음 시작 지점 갱신

계획 메모
- 우선 구현 필드는 현재 `Project` 엔티티 기준으로 잡는다.
  - `slug`
  - `title`
  - `summary`
  - `serviceUrl`
  - `repoUrl`
  - `visibility`
  - `status`
  - `coverMediaAssetId`
  - `publishedAt`
- `viewCount`, `likeCount`는 관리자 CRUD 입력값에서 직접 받지 않고 시스템 관리 값으로 유지하는 쪽을 우선 검토한다.
- `projectTags`는 현재 단계에서 CRUD 범위에 넣지 않고, 기본 프로젝트 본문 필드부터 먼저 완성한다.
- 삭제 정책 메모:
  관리자 소유 콘텐츠는 우선 hard delete,
  외부 사용자 생성 데이터(예: 댓글)는 soft delete 우선으로 설계한다.

PROJ-01 진행 결과
- README에서 `P0-008-BE-3`은 `프로젝트 CRUD(Admin)`까지만 담당하고, 공개 조회는 `P0-010-BE-5`로 분리돼 있음을 다시 확인했다.
- 현재 `project` 패키지에는 아래만 존재한다.
  - domain: `Project`, `ProjectTag`, `ProjectTagId`, `ProjectStatus`
  - application/port/out: `ProjectRepositoryPort`
  - infrastructure/persistence: `ProjectRepositoryAdapter`, `ProjectJpaRepository`
- 현재 없는 계층은 아래다.
  - application `port/in`
  - command/result/query
  - admin controller/DTO
  - CRUD용 service
  - CRUD용 테스트
- 즉 현재 출발점은 `엔티티 + repository 기반은 있음`, `관리자 CRUD 유스케이스/웹 계층은 아직 없음`으로 정리한다.

PROJ-02 진행 결과
- 포함 기능은 `createProject`, `getProject`, `listProjects`, `updateProject`, `deleteProject` 5개 메서드로 고정한다.
- 이번 단계의 관리자 조회는 `projectId` 기준 단건 조회와 기본 목록 조회까지만 포함한다.
- 이번 단계에서 제외하는 범위는 아래다.
  - 공개 프로젝트 조회 API
  - `projectTags` 연동
  - MinIO/media 업로드 처리
  - `viewCount`, `likeCount` 직접 수정
  - QueryDSL 검색/필터
- `postProjects` 연동 처리
- 즉 `P0-008-BE-3`은 먼저 “프로젝트 본문 필드 CRUD”를 완성하는 단계로 확정한다.

PROJ-03 진행 결과
- 관리자 엔드포인트는 아래 5개로 확정한다.
  - `POST /api/admin/projects` -> `createProject`
  - `GET /api/admin/projects/{projectId}` -> `getProject`
  - `GET /api/admin/projects` -> `listProjects`
  - `PUT /api/admin/projects/{projectId}` -> `updateProject`
  - `DELETE /api/admin/projects/{projectId}` -> `deleteProject`
- `updateProject`는 `PATCH`가 아니라 `PUT`으로 고정해, 이번 단계에서는 전체 수정 요청 기준으로 구현한다.
- create request 필드는 아래로 확정한다.
  - `slug`
  - `title`
  - `summary`
  - `serviceUrl`
  - `repoUrl`
  - `visibility`
  - `status`
  - `coverMediaAssetId`
- create response는 아래 필드를 반환한다.
  - `projectId`
  - `slug`
  - `title`
  - `summary`
  - `serviceUrl`
  - `repoUrl`
  - `visibility`
  - `status`
  - `coverMediaAssetId`
  - `publishedAt`
  - `createdAt`
  - `updatedAt`
- detail response는 create response와 같은 상세 필드를 사용한다.
- list response는 `items + totalCount` 구조로 두고, 각 item에는 아래 요약 필드를 담는다.
  - `projectId`
  - `slug`
  - `title`
  - `visibility`
  - `status`
  - `publishedAt`
  - `createdAt`
  - `updatedAt`
- update request/response는 create와 동일 필드 구조로 두고, `projectId`는 path variable로만 받는다.
- `publishedAt`은 request로 받지 않고 서버가 관리하며, response에서는 그대로 내려준다.
- delete 응답은 본문 없는 `204 No Content`로 확정한다.
- 필드 검증 규칙은 아래 기준으로 확정한다.
  - `slug`: 필수, 최대 120자, 소문자/숫자/하이픈 형식
  - `title`: 필수, 최대 200자
  - `summary`: 선택, 최대 500자
  - `serviceUrl`, `repoUrl`: 선택, 최대 500자, URL 형식
  - `visibility`, `status`: 필수 enum
  - `coverMediaAssetId`: 선택, 양수
- 상태코드/에러 케이스는 아래로 확정한다.
  - `201 Created`: create 성공
  - `200 OK`: detail/list/update 성공
  - `204 No Content`: delete 성공
  - `400 Bad Request`: 요청 본문 검증 실패, enum/JSON 파싱 실패
  - `401 Unauthorized`: 토큰 없음/인증 실패
  - `403 Forbidden`: ADMIN 권한 없음
  - `404 Not Found`: 대상 프로젝트 없음
  - `409 Conflict`: slug 중복
- `500 Internal Server Error`: 미처리 예외
- 에러 응답 형식은 기존 `GlobalExceptionHandler + ErrorResponse` 표준 구조를 그대로 사용한다.

PROJ-04-1 진행 결과
- `Project` 엔티티에 `Project.create(...)` 정적 생성 메서드를 추가했다.
- 생성 메서드는 이번 단계에서 입력받기로 확정한 프로젝트 본문 필드(`slug`, `title`, `summary`, `serviceUrl`, `repoUrl`, `visibility`, `status`, `coverMediaAssetId`, `publishedAt`)를 한 번에 받는다.
- 생성 내부는 필드를 직접 채운 뒤 마지막에 `validate()` 한 번으로 생성된 엔티티 전체 상태를 검증하도록 정리했다.
- 필수 필드인 `slug`, `title`, `visibility`, `status`는 `ProjectErrorStatus` 기반 `BaseException`으로 도메인 검증하도록 바꿨다.
- `viewCount`, `likeCount` 같은 시스템 관리 값은 엔티티 기본값을 그대로 유지한다.
- `./gradlew --no-daemon compileJava` 통과를 확인했다.

PROJ-04-2 진행 결과
- `Project` 엔티티에 `update(...)` 메서드를 추가했다.
- 수정 메서드는 create와 같은 입력 필드 구조(`slug`, `title`, `summary`, `serviceUrl`, `repoUrl`, `visibility`, `status`, `coverMediaAssetId`, `publishedAt`)를 받는다.
- 수정 내부는 필드를 다시 채운 뒤 마지막에 `validate()`를 호출해, 생성과 동일한 도메인 검증 규칙을 재사용하도록 정리했다.
- `./gradlew --no-daemon compileJava` 통과를 확인했다.

PROJ-04-3 진행 결과
- `ProjectRepositoryPort`에 `existsBySlug(String slug)` 메서드를 추가했다.
- `ProjectRepositoryAdapter`, `ProjectJpaRepository`에도 같은 메서드를 연결해 서비스 계층에서 slug 중복 여부를 바로 확인할 수 있게 했다.
- 이 단계에서는 slug 중복 확인 경로만 열었고, update 시 자기 자신 제외 같은 추가 조건은 다음 유스케이스 단계에서 다룬다.
- `./gradlew --no-daemon compileJava` 통과를 확인했다.

PROJ-04-4 진행 결과
- 관리자 단건 조회용 repository 메서드는 새로 늘리지 않고, 기존 `findById(Long projectId)`를 그대로 사용하기로 정리했다.
- `ProjectRepositoryPort`에 이 메서드가 관리자 상세 조회용이라는 설명을 짧게 남겨 역할을 명확히 했다.
- 즉 `getProject` 유스케이스는 `projectId` 기준 단건 조회를 그대로 재사용하는 방향으로 고정한다.
- `./gradlew --no-daemon compileJava` 통과를 확인했다.

PROJ-04-5 진행 결과
- 관리자 목록 조회용으로 `ProjectRepositoryPort`에 `findAll()` 메서드를 추가했다.
- `ProjectRepositoryAdapter`에도 같은 메서드를 연결해, `listProjects` 유스케이스가 우선 전체 목록 조회부터 시작할 수 있게 했다.
- `JpaRepository`는 이미 `findAll()`을 제공하므로, 이 단계에서는 `ProjectJpaRepository`에 별도 선언을 추가하지 않았다.
- 정렬/페이징/필터 규칙은 아직 포함하지 않고, 이후 `PROJ-06-13 listProjects 정렬/반환 범위 규칙 확정` 단계에서 정리한다.
- `./gradlew --no-daemon compileJava` 통과를 확인했다.

PROJ-04-6 진행 결과
- `deleteProject` 유스케이스가 사용할 수 있도록 `ProjectRepositoryPort`에 `delete(Project project)` 메서드를 추가했다.
- `ProjectRepositoryAdapter`에도 같은 메서드를 연결해, 관리자 프로젝트 삭제는 우선 hard delete 기준으로 진행할 수 있게 했다.
- `ProjectJpaRepository`는 이미 `JpaRepository`의 `delete(...)`를 상속받으므로, 이 단계에서는 별도 삭제 메서드를 추가하지 않았다.
- soft delete는 현재 범위에 포함하지 않고, 관리자 소유 콘텐츠는 hard delete 원칙을 그대로 유지한다.
- `./gradlew --no-daemon compileJava` 통과를 확인했다.

PROJ-04-7 진행 결과
- `ProjectJpaRepository` 기준으로 현재 port에서 사용하는 `save`, `findById`, `findAll`, `delete`는 모두 `JpaRepository` 기본 메서드 상속만으로 충분하다는 점을 확정했다.
- 따라서 이 단계에서는 JPA repository에 불필요한 중복 선언을 추가하지 않고, slug 조회 관련 `existsBySlug`, `findBySlug`만 명시 메서드로 유지한다.
- `ProjectJpaRepository`에 이 기준이 보이도록 짧은 설명 주석을 추가했다.
- `./gradlew --no-daemon compileJava` 통과를 확인했다.

PROJ-04-8 진행 결과
- `ProjectRepositoryAdapter` 기준으로 현재 관리자 CRUD 단계에서 필요한 `save`, `delete`, `existsBySlug`, `findById`, `findAll`, `findBySlug` 메서드가 모두 port와 일관되게 연결되어 있는지 다시 확인했다.
- adapter 계층은 저장/조회/삭제 규칙을 별도로 가공하지 않고 JPA repository 위임만 담당한다는 점을 코드 주석으로 남겼다.
- 이 단계에서는 adapter에 비즈니스 로직을 넣지 않고, 이후 유스케이스 서비스 계층에서 생성/조회/수정/삭제 규칙을 처리하는 방향을 유지한다.
- persistence adapter stereotype도 `@Component`에서 `@Repository`로 정리해, DB 접근 어댑터라는 역할과 Spring 예외 변환 의도를 더 분명하게 맞췄다.
- `./gradlew --no-daemon compileJava` 통과를 확인했다.

PROJ-05-1 진행 결과
- `project/application/command` 패키지에 `CreateProjectCommand` record를 추가했다.
- command 필드는 현재 `createProject`에서 쓰기로 확정한 입력 필드(`slug`, `title`, `summary`, `serviceUrl`, `repoUrl`, `visibility`, `status`, `coverMediaAssetId`)를 담는다.
- 이 단계에서는 아직 검증 로직을 넣지 않고, 유스케이스 입력 모델을 먼저 고정하는 데 집중했다.
- `publishedAt`은 요청 입력이 아니라 서버 관리 값으로 정리했다.
- 이후 `CreateProjectRequest`와 서비스 구현은 이 command를 기준으로 그대로 연결한다.

PROJ-05-2 진행 결과
- `project/application/result` 패키지에 `CreateProjectResult` record를 추가했다.
- result 필드는 create 응답에서 내려주기로 확정한 상세 필드(`projectId`, `slug`, `title`, `summary`, `serviceUrl`, `repoUrl`, `visibility`, `status`, `coverMediaAssetId`, `publishedAt`, `createdAt`, `updatedAt`)를 그대로 담는다.
- 시간 필드는 `Project`와 `BaseTimeEntity` 기준에 맞춰 `publishedAt`, `createdAt`, `updatedAt` 모두 `LocalDateTime`으로 정리했다.
- 이후 `CreateProjectResponse` DTO는 이 result를 기준으로 단순 매핑할 수 있게 정리했다.
- detail/update 응답도 같은 상세 필드 구조를 공유할 예정이므로, 이번 result 정의가 이후 조회/수정 응답 구조의 기준점이 된다.

PROJ-05-3 진행 결과
- `project/application/port/in` 패키지에 `CreateProjectUseCase` 인터페이스를 추가했다.
- 메서드 시그니처는 `CreateProjectResult createProject(CreateProjectCommand command)`로 고정해, 이후 컨트롤러가 service 구현체가 아니라 in-port에 의존하도록 준비했다.
- 메서드 이름은 이번 작업 계획의 기준 이름인 `createProject`로 통일했다.
- 이후 `CreateProjectService` 구현체는 이 인터페이스를 구현하는 방식으로 연결한다.

PROJ-05-4 진행 결과
- `createProject` 입력 검증은 우선 `CreateProjectRequest` DTO에서 구조적 검증을 담당하도록 확정했다.
- DTO 검증 규칙은 아래로 고정한다.
  - `slug`: `@NotBlank`, `@Size(max = 120)`, 소문자/숫자/하이픈 패턴 검증
  - `title`: `@NotBlank`, `@Size(max = 200)`
  - `summary`: 선택, `@Size(max = 500)`
  - `serviceUrl`, `repoUrl`: 선택, `@Size(max = 500)`, URL 형식 검증
  - `visibility`, `status`: `@NotNull`
  - `coverMediaAssetId`: 선택, 양수 검증
- enum 문자열이 잘못 들어오거나 JSON 역직렬화가 실패하는 경우는 기존 `GlobalExceptionHandler`의 `HttpMessageNotReadableException -> 400` 흐름을 그대로 사용한다.
- 도메인 엔티티 `Project.create(...)`는 최종 fail-fast 가드로 `slug`, `title`, `visibility`, `status`의 최소 불변식 검증을 유지한다.
- `publishedAt`은 request 검증 대상에서 제외하고, 서버가 상태 기준으로 결정하도록 정리했다.
- slug 중복은 저장소 조회가 필요한 비즈니스 규칙이므로 이 단계에서는 DTO/엔티티 검증에 넣지 않고, 다음 `PROJ-05-5`에서 service early return으로 처리한다.

PROJ-05-5 진행 결과
- slug 중복은 도메인 불변식이 아니라 저장소 조회가 필요한 비즈니스 규칙이라는 점을 기준으로 유지했다.
- `ProjectErrorStatus`에 `DUPLICATE_PROJECT_SLUG`(`409 Conflict`) 에러 코드를 추가해, create 시 중복 slug 실패를 표준 에러 응답으로 반환할 수 있게 했다.
- 실제 구현 기준은 `ProjectRepositoryPort.existsBySlug(command.slug())`가 `true`이면 `BaseException(ProjectErrorStatus.DUPLICATE_PROJECT_SLUG)`로 early return 하는 방식으로 고정했다.
- 아직 `CreateProjectService` 구현 전이므로, 이번 단계에서는 중복 slug 처리 규칙과 에러 코드를 먼저 코드에 반영했다.

PROJ-05-6 진행 결과
- `Project.create(...)`가 현재 생성에 필요한 필드(`slug`, `title`, `summary`, `serviceUrl`, `repoUrl`, `visibility`, `status`, `coverMediaAssetId`)와 서버 계산 `publishedAt`을 함께 받을 수 있음을 다시 확인했다.
- 따라서 `createProject` 유스케이스의 엔티티 생성 로직은 별도 빌더나 팩토리 없이, 현재 `Project.create(...)`를 공식 생성 경로로 사용하기로 고정했다.
- `Project.create(...)` 위에 관리자 `createProject` 유스케이스의 생성 진입점이라는 설명 주석을 추가했다.
- 실제 `CreateProjectCommand -> Project.create(...)` 호출 연결은 다음 `CreateProjectService` 저장 로직 구현 단계에서 붙인다.

PROJ-05-7 진행 결과
- `project/application/service` 패키지에 `CreateProjectService`를 추가하고 `CreateProjectUseCase`를 구현했다.
- `createProject(...)` 메서드는 현재 기준으로 아래 순서로 동작한다.
  - `command == null`이면 `CommonErrorStatus.BAD_REQUEST`로 early return
  - `projectRepositoryPort.existsBySlug(command.slug())`가 `true`이면 `ProjectErrorStatus.DUPLICATE_PROJECT_SLUG`로 early return
  - `Project.create(...)`로 엔티티 생성
  - `projectRepositoryPort.save(...)`로 저장
  - 저장된 엔티티를 `CreateProjectResult`로 매핑
- 엔티티 생성(`createProjectEntity`)과 결과 변환(`toResult`)은 private 메서드로 분리해, 이후 테스트와 수정 작업에서 흐름이 바로 보이도록 정리했다.
- `publishedAt`은 request에서 받지 않고, `status == ACTIVE`면 `LocalDateTime.now()`, 아니면 `null`로 서버가 결정하도록 정리했다.
- `createdAt`, `updatedAt`는 저장된 엔티티 기준으로 응답에 담도록 연결했다.

PROJ-05-8 진행 결과
- `project/presentation/dto/request` 패키지에 `CreateProjectRequest` record를 추가했다.
- `slug`, `title`, `summary`, `serviceUrl`, `repoUrl`, `visibility`, `status`, `coverMediaAssetId`를 요청 필드로 정의했다.
- 검증 규칙은 앞서 확정한 기준대로 DTO 어노테이션으로 반영했다.
  - `slug`: `@NotBlank`, `@Size(max = 120)`, `^[a-z0-9-]+$`
  - `title`: `@NotBlank`, `@Size(max = 200)`
  - `summary`: `@Size(max = 500)`
  - `serviceUrl`, `repoUrl`: `@Size(max = 500)`, `@URL`
  - `visibility`, `status`: `@NotNull`
- `coverMediaAssetId`: `@Positive`
- `publishedAt`은 request에서 제거하고, 서버가 응답에만 내려주는 필드로 정리했다.

PROJ-05-9 진행 결과
- `project/presentation/dto/response` 패키지에 `CreateProjectResponse` record를 추가했다.
- 응답 필드는 `CreateProjectResult`와 동일한 상세 구조(`projectId`, `slug`, `title`, `summary`, `serviceUrl`, `repoUrl`, `visibility`, `status`, `coverMediaAssetId`, `publishedAt`, `createdAt`, `updatedAt`)로 고정했다.
- `CreateProjectResponse.from(CreateProjectResult result)` 정적 변환 메서드를 추가해, 이후 컨트롤러에서 application result를 바로 응답 DTO로 변환할 수 있게 했다.
- `publishedAt`은 request에는 없지만 response에는 유지해, 서버가 계산한 게시 시각을 그대로 반환하도록 정리했다.

PROJ-05-10 진행 결과
- `project/presentation/controller` 패키지에 `AdminProjectController`를 추가했다.
- `POST /api/admin/projects` 엔드포인트를 `createProject(...)` 메서드로 연결했다.
- 컨트롤러는 `@Valid CreateProjectRequest`를 받아 `CreateProjectCommand`로 변환한 뒤 `CreateProjectUseCase.createProject(...)`를 호출한다.
- 응답은 `CreateProjectResponse.from(result)`로 변환하고, 상태코드는 `@ResponseStatus(HttpStatus.CREATED)`로 `201 Created`를 반환하도록 맞췄다.
- `publishedAt`은 request에서 받지 않으므로 command 변환 시 포함하지 않고, 서버가 계산한 값을 응답에서만 내려준다.

PROJ-05-11 진행 결과
- `project/application/service` 테스트로 `CreateProjectServiceTest`를 추가했다.
- 성공 케이스에서 아래 흐름을 검증했다.
  - `existsBySlug(...)`가 `false`면 저장 흐름으로 진행
  - `save(...)`에 전달된 `Project`의 필드가 command 값과 일치
  - `ACTIVE` 상태일 때 `publishedAt`이 서버에서 채워짐
  - 저장 결과가 `CreateProjectResult`에 올바르게 매핑됨
- 저장 엔티티 검증은 `ArgumentCaptor<Project>`로, 반환 결과 검증은 AssertJ로 정리했다.

PROJ-05-12 진행 결과
- `CreateProjectServiceTest`에 실패 케이스 2건을 추가했다.
  - slug 중복 시 `ProjectErrorStatus.DUPLICATE_PROJECT_SLUG` 반환
  - 필수값(title) 검증 실패 시 `ProjectErrorStatus.INVALID_PROJECT_TITLE` 반환
- 두 케이스 모두 `save(...)`가 호출되지 않는지 같이 검증했다.
- 서비스 테스트는 아래 명령으로 다시 실행했고 통과했다.
  - `./gradlew --no-daemon test --tests com.snowk.blog.api.project.application.service.CreateProjectServiceTest`

PROJ-05-13 진행 결과
- `project/presentation/controller` 테스트로 `AdminProjectControllerTest`를 추가했다.
- 성공 케이스에서 아래 흐름을 검증했다.
  - `POST /api/admin/projects` 요청이 `201 Created`를 반환
  - 응답 본문에 `projectId`, `slug`, `title`, `visibility`, `status`, `publishedAt`, `createdAt`, `updatedAt`가 포함
  - 컨트롤러가 요청 본문을 `CreateProjectCommand`로 올바르게 변환
- controller 테스트는 `standaloneSetup + GlobalExceptionHandler + validator` 조합으로 기존 auth 컨트롤러 테스트 스타일과 맞췄다.

PROJ-05-14 진행 결과
- `AdminProjectControllerTest`에 검증/에러 케이스 2건을 추가했다.
  - 요청 본문이 유효하지 않으면 `400 Bad Request`
  - use case가 `DUPLICATE_PROJECT_SLUG` 예외를 던지면 `409 Conflict`
- 두 케이스 모두 `GlobalExceptionHandler`를 통해 표준 에러 응답(`code`, `isSuccess`)이 내려오는지 확인했다.
- 컨트롤러 테스트는 아래 명령으로 다시 실행했고 통과했다.
  - `./gradlew --no-daemon test --tests com.snowk.blog.api.project.presentation.controller.AdminProjectControllerTest`

PROJ-06-1 진행 결과
- `project/application/query` 패키지에 `GetProjectQuery` record를 추가했다.
- 관리자 상세 조회 입력은 현재 범위에서 `projectId` 하나만 필요하므로, `Long projectId` 단일 필드 구조로 고정했다.
- 이후 `GET /api/admin/projects/{projectId}` 컨트롤러는 path variable을 이 query로 변환해 `getProject` 유스케이스에 전달한다.
- 이 단계에서는 아직 조회 실패 규칙이나 응답 매핑은 넣지 않고, 조회 입력 모델만 먼저 고정했다.

PROJ-06-2 진행 결과
- `project/application/result` 패키지에 `GetProjectResult` record를 추가했다.
- 상세 조회 응답 필드는 create 응답과 동일한 상세 구조(`projectId`, `slug`, `title`, `summary`, `serviceUrl`, `repoUrl`, `visibility`, `status`, `coverMediaAssetId`, `publishedAt`, `createdAt`, `updatedAt`)로 고정했다.
- 필드 구조는 같지만, 유스케이스 의미를 분리하기 위해 `CreateProjectResult`를 재사용하지 않고 `GetProjectResult`를 별도 타입으로 두었다.
- 이후 `GetProjectResponse` DTO는 이 result를 기준으로 매핑한다.

PROJ-06-3 진행 결과
- `project/application/port/in` 패키지에 `GetProjectUseCase` 인터페이스를 추가했다.
- 메서드 시그니처는 `GetProjectResult getProject(GetProjectQuery query)`로 고정해, 이후 상세 조회 컨트롤러가 service 구현체가 아니라 조회용 in-port에 의존하도록 준비했다.
- 메서드 이름은 작업 계획과 동일하게 `getProject`로 통일했다.
- 이후 `GetProjectService` 구현체는 이 인터페이스를 구현하는 방식으로 연결한다.

PROJ-06-4 진행 결과
- 상세 조회 실패는 공통 `RESOURCE_NOT_FOUND`가 아니라 `ProjectErrorStatus.PROJECT_NOT_FOUND`로 처리하기로 확정했다.
- 이에 따라 `ProjectErrorStatus`에 `PROJECT_NOT_FOUND`(`404 Not Found`, `PROJECT4041`) 에러 코드를 추가했다.
- 이후 `getProject` 서비스 구현에서는 `projectRepositoryPort.findById(query.projectId())` 결과가 비어 있으면 `BaseException(ProjectErrorStatus.PROJECT_NOT_FOUND)`를 던지는 방식으로 연결한다.
- 이렇게 하면 관리자 상세 조회 실패도 프로젝트 도메인 맥락이 드러나는 전용 에러 코드로 응답할 수 있다.

PROJ-06-5 진행 결과
- `project/application/service` 패키지에 `GetProjectService`를 추가하고 `GetProjectUseCase`를 구현했다.
- `getProject(...)` 메서드는 현재 기준으로 아래 순서로 동작한다.
  - `query == null` 또는 `query.projectId() == null`이면 `CommonErrorStatus.BAD_REQUEST`
  - `projectRepositoryPort.findById(query.projectId())`로 조회
  - 결과가 없으면 `ProjectErrorStatus.PROJECT_NOT_FOUND`
  - 조회 성공 시 `GetProjectResult`로 매핑
- 결과 매핑은 `toResult(...)` private 메서드로 분리해, 이후 테스트와 응답 DTO 구현에서 흐름이 바로 보이도록 정리했다.

PROJ-06-6 진행 결과
- `project/presentation/dto/response` 패키지에 `GetProjectResponse` record를 추가했다.
- 응답 필드는 `GetProjectResult`와 동일한 상세 구조(`projectId`, `slug`, `title`, `summary`, `serviceUrl`, `repoUrl`, `visibility`, `status`, `coverMediaAssetId`, `publishedAt`, `createdAt`, `updatedAt`)로 고정했다.
- `GetProjectResponse.from(GetProjectResult result)` 정적 변환 메서드를 추가해, 이후 상세 조회 컨트롤러에서 application result를 바로 응답 DTO로 변환할 수 있게 했다.
- create 응답과 필드 구조는 같지만, 상세 조회 유스케이스 의미를 분리하기 위해 response 타입은 별도로 유지한다.

PROJ-06-7 진행 결과
- `AdminProjectController`에 `GET /api/admin/projects/{projectId}` 엔드포인트를 추가했다.
- 컨트롤러는 path variable `projectId`를 `GetProjectQuery`로 변환한 뒤 `GetProjectUseCase.getProject(...)`를 호출한다.
- 응답은 `GetProjectResponse.from(result)`로 변환해 반환하도록 연결했다.
- 상세 조회는 성공 시 기본 `200 OK`를 사용하고, 별도 `ResponseEntity` 없이 DTO 직접 반환으로 유지했다.

PROJ-06-8 진행 결과
- `project/application/service` 테스트로 `GetProjectServiceTest`를 추가했다.
- 아래 흐름을 단위 테스트로 검증했다.
  - 정상 조회 시 `GetProjectResult`에 상세 필드가 올바르게 매핑됨
  - 대상 프로젝트가 없으면 `ProjectErrorStatus.PROJECT_NOT_FOUND`
  - query 자체가 없거나 `projectId`가 없으면 `CommonErrorStatus.BAD_REQUEST`
- 서비스 테스트는 AssertJ 기준으로 정리했고, repository 조회는 `findById(...)` 호출 여부까지 같이 확인했다.

PROJ-06-9 진행 결과
- `AdminProjectControllerTest`에 상세 조회 케이스 2건을 추가했다.
  - 성공 시 `GET /api/admin/projects/{projectId}`가 `200 OK`와 상세 응답 본문을 반환
  - 대상이 없으면 `404 Not Found`와 `PROJECT4041` 에러 응답을 반환
- 성공 케이스에서는 path variable이 `GetProjectQuery`로 올바르게 변환되는지도 `ArgumentCaptor<GetProjectQuery>`로 검증했다.
- 컨트롤러 테스트는 create 테스트와 같은 `standaloneSetup + GlobalExceptionHandler + validator` 스타일을 유지했다.

PROJ-06-10 진행 결과
- `project/application/query` 패키지에 `ListProjectsQuery` record를 추가했다.
- 현재 관리자 목록 조회 범위에서는 정렬/필터/페이지 입력을 아직 확정하지 않았으므로, 우선 빈 query 구조로 시작한다.
- 이후 `PROJ-06-13 listProjects 정렬/반환 범위 규칙 확정` 단계에서 정렬 기준이나 페이지 파라미터가 필요해지면 이 query에 필드를 추가한다.
- 지금 단계의 목적은 목록 조회도 create/get과 같은 방식으로 별도 query 타입을 가지도록 구조를 맞추는 것이다.

PROJ-06-11 진행 결과
- `project/application/result` 패키지에 `ListProjectsResult` record를 추가했다.
- 목록 응답 구조는 `items + totalCount`로 고정했고, item 필드는 아래 요약 필드로 맞췄다.
  - `projectId`
  - `slug`
  - `title`
  - `visibility`
  - `status`
  - `publishedAt`
  - `createdAt`
  - `updatedAt`
- item 구조는 `ListProjectsResult.Item` 중첩 record로 정의해, 목록 응답 전용 요약 모델이라는 점이 바로 보이도록 정리했다.

PROJ-06-12 진행 결과
- `project/application/port/in` 패키지에 `ListProjectsUseCase` 인터페이스를 추가했다.
- 메서드 시그니처는 `ListProjectsResult listProjects(ListProjectsQuery query)`로 고정해, 이후 목록 조회 컨트롤러가 service 구현체가 아니라 조회용 in-port에 의존하도록 준비했다.
- `Pageable` 같은 Spring Data 타입은 use case에 직접 노출하지 않고, 목록 조회도 query/result 타입으로 감싸는 방향을 유지했다.
- 이후 `ListProjectsService` 구현체는 이 인터페이스를 구현하는 방식으로 연결한다.

PROJ-06-13 진행 결과
- 관리자 목록 조회는 `visibility`, `status`로 숨기지 않고 전체 프로젝트를 반환하기로 확정했다.
- 정렬은 관리자 사용성을 기준으로 `updatedAt DESC`를 기본으로 하고, 같은 시각이면 `projectId DESC`를 tie-breaker로 사용하기로 정했다.
- 현재 범위에서는 페이징, 검색, 필터를 넣지 않고 전체 목록 반환으로 시작한다.
- `ListProjectsResult.totalCount`는 현재 반환된 목록 크기와 동일하게 `items.size()` 기준으로 계산한다.
- 이후 목록 조회 구현은 이 규칙에 맞춰 repository 결과를 정렬/매핑하도록 진행한다.

PROJ-06-14 진행 결과
- `project/application/service` 패키지에 `ListProjectsService`를 추가하고 `ListProjectsUseCase`를 구현했다.
- `listProjects(...)` 메서드는 현재 기준으로 아래 순서로 동작한다.
  - `query == null`이면 `CommonErrorStatus.BAD_REQUEST`
  - `projectRepositoryPort.findAll()`로 전체 프로젝트 조회
  - `updatedAt DESC`, tie-breaker `projectId DESC` 기준으로 정렬
  - 각 엔티티를 `ListProjectsResult.Item`으로 매핑
- `items.size()`를 `totalCount`로 채워 반환
- 목록 item 매핑은 `toItem(...)` private 메서드로 분리해, 이후 응답 DTO와 테스트에서 흐름이 바로 보이도록 정리했다.

PROJ-06-15 진행 결과
- `project/presentation/dto/response` 패키지에 `ListProjectsResponse` record를 추가했다.
- application 계층의 `ListProjectsResult`는 프레임워크 중립성을 위해 `items`를 유지하고, web 응답에서는 추후 page 구조와 자연스럽게 맞출 수 있도록 `content + totalCount` 형태로 매핑했다.
- `ListProjectsResponse.from(ListProjectsResult result)` 정적 변환 메서드를 추가해, `result.items()`를 response `content`로 변환하도록 정리했다.
- response item 구조도 `ListProjectsResponse.Item` 중첩 record로 분리해, 목록 응답 전용 모델이라는 점이 바로 보이게 했다.

PROJ-06-16 진행 결과
- `AdminProjectController`에 `GET /api/admin/projects` 엔드포인트를 추가했다.
- 컨트롤러는 현재 범위 기준으로 빈 `ListProjectsQuery`를 생성해 `ListProjectsUseCase.listProjects(...)`를 호출한다.
- 응답은 `ListProjectsResponse.from(result)`로 변환해 반환하도록 연결했다.
- 목록 조회도 성공 시 기본 `200 OK`를 사용하고, 별도 `ResponseEntity` 없이 DTO 직접 반환으로 유지했다.

PROJ-06-17 진행 결과
- `project/application/service` 테스트로 `ListProjectsServiceTest`를 추가했다.
- 성공 케이스에서 아래 흐름을 검증했다.
  - `findAll()` 결과가 `updatedAt DESC`, tie-breaker `projectId DESC` 기준으로 정렬됨
  - 정렬된 엔티티가 `ListProjectsResult.Item` 목록으로 매핑됨
  - `totalCount`가 현재 반환 항목 수와 동일하게 계산됨
- 실패 케이스에서는 `query == null`이면 `CommonErrorStatus.BAD_REQUEST`가 발생하고, 이때 `findAll()`이 호출되지 않는지도 확인했다.

PROJ-06-18 진행 결과
- `AdminProjectControllerTest`에 목록 조회 성공 케이스를 추가했다.
- `GET /api/admin/projects` 요청이 `200 OK`와 `content + totalCount` 응답 구조를 반환하는지 검증했다.
- 응답 본문에서 `content[0]`, `content[1]` 순서와 `totalCount` 값이 기대한 목록 결과와 일치하는지 확인했다.
- 컨트롤러가 내부적으로 빈 `ListProjectsQuery`를 만들어 use case에 전달하는지도 `ArgumentCaptor<ListProjectsQuery>`로 검증했다.

PROJ-07-1 진행 결과
- `project/application/command` 패키지에 `UpdateProjectCommand` record를 추가했다.
- `projectId`는 path variable 입력을 반영하기 위해 command에 포함했고, 나머지 필드는 create와 같은 수정 본문 구조(`slug`, `title`, `summary`, `serviceUrl`, `repoUrl`, `visibility`, `status`, `coverMediaAssetId`)로 맞췄다.
- `publishedAt`은 수정 요청에서도 받지 않으므로 command 필드에 포함하지 않았다.
- 이후 `updateProject` 컨트롤러는 path variable과 request body를 합쳐 이 command를 만들게 된다.

PROJ-07-2 진행 결과
- `project/application/result` 패키지에 `UpdateProjectResult` record를 추가했다.
- 수정 성공 응답은 create/get과 같은 상세 필드 구조(`projectId`, `slug`, `title`, `summary`, `serviceUrl`, `repoUrl`, `visibility`, `status`, `coverMediaAssetId`, `publishedAt`, `createdAt`, `updatedAt`)로 고정했다.
- 필드 구조는 같지만, 수정 유스케이스 의미를 분리하기 위해 `CreateProjectResult`, `GetProjectResult`를 재사용하지 않고 `UpdateProjectResult`를 별도 타입으로 두었다.
- 이후 `UpdateProjectResponse` DTO는 이 result를 기준으로 매핑한다.

PROJ-07-3 진행 결과
- `project/application/port/in` 패키지에 `UpdateProjectUseCase` 인터페이스를 추가했다.
- 메서드 시그니처는 `UpdateProjectResult updateProject(UpdateProjectCommand command)`로 고정해, 이후 수정 컨트롤러가 service 구현체가 아니라 수정용 in-port에 의존하도록 준비했다.
- 메서드 이름은 작업 계획과 동일하게 `updateProject`로 통일했다.
- 이후 `UpdateProjectService` 구현체는 이 인터페이스를 구현하는 방식으로 연결한다.

PROJ-07-4 진행 결과
- `updateProject`의 수정 가능 필드는 create와 동일한 본문 필드로 확정한다.
  - `slug`
  - `title`
  - `summary`
  - `serviceUrl`
  - `repoUrl`
  - `visibility`
  - `status`
  - `coverMediaAssetId`
- `projectId`는 계속 path variable로만 받고, request body에는 포함하지 않는다.
- `publishedAt`, `createdAt`, `updatedAt`, `viewCount`, `likeCount`는 서버 관리 필드로 유지하고 수정 요청에서는 받지 않는다.
- `publishedAt`은 수정 시에도 request에서 직접 받지 않고, 이후 서비스 단계에서 상태 변화에 따라 유지하거나 조정하는 방식으로 처리한다.

PROJ-07-5 진행 결과
- `project/application/service` 패키지에 `UpdateProjectService`를 추가하고 `UpdateProjectUseCase`를 구현했다.
- 이번 단계에서는 수정 흐름의 첫 부분만 붙여, `command == null` 또는 `projectId == null`이면 `CommonErrorStatus.BAD_REQUEST`, `findById(projectId)` 결과가 없으면 `ProjectErrorStatus.PROJECT_NOT_FOUND`를 반환하도록 정리했다.
- 기존 프로젝트 조회는 `findProject(...)` private 메서드로 분리해, 이후 slug 중복 검증과 `Project.update(...)` 연결 전에 재사용할 수 있게 했다.
- 결과 변환도 `toResult(...)` private 메서드로 분리해 두었고, 실제 수정/저장 로직은 다음 단계에서 이어서 붙인다.

PROJ-07-6 진행 결과
- `UpdateProjectService`에 slug 중복 검증을 추가했다.
- 검증 기준은 아래로 고정했다.
  - 요청 slug가 현재 프로젝트의 slug와 같으면 그대로 통과
  - 다른 slug로 바꾸는 경우 `findBySlug(slug)`로 기존 프로젝트를 조회
- 같은 slug의 다른 프로젝트가 있으면 `ProjectErrorStatus.DUPLICATE_PROJECT_SLUG` 반환
- 중복 검증은 `validateDuplicateSlug(...)` private 메서드로 분리해, 이후 실제 수정/저장 로직 앞에서 재사용할 수 있게 정리했다.

PROJ-07-7 진행 결과
- `UpdateProjectService`에 기존 프로젝트 엔티티를 실제로 수정하는 흐름을 추가했다.
- 조회와 slug 중복 검증이 끝난 뒤 `Project.update(...)`를 호출해 command 값을 엔티티에 반영하도록 연결했다.
- `publishedAt`은 request에서 받지 않으므로, 상태 기준으로 아래 규칙을 적용한다.
  - 수정 후 상태가 `ACTIVE`이고 기존 `publishedAt`이 있으면 그대로 유지
  - 수정 후 상태가 `ACTIVE`인데 기존 `publishedAt`이 없으면 `LocalDateTime.now()`로 설정
  - 수정 후 상태가 `INACTIVE`면 `null`
- 이 규칙은 현재 엔티티 상태를 함께 보므로 `Project.update(...)` 내부의 `resolvePublishedAt(...)` private 메서드로 옮겨, 서비스가 아니라 엔티티가 게시 시각 변경 규칙을 직접 관리하도록 정리했다.

PROJ-07-8 진행 결과
- 현재 수정 저장은 `save(...)`를 명시적으로 다시 호출하지 않고, JPA dirty checking 기준으로 처리하기로 확정했다.
- 근거는 `UpdateProjectService`가 `@Transactional`이고, `findById(...)`로 조회한 `Project`가 같은 영속성 컨텍스트의 관리 엔티티이기 때문이다.
- 따라서 `Project.update(...)`로 상태를 변경한 뒤 트랜잭션이 종료되면 변경 내용이 자동 반영된다.
- 이 단계에서는 별도 `save(...)` 없이 dirty checking 기준으로 수정이 반영되는 구조를 유지한다.

PROJ-07-9 진행 결과
- `project/presentation/dto/request` 패키지에 `UpdateProjectRequest` record를 추가했다.
- 수정 요청은 create와 같은 본문 구조를 사용하되, `projectId`는 path variable로만 받고 `publishedAt`은 서버 관리 값이므로 DTO에서 제외했다.
- 검증 규칙은 create와 동일하게 유지했다.
  - `slug`: `@NotBlank`, `@Size(max = 120)`, `^[a-z0-9-]+$`
  - `title`: `@NotBlank`, `@Size(max = 200)`
  - `summary`: `@Size(max = 500)`
- `serviceUrl`, `repoUrl`: `@Size(max = 500)`, `@URL`
- `visibility`, `status`: `@NotNull`
- `coverMediaAssetId`: `@Positive`

PROJ-07-10 진행 결과
- `project/presentation/dto/response` 패키지에 `UpdateProjectResponse` record를 추가했다.
- 수정 성공 응답은 `UpdateProjectResult`와 동일한 상세 필드 구조(`projectId`, `slug`, `title`, `summary`, `serviceUrl`, `repoUrl`, `visibility`, `status`, `coverMediaAssetId`, `publishedAt`, `createdAt`, `updatedAt`)로 고정했다.
- `UpdateProjectResponse.from(UpdateProjectResult result)` 정적 변환 메서드를 추가해, 이후 수정 컨트롤러에서 application result를 바로 응답 DTO로 변환할 수 있게 했다.
- create/get과 필드 구조는 같더라도 수정 유스케이스 의미를 분리하기 위해 response 타입은 별도로 유지한다.

PROJ-07-11 진행 결과
- `AdminProjectController`에 `PUT /api/admin/projects/{projectId}` 엔드포인트를 추가했다.
- 컨트롤러는 path variable `projectId`와 `@Valid UpdateProjectRequest`를 합쳐 `UpdateProjectCommand`를 만든 뒤 `UpdateProjectUseCase.updateProject(...)`를 호출한다.
- 응답은 `UpdateProjectResponse.from(result)`로 변환해 반환하도록 연결했다.
- 이번 단계에서는 계획대로 `PATCH`가 아니라 `PUT` 기준으로 고정해 전체 수정 요청 흐름을 사용한다.

PROJ-07-12 진행 결과
- `project/application/service` 테스트로 `UpdateProjectServiceTest`를 추가했다.
- 아래 성공 흐름을 단위 테스트로 검증했다.
  - 기존 프로젝트 조회 후 수정 필드가 엔티티에 반영됨
  - 같은 slug 수정 경로에서는 `findBySlug(...)`를 다시 호출하지 않음
  - update 경로에서는 `save(...)`를 호출하지 않고 dirty checking 전제만 유지함
  - 활성 상태 유지 시 기존 `publishedAt`이 보존됨
  - 비활성 -> 활성 전환 시 `publishedAt`이 새로 채워짐
- 테스트 엔티티는 실제 `Project.create(...)`와 `ReflectionEntityFactory`를 사용해 만들어, 수정 로직과 결과 매핑을 함께 검증했다.

PROJ-07-13 진행 결과
- `UpdateProjectServiceTest`에 실패 케이스를 추가했다.
- 아래 실패 흐름을 단위 테스트로 검증했다.
  - 수정 대상 프로젝트가 없으면 `PROJECT_NOT_FOUND`
  - 다른 프로젝트가 같은 slug를 사용 중이면 `DUPLICATE_PROJECT_SLUG`
  - command 자체가 없거나 `projectId`가 없으면 `BAD_REQUEST`
- 실패 케이스에서는 `save(...)`가 호출되지 않고, 필요한 경우에만 `findById(...)`, `findBySlug(...)`가 호출되는지도 함께 확인했다.

PROJ-07-14 진행 결과
- `AdminProjectControllerTest`에 `PUT /api/admin/projects/{projectId}` 성공 케이스를 추가했다.
- 아래 흐름을 웹 계층 테스트로 검증했다.
  - 성공 시 `200 OK`와 수정 응답 본문을 반환
  - path variable `projectId`와 request body가 `UpdateProjectCommand`로 올바르게 합쳐짐
  - `visibility`, `status`, `coverMediaAssetId` 등 수정 필드가 그대로 전달됨
- 컨트롤러 테스트는 기존과 같은 `standaloneSetup + GlobalExceptionHandler + validator` 스타일을 유지했다.

PROJ-07-15 진행 결과
- `AdminProjectControllerTest`에 수정 검증/에러 케이스를 추가했다.
- 아래 흐름을 웹 계층 테스트로 검증했다.
  - 요청 본문이 유효하지 않으면 `400 Bad Request`
  - use case가 `PROJECT_NOT_FOUND`를 던지면 `404 Not Found`
  - use case가 `DUPLICATE_PROJECT_SLUG`를 던지면 `409 Conflict`
- 세 케이스 모두 `GlobalExceptionHandler` 표준 에러 응답(`code`, `isSuccess`)을 기준으로 확인했다.

PROJ-08 진행 결과
- `project/application/port/in` 패키지에 `DeleteProjectUseCase`를 추가하고, 삭제 유스케이스 시그니처를 `void deleteProject(Long projectId)`로 고정했다.
- 삭제 정책은 현재 관리자 소유 콘텐츠 기준 hard delete로 유지했다.
- 참조 무결성 영향은 초기 스키마를 다시 확인했고, 현재 `project_tags`, `post_projects`의 `project_id` FK는 모두 `ON DELETE CASCADE`라서 이번 단계의 hard delete 정책과 충돌하지 않는다.
- `DeleteProjectService`를 추가해 아래 흐름을 구현했다.
  - `projectId == null`이면 `BAD_REQUEST`
  - `findById(projectId)` 결과가 없으면 `PROJECT_NOT_FOUND`
  - 조회된 `Project`를 `projectRepositoryPort.delete(project)`로 삭제
- `AdminProjectController`에 `DELETE /api/admin/projects/{projectId}`를 추가했고, 성공 응답은 `204 No Content`로 고정했다.
- `DeleteProjectServiceTest`를 추가해 아래 케이스를 검증했다.
  - 삭제 성공 시 조회한 엔티티를 그대로 삭제
  - 대상 프로젝트가 없으면 `PROJECT_NOT_FOUND`
  - `projectId`가 없으면 `BAD_REQUEST`
- `AdminProjectControllerTest`에 삭제 케이스를 추가해 아래 흐름을 검증했다.
  - 성공 시 `204 No Content`
  - 대상 프로젝트가 없으면 `404 Not Found`
- delete 관련 검증은 아래 명령으로 다시 실행했고 통과했다.
  - `./gradlew --no-daemon test --tests com.snowk.blog.api.project.application.service.DeleteProjectServiceTest --tests com.snowk.blog.api.project.presentation.controller.AdminProjectControllerTest`

PROJ-09 진행 결과
- project 관련 기존 테스트 범위를 다시 확인했다.
  - `ProjectTagIdTest`
  - `ProjectRepositoryAdapterTest`
  - `ProjectJpaRepositoryTest`
  - project service/controller 테스트 전체
- 아래 명령으로 project 관련 테스트를 한 번에 다시 실행했고 통과했다.
  - `./gradlew --no-daemon test --tests com.snowk.blog.api.project.domain.entity.ProjectTagIdTest --tests com.snowk.blog.api.project.infrastructure.persistence.adapter.ProjectRepositoryAdapterTest --tests com.snowk.blog.api.project.infrastructure.persistence.jpa.ProjectJpaRepositoryTest --tests com.snowk.blog.api.project.application.service.CreateProjectServiceTest --tests com.snowk.blog.api.project.application.service.GetProjectServiceTest --tests com.snowk.blog.api.project.application.service.ListProjectsServiceTest --tests com.snowk.blog.api.project.application.service.UpdateProjectServiceTest --tests com.snowk.blog.api.project.application.service.DeleteProjectServiceTest --tests com.snowk.blog.api.project.presentation.controller.AdminProjectControllerTest`
- 이번 단계에서는 project 도메인/repository 기존 테스트와 새 CRUD 테스트가 충돌하지 않았고, 별도 보정이 필요하지 않았다.
- 마지막으로 `./gradlew --no-daemon test` 전체 테스트도 다시 실행했고 통과했다.

PROJ-10 진행 결과
- `README.md`에서 `P0-008-BE-3 프로젝트 CRUD(Admin)`를 완료(`[x]`)로 반영했다.
- `WORK_PROGRESS.md` 상단 상태를 현재 완료 기준으로 정리했다.
  - `P0-008-BE-3` 완료 범위
  - 최종 테스트 결과
  - 다음 작업 주제
- 다음 시작 지점은 `P0-009-BE-4 글 CRUD(Admin) + DRAFT/PUBLISHED 전환`으로 넘겼다.

다음 시작 지점
- `P0-009-BE-4 현황 확인`
- 이 단계는 글 CRUD(Admin) 시작 전 현재 `post` 도메인과 README 요구사항을 다시 맞춰보는 작업이다.
- 즉 다음 컨텍스트에서는 `README의 P0-009-BE-4 범위 확인 -> post 구조 확인 -> 세부 단계 수립` 순서로 시작하면 된다.

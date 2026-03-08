# Blog Work Progress

이 파일은 저장소에 유지하는 작업 기록이다.
- 작업을 시작하거나 끝낼 때마다 현재 단계와 다음 단계를 갱신한다.
- 구현은 한 번에 하나의 단계만 진행한다.

현재 작업 주제
- `P0-008-BE-3 프로젝트 CRUD(Admin)`

현재 확정 범위
- `P0-007-BE-2 관리자 로그인 API + ADMIN 보호구역 설정`은 완료 상태로 고정한다.
- 다음 작업은 `P0-008-BE-3 프로젝트 CRUD(Admin)`이며, 세부 단계는 시작 시 추가한다.

현재 완료 상태
- `P0-007-BE-2 관리자 로그인 API + ADMIN 보호구역 설정`은 구현과 테스트까지 완료했다.
- 완료 범위: `/api/admin/auth/login`, JWT 발급/검증, `/api/admin/** -> ADMIN` 인가 규칙, 서비스/컨트롤러/인가 테스트
- 최종 확인: `blog/apps/api`에서 `./gradlew --no-daemon test` 통과

세부 단계
- [x] SEC-00 직전 일괄 보안 변경 롤백 후 순차 진행 방식으로 전환
- [x] SEC-01 `s-nowk/global` 보안 설정과 현재 `blog/apps/api` 구조 차이 확인
- [x] SEC-02 블로그 인증 범위 확정: 관리자 로그인만 먼저 처리할지, 일반 사용자 인증까지 포함할지 결정
- [x] SEC-03 Security/CORS/JWT 관련 프로퍼티 키 설계
- [x] SEC-04 토큰 발급 방식 및 인증 필터/리소스 서버 방식 확정
- [x] SEC-05 관리자 로그인 API 구현
  - [x] SEC-05-1 Security/CORS 프로퍼티 구조를 설계안대로 실제 코드/YAML에 반영
  - [x] SEC-05-2 JWT 발급 설정(`JwtProperties`, `JwtEncoder`) 추가
  - [x] SEC-05-3 관리자 로그인 서비스 구현(사용자 조회 + 비밀번호 검증 + access token 발급)
  - [x] SEC-05-4 관리자 로그인 컨트롤러/DTO 구현
  - [x] SEC-05-5 컴파일 확인 및 단계 상태 갱신
- [x] SEC-06 `/api/admin/**` 보호구역 적용
  - [x] SEC-06-1 `JwtDecoder` 추가 및 `security.jwt.secret-key` 기반 검증 설정
  - [x] SEC-06-2 JWT claim(`roles`) -> Spring Security 권한 변환기 추가
  - [x] SEC-06-3 `oauth2ResourceServer().jwt()` 활성화
  - [x] SEC-06-4 `/api/admin/auth/login` 공개 유지, `/api/admin/**`는 `ADMIN` 권한 요구로 인가 규칙 명시
  - [x] SEC-06-5 컴파일 확인 및 단계 상태 갱신
- [x] SEC-07 인증/인가 테스트 작성 및 통과 확인
  - [x] SEC-07-1 관리자 로그인 성공/실패 서비스 테스트 작성
  - [x] SEC-07-2 로그인 API 웹 계층 테스트 작성
  - [x] SEC-07-3 관리자 보호구역 인가 테스트 작성
  - [x] SEC-07-4 테스트 실행 및 실패 원인 정리/보정
- [x] SEC-08 README 체크리스트와 진행 상태 갱신
  - [x] SEC-08-1 README의 `P0-007-BE-2` 상태 재판정
  - [x] SEC-08-2 `WORK_PROGRESS.md` 완료 상태 정리
  - [x] SEC-08-3 다음 시작 지점 갱신

SEC-01 비교 결과
- 공통점: `security.all.request-matchers`, `cors.allow.origins` 프로퍼티 구조는 `global`과 `blog`가 동일한 방향이다.
- 차이점: `global`은 OAuth2 로그인 중심 설정이고 `blog`는 세션 없는 API 서버를 전제로 한 `STATELESS` 보안 베이스다.
- 차이점: `global`의 JWT는 `JWTAuthenticationFilter + JWTUtil + UserRepository` 기반 커스텀 필터 구조이고, 현재 `blog`에는 JWT 유틸/필터/인증 주체가 아직 없다.
- 차이점: `global`의 CORS는 `WebMvcConfigurer` 기반이고, `blog`는 `CorsConfigurationSource` Bean 기반이다.
- 결론: `blog`는 `global`의 프로퍼티 구조와 request-matcher 처리 방식은 참고하되, 인증 구현은 OAuth2 복제가 아니라 `관리자 로그인 + JWT API 인증` 용도로 별도 설계해야 한다.

SEC-02 범위 확정 결과
- 우선 구현 범위는 `관리자 로그인 + ADMIN 보호구역`만 포함한다.
- 근거: README에서 `P0-007-BE-2`는 관리자 로그인이고, 일반 사용자 인증은 `P1-001-AUTH-1`로 뒤 단계에 분리돼 있다.
- 이번 단계에서 USER 회원가입/로그인, refresh token, 일반 사용자 권한 모델은 포함하지 않는다.
- 이번 단계의 목표는 운영자가 관리자 화면에 로그인하고 `/api/admin/**`를 보호할 수 있는 최소 인증 체인을 만드는 것이다.

SEC-03 프로퍼티 키 설계 결과
- 변경: `security.all.request-matchers`는 의미가 모호하므로 `security.public.request-matchers`로 변경한다.
- 변경: CORS 설정은 별도 루트가 아니라 `security` 하위로 포함한다.
- 신규 JWT 키는 `global`의 네이밍을 최대한 따라 아래 구조로 설계한다.

예정 프로퍼티 구조
- `security.public.request-matchers`
- `security.cors.allow.origins`
- `security.jwt.issuer`
- `security.jwt.secret-key`
- `security.jwt.access-token.expiretime`

예정 YAML 형태
- `security.public.request-matchers`: 인증 없이 허용할 엔드포인트 목록
- `security.cors.allow.origins`: 허용 오리진 목록
- `security.jwt.issuer`: 발급자 식별자(미래 auth server 기준값)
- `security.jwt.secret-key`: 관리자 access token 서명 키
- `security.jwt.access-token.expiretime`: access token 만료 시간(분)

설계 메모
- `refresh token` 관련 키는 이번 범위에 포함하지 않는다.
- `header`, `prefix` 같은 키는 현재 범위에서 고정값(`Authorization`, `Bearer`)으로 처리하고 프로퍼티로 분리하지 않는다.
- `global`과의 일관성을 위해 JWT 키 이름은 `secret-key`, `access-token.expiretime` 형태를 우선 사용한다.
- `public`은 “인증 없이 공개 허용되는 경로” 의미를 직접 드러내므로 `all`보다 우선한다.
- `global`과 달리 CORS는 보안 설정과 함께 보기 쉽도록 `security.cors.*` 아래에 둔다.
- `security.jwt.issuer` 값은 `blog-api`가 아니라 미래 인증 서버 식별자 기준으로 잡는다. 예: `https://auth.s-nowk.com`
- 현재는 대칭키(`security.jwt.secret-key`) 기반으로 시작하되, 인증 서버 분리 이후에는 공개키/JWKS 기반 검증으로 전환할 수 있게 설계한다.

SEC-04 인증 방식 확정 결과
- 토큰 발급은 `Spring Security JwtEncoder` 기반으로 처리한다.
- 토큰 검증은 `oauth2ResourceServer().jwt()` 기반으로 처리한다.
- 현재 범위에서는 `HS256 + security.jwt.secret-key` 방식으로 관리자 access token만 발급한다.
- 인증 헤더는 고정값 `Authorization: Bearer <token>` 으로 사용한다.
- 관리자 권한은 토큰 claim의 `roles` 또는 동등한 권한 claim에서 `ROLE_ADMIN`으로 판별한다.

선택 이유
- 현재 `blog`에는 이미 `spring-boot-starter-security-oauth2-resource-server`가 있어 리소스 서버 방식과 자연스럽게 맞는다.
- `global`의 `JWTAuthenticationFilter + JWTUtil` 커스텀 방식보다 코드량이 적고, Spring Security 표준 흐름에 더 가깝다.
- 나중에 인증 서버를 분리할 때도 Bearer 토큰 구조는 유지한 채, 발급자와 검증 키만 바꾸기 쉽다.

미래 확장 방향
- 지금: `JwtEncoder/JwtDecoder + HS256` 로 시작
- 나중: auth server 발급 + resource server 검증 구조로 전환
- 전환 시 유지할 것: `issuer`, `Authorization: Bearer`, 역할 claim 해석 방식
- 전환 시 바뀔 것: 로컬 `secret-key` 검증 -> 공개키/JWKS 검증

SEC-05-2 진행 결과
- `security.jwt.issuer`, `security.jwt.secret-key`, `security.jwt.access-token.expiretime` 프로퍼티를 실제 `application.yaml`/`application-test.yaml`에 반영했다.
- `JwtProperties`를 추가해 JWT 설정을 타입 안전하게 주입받도록 구성했다.
- `JwtConfig`를 추가해 `HS256` 대칭키 기반 `JwtEncoder` Bean을 등록했다.
- 이후 정리로 `JwtProperties`의 자바 기본값을 제거하고, 실제 값의 소스는 YAML/환경변수로 통일했다.
- 진행 편의를 위해 `application.yaml`에는 `BLOG_JWT_ISSUER`, `BLOG_JWT_SECRET_KEY`의 fallback 값을 다시 두고 사용한다.
- 이 단계에서는 토큰 검증용 `JwtDecoder`, 로그인 서비스, 컨트롤러는 아직 추가하지 않았다.
- `./gradlew --no-daemon compileJava` 통과를 확인했다.

SEC-05-3 진행 결과
- `AdminLoginService`를 추가해 사용자 조회, ADMIN 권한 확인, 비밀번호 해시 검증, access token 발급을 하나의 서비스로 구성했다.
- 로그인 실패는 공통 401 응답으로 처리할 수 있도록 `AuthErrorStatus.ADMIN_LOGIN_FAILED`를 추가했다.
- 비밀번호 비교를 위해 `BCryptPasswordEncoder` Bean을 `SecurityConfig`에 추가했다.
- 토큰 claim에는 `iss`, `sub`, `username`, `roles`, `iat`, `exp`를 포함하도록 구성했다.
- 이 단계에서는 HTTP 요청/응답 DTO와 `/api/admin/auth/login` 컨트롤러는 아직 추가하지 않았다.
- `./gradlew --no-daemon compileJava` 통과를 확인했다.

SEC-05-4 진행 결과
- `application` 입력 모델을 `AdminLoginCommand`로 분리해 `Command + Result` 구조를 맞췄다.
- `presentation` 계층에 `AdminLoginRequest`, `AdminLoginResponse`, `AdminAuthController`를 추가했다.
- 로그인 엔드포인트는 `POST /api/admin/auth/login`으로 고정했다.
- 로그인 API가 실제로 열릴 수 있도록 `security.public.request-matchers`에 `/api/admin/auth/login:POST`를 추가했다.
- `AdminLoginService`는 더 이상 primitive 인자를 직접 받지 않고 `AdminLoginCommand`를 입력으로 받는다.
- 이후 정리로 `application/port/in/AdminLoginUseCase`를 추가하고, 컨트롤러는 구현체가 아니라 유스케이스 포트를 주입받도록 변경했다.
- `./gradlew --no-daemon compileJava` 통과를 확인했다.

SEC-06-1 진행 결과
- `JwtConfig`에 `JwtDecoder` Bean을 추가했다.
- 검증 키는 현재 발급과 동일하게 `security.jwt.secret-key`를 사용한다.
- 검증 알고리즘은 `HS256`으로 고정했다.
- 이 단계에서는 아직 `SecurityConfig`에 리소스 서버를 연결하지 않았고, 인가 규칙도 바꾸지 않았다.
- `./gradlew --no-daemon compileJava` 통과를 확인했다.

SEC-06-2 진행 결과
- `SecurityConfig`에 `JwtAuthenticationConverter` Bean을 추가했다.
- JWT의 `roles` claim 값을 `SimpleGrantedAuthority`로 변환하도록 구성했다.
- 현재 토큰의 `roles=["ROLE_ADMIN"]` 값을 Spring Security 권한으로 그대로 해석할 수 있는 상태가 됐다.
- 이 단계에서는 아직 `oauth2ResourceServer().jwt()`를 연결하지 않았고, `/api/admin/**` 인가 규칙도 변경하지 않았다.
- `./gradlew --no-daemon compileJava` 통과를 확인했다.

SEC-06-3 진행 결과
- `SecurityFilterChain`에 `oauth2ResourceServer().jwt()`를 연결했다.
- 이제 `Authorization: Bearer <token>` 헤더가 들어오면 Spring Security가 `JwtDecoder`로 검증하고 `JwtAuthenticationConverter`로 인증 객체를 구성한다.
- 이 단계에서는 아직 `/api/admin/**`를 `hasRole("ADMIN")`으로 제한하지 않았고, 현재는 기존처럼 인증 여부만 요구하는 상태다.
- `./gradlew --no-daemon compileJava` 통과를 확인했다.

SEC-06-4 진행 결과
- 로그인 엔드포인트는 기존 `security.public.request-matchers` 설정으로 그대로 공개 상태를 유지했다.
- `SecurityConfig`에 `/api/admin/** -> hasRole("ADMIN")` 인가 규칙을 추가했다.
- 현재 access token의 `roles=["ROLE_ADMIN"]` claim이 권한으로 해석되므로, 관리자 토큰만 admin API에 접근할 수 있는 조건이 갖춰졌다.
- 이 단계에서는 `SEC-06` 전체 완료 처리는 아직 하지 않았고, 마지막 상태 정리(`SEC-06-5`)만 남았다.

SEC-06-5 진행 결과
- `SEC-06-1`부터 `SEC-06-4`까지의 보안 체인이 현재 코드 기준으로 정상 연결됐는지 다시 컴파일로 확인했다.
- `./gradlew --no-daemon compileJava` 결과 `UP-TO-DATE`로 통과했다.
- 현재 상태는 `로그인 API 공개 + Bearer 토큰 검증 + roles 권한 변환 + /api/admin/** ADMIN 인가`까지 연결된 상태다.
- `SEC-06` 전체를 완료 처리하고 다음 시작 지점을 테스트 단계로 넘긴다.

SEC-07-1 진행 결과
- `AdminLoginServiceTest`를 추가해 관리자 로그인 서비스의 성공/실패 케이스를 분리해서 검증했다.
- 성공 케이스에서는 access token 반환과 JWT claim(`issuer`, `subject`, `username`, `roles`) 구성을 확인했다.
- 실패 케이스는 `사용자 없음`, `ADMIN 권한 아님`, `비밀번호 불일치` 3가지를 검증했다.
- 서비스 테스트만 대상으로 `./gradlew --no-daemon test --tests com.snowk.blog.api.auth.application.service.AdminLoginServiceTest` 를 실행해 통과를 확인했다.

SEC-07-2 진행 결과
- `AdminAuthControllerTest`를 추가해 `/api/admin/auth/login`의 웹 계층 요청/응답 매핑을 검증했다.
- 성공 응답(200), 잘못된 요청 본문(400), 인증 실패 응답(401) 3가지를 확인했다.
- `AdminLoginUseCase`는 mock으로 두고, 컨트롤러와 `GlobalExceptionHandler`를 `standalone MockMvc`로 연결했다.
- 테스트 과정에서 JSON 메시지 변환에 필요한 Jackson 의존성이 누락된 것을 확인해 `jackson-databind`, `jackson-datatype-jsr310`를 `build.gradle`에 명시적으로 추가했다.
- 컨트롤러 테스트만 대상으로 `./gradlew --no-daemon test --tests com.snowk.blog.api.auth.presentation.controller.AdminAuthControllerTest` 를 실행해 통과를 확인했다.

SEC-07-3 진행 결과
- `AdminAuthorizationIntegrationTest`를 추가해 `/api/admin/**` 보호구역의 인가 규칙을 전체 보안 체인 기준으로 검증했다.
- 테스트 전용 `GET /api/admin/test/ping` 엔드포인트를 `@Import`로 등록해, 실제 admin 라우팅 규칙만 분리해서 확인할 수 있게 했다.
- 토큰 없는 요청은 401, `ROLE_USER` 토큰은 403, `ROLE_ADMIN` 토큰은 200을 반환하는지 확인했다.
- 테스트용 access token은 `JwtEncoder`와 `security.jwt.*` 테스트 설정을 사용해 실제 Bearer 토큰 형식으로 발급했다.
- 인가 테스트만 대상으로 `./gradlew --no-daemon test --tests com.snowk.blog.api.global.config.security.AdminAuthorizationIntegrationTest` 를 실행해 통과를 확인했다.

SEC-07-4 진행 결과
- `blog/apps/api` 모듈 전체 기준으로 `./gradlew --no-daemon test` 를 실행해 현재 인증/인가 변경이 기존 테스트와 함께도 안정적으로 통과하는지 확인했다.
- 이번 실행에서는 추가 보정이 필요한 실패 케이스가 나오지 않았고, 전체 결과는 `BUILD SUCCESSFUL` 이었다.
- 이 단계로 `SEC-07` 전체를 완료 처리하고, 다음 작업은 README 상태 갱신 단계로 넘긴다.

SEC-08-1 진행 결과
- README의 `P0-007-BE-2 관리자 로그인 API + ADMIN 보호구역 설정` 항목을 현재 구현 상태와 다시 대조했다.
- 근거: `/api/admin/auth/login` 컨트롤러 구현, JWT 발급/검증 체인 연결, `/api/admin/** -> hasRole("ADMIN")` 인가 규칙 적용, 서비스/컨트롤러/인가 테스트 전체 통과.
- 위 기준으로 `P0-007-BE-2`는 완료 상태로 보는 것이 맞아 README 체크박스를 `[x]`로 갱신했다.

SEC-08-2 진행 결과
- `WORK_PROGRESS.md` 상단에 현재 완료 범위를 바로 확인할 수 있는 `현재 완료 상태` 섹션을 추가했다.
- 이 섹션에는 완료된 작업명, 실제 완료 범위, 마지막 전체 테스트 확인 결과를 요약했다.
- 다음 컨텍스트에서도 파일 상단만 읽으면 `P0-007-BE-2`가 끝난 상태라는 점을 바로 파악할 수 있게 정리했다.

SEC-08-3 진행 결과
- `SEC-08` 단계가 끝난 시점을 기준으로 `WORK_PROGRESS.md` 상단의 `현재 작업 주제`를 다음 백엔드 작업인 `P0-008-BE-3 프로젝트 CRUD(Admin)`으로 갱신했다.
- 기존 인증 작업의 세부 이력은 그대로 남기고, 상단 `현재 확정 범위`에는 `P0-007-BE-2`가 완료 상태라는 점과 다음 작업은 `P0-008-BE-3`부터 이어간다는 점만 요약했다.
- 이 단계로 `SEC-08` 전체를 완료 처리하고, 다음 컨텍스트의 시작 지점을 `P0-008-BE-3 현황 확인`으로 넘긴다.

다음 시작 지점
- `P0-008-BE-3 현황 확인`

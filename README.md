# s-nowk blog 플랫폼

Next.js(SSR) + Spring API + MySQL(블로그/미디어 분리) + Docker 배포 + Cloudflare DNS(필요 시 Tunnel) + Nginx 라우팅으로 **검색 유입(SEO)과 프로젝트 체험(랜딩)**을 동시에 만족하는 개인 블로그/프로젝트 플랫폼.

## 한 줄 요약
검색 유입과 프로젝트 체험을 동시에 만족하는 개인 블로그/프로젝트 플랫폼을 만든다.

## 목표와 성공 기준
목표
- 개발 성장 기록(70%) + C 가이드/튜토리얼(30%)
- 프로젝트 홍보 및 “직접 써보기” 경험 제공
- SSR 기반 검색 최적화(SEO)
- 운영 확장: 댓글/구독/알림/AI 연관글/자동 업로드(후순위)

사용자
- 불특정 다수(비로그인)
- 운영자(관리자)

성공 기준
- 검색 유입이 발생하고, 홈에서 프로젝트를 둘러본 뒤 프로젝트/배포 서비스로 이동(CTA)
- 운영자가 글 작성 → 발행 → 이미지 업로드 → 공개 노출까지 한 번에 처리 가능
- 페이지가 공유(OG) / 검색(sitemap)에서 정상적으로 수집

## 아키텍처 요약
런타임 구성
- Frontend: Next.js (SSR/SSG 혼합)
- Backend: Spring Boot (REST API, Spring Security + JWT)
- DB: MySQL
- blog_db: posts/projects/tags/users/매핑 테이블
- media_db: media_assets (메타데이터)
- Reverse Proxy: Nginx
- Infra: Docker Compose (로컬/홈서버)
- DNS/CDN: Cloudflare (고정 IP 없으면 Tunnel 선택)

라우팅 전략(선택지)
- (A) 단일 도메인 + 경로 분리: /(web), /api(api), /media(assets)
- (B) 서브도메인 분리: blog.<DOMAIN>, api.<DOMAIN>, media.<DOMAIN>

기본은 A를 우선 적용, 필요 시 B로 확장.

## 핵심 설계 결정
콘텐츠/내비게이션
- 홈 화면에서 프로젝트 섹션과 블로그 섹션을 분리 노출
- 관계 방향: 글 → 프로젝트는 가능, 프로젝트 → 글은(기본) 최소화
- DB에서 `post_projects` 매핑으로 구현
- 공개/비공개: `visibility` + (글은) `status=DRAFT/PUBLISHED`

태그/카테고리
- 글 태그와 프로젝트 태그 스코프 분리: `tags.scope = POST | PROJECT`
- 카테고리 + 태그 구조: `tags.kind = CATEGORY | TAG`
- `parent_tag_id`로 트리 구성

국제화
- 현재는 한국어만: `posts.lang = 'ko'`
- 구조는 다국어 확장 가능하게 설계

ID 전략
- AUTO_INCREMENT 사용 안 함
- PK는 BIGINT + Snowflake(앱에서 생성)
- PK 컬럼명: 테이블명_id 규칙

## 현재 진행 상태 (체크리스트)

DNS
- [x] P0-001-D0 도메인/경로 전략 확정(단일 도메인 /api /media)

Infra
- [x] P0-002-INF-1 모노레포 폴더 구조 생성(apps/web, apps/api, infra)
- [x] P0-003-INF-2 docker compose 스켈레톤(web/api/db/minio/nginx)

DB
- [x] P0-004-DB-1 P0 테이블/관계 확정(posts/projects/tags/maps/media/users)
- [x] P0-005-DB-2 Flyway 도입 및 V1__init.sql 작성

Backend
- [x] P0-006-BE-1 엔티티/JPA 리포지토리 구현(P0 코어)
- [x] P0-007-BE-2 관리자 로그인 API + ADMIN 보호구역 설정
- [x] P0-008-BE-3 프로젝트 CRUD(Admin)
- [x] P0-009-BE-4 글 CRUD(Admin) + DRAFT/PUBLISHED 전환
- [x] P0-010-BE-5 공개 프로젝트 조회 API(목록/상세 slug)
- [x] P0-011-BE-6 공개 글 조회 API(목록/상세 slug)
- [x] P0-012-BE-7 QueryDSL 기반 필터/검색

Media
- [ ] P0-013-MEDIA-1 MinIO 연결 + 버킷/키 규칙 확정
- [ ] P0-014-MEDIA-2 presign 업로드 URL 발급 API + media_assets 저장

Frontend
- [ ] P0-015-FE-PUB-1 홈 UI(프로젝트/블로그 섹션 분리)
- [ ] P0-016-FE-PUB-2 프로젝트 목록/상세(서비스 랜딩 UX)
- [ ] P0-017-FE-PUB-3 글 목록(필터/검색 UI)
- [ ] P0-018-FE-PUB-4 글 상세(마크다운 렌더 + 관련 프로젝트)
- [ ] P0-019-FE-ADM-1 관리자 로그인 화면
- [ ] P0-020-FE-ADM-2 마크다운 에디터(작성/미리보기/발행)
- [ ] P0-021-FE-ADM-3 이미지 업로드(presign) + 본문 삽입

SEO
- [ ] P0-022-SEO-1 메타/OG 적용(글/프로젝트)
- [ ] P0-023-SEO-2 sitemap.xml / robots.txt 제공

Deploy
- [ ] P0-024-DEP-1 운영용 compose 분리(dev/prod) + restart/healthcheck
- [ ] P0-025-DEP-2 Nginx 라우팅(/ -> web, /api -> api, /media -> storage)

DNS
- [ ] P0-026-DNS-1 Cloudflare DNS 연결(A 레코드) + Proxy 정책 확정

Deploy
- [ ] P0-027-DEP-3 HTTPS 적용(LE 또는 Cloudflare Origin) + 리다이렉트

Ops
- [ ] P0-028-OPS-1 포트포워딩/방화벽(80/443만) + 내부 포트 차단 확인
- [ ] P0-029-OPS-2 백업 최소 적용(DB 덤프 + MinIO 볼륨)
- [ ] P0-030-E2E-1 외부 환경 E2E 재검증(작성→발행→업로드→공개)

Backend
- [ ] P1-001-AUTH-1 회원가입/로그인(일반 사용자) API + USER 권한 모델

Frontend
- [ ] P1-002-AUTH-2 프론트 로그인/회원가입 UI + 토큰/세션 처리

DB
- [ ] P1-003-REA-1 좋아요 테이블/인덱스 설계 + 마이그레이션

Backend
- [ ] P1-004-REA-2 좋아요 API 구현

Frontend
- [ ] P1-005-REA-3 좋아요 UI(글/프로젝트)

DB
- [ ] P1-006-VIEW-1 조회수 집계 스키마/정책 설계 + 마이그레이션

Backend
- [ ] P1-007-VIEW-2 조회수 수집 API/로직 구현

Frontend
- [ ] P1-008-VIEW-3 글/프로젝트 조회수 표시 + 트래킹

DB
- [ ] P1-009-CMT-1 댓글 무한뎁스 스키마 확정 + 마이그레이션

Backend
- [ ] P1-010-CMT-2 댓글 API 구현
- [ ] P1-011-CMT-3 댓글 트리 조회 최적화

Frontend
- [ ] P1-012-CMT-4 댓글 UI(무한뎁스)

Ops
- [ ] P1-013-CMT-5 스팸/남용 방지

DB
- [ ] P1-014-SUB-1 이메일 구독 테이블 + 마이그레이션

Backend
- [ ] P1-015-SUB-2 이메일 구독 API(더블 옵트인)
- [ ] P1-016-SUB-3 새 글 발행 메일 발송

DB
- [ ] P1-017-SEARCH-1 FULLTEXT 인덱스 + 마이그레이션

Backend
- [ ] P1-018-SEARCH-2 검색 API 개선
- [ ] P1-019-SEARCH-3 검색 로그 저장

Frontend
- [ ] P1-020-SEO-3 구조화 데이터 적용
- [ ] P1-021-ADS-1 AdSense 준비 페이지
- [ ] P1-022-ADS-2 AdSense 스크립트/광고 슬롯

Ops
- [ ] P1-023-OPS-1 운영 모니터링 최소
- [ ] P1-024-OPS-2 백업 자동화 강화

Frontend
- [ ] P1-025-PERF-1 성능/UX 개선

DB
- [ ] P2-001-NOTIF-1 알림 테이블/인덱스 설계 + 마이그레이션

Backend
- [ ] P2-002-NOTIF-2 도메인 이벤트 발행/구독 구조
- [ ] P2-003-NOTIF-3 알림 API 구현

Frontend
- [ ] P2-004-NOTIF-4 알림함 UI

Backend
- [ ] P2-005-EXT-1 텔레그램 봇 연동

Ops
- [ ] P2-006-EXT-2 카카오 알림 연동 조사/대안 결정

Backend
- [ ] P2-007-CHAT-1 WebSocket/STOMP 서버 설정

DB
- [ ] P2-008-CHAT-2 채팅 DB 모델 + 마이그레이션

Backend
- [ ] P2-009-CHAT-3 채팅 메시지 프로토콜/이력 API

Frontend
- [ ] P2-010-CHAT-4 실시간 채팅 UI

Backend
- [ ] P2-011-AI-1 연관 글 추천 V1(규칙 기반)
- [ ] P2-012-AI-2 연관 글 추천 V2(임베딩)

Ops
- [ ] P2-013-AI-3 추천 로그/AB 테스트

Backend
- [ ] P2-014-AUTO-1 Git 기반 자동 업로드 포맷 설계
- [ ] P2-015-AUTO-2 Git webhook 수신 + 초안 생성
- [ ] P2-016-AUTO-3 변경 감지 동기화 처리

Frontend
- [ ] P2-017-I18N-1 다국어 라우팅/언어 전환 UI

SEO
- [ ] P2-018-I18N-2 hreflang/sitemap 다국어 확장

Backend
- [ ] P2-019-SEC-1 인증 고도화(Refresh 토큰/회전/기기 관리)

Deploy
- [ ] P2-020-DEP-1 프로젝트 서브도메인 분리 옵션 적용

## 작업 진행 원칙
- 한 번에 하나의 주제만 진행한다.
- 각 단계는 `현황 확인 -> 참고 원본 확인 -> 변경 범위 기록 -> 코드 수정 -> 테스트 -> README 갱신` 순서로 진행한다.
- 앞 단계가 끝나기 전에는 다음 단계 구현으로 넘어가지 않는다.
- 새 컨텍스트에서도 이어서 작업할 수 있도록 세부 진행 내역은 `WORK_PROGRESS.md`에 기록한다.
- 삭제 정책 기본 원칙:
  관리자 소유 콘텐츠(`Project`, `Post`, `Tag` 등)는 우선 hard delete로 설계하고,
  외부 사용자 생성 데이터(`Comment`, `Reply`, 추후 사용자 활동 로그 등)는 soft delete를 우선 검토한다.

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
- `P0-016-FE-PUB-2 프로젝트 목록/상세(서비스 랜딩 UX)`

최근 완료 작업
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
- `apps/web` 공개 라우트는 현재 홈 `/`만 있고, `/projects`, `/projects/[slug]` 페이지는 아직 없다.
- `apps/web/src/entities/project/ui/project-card.tsx`는 홈 프로젝트 카드 언어로 이미 구현되어 있다.
- `apps/web/src/shared/ui`에는 `container`, `section-header`, `surface-card`, `cta-button`, `promo-slot`, `status-badge`가 있다.
- `apps/web/src/shared/lib/mock/home-data.ts`에는 프로젝트 slug/detailUrl/status/tags 기반 mock data가 있다.
- `apps/web/src/shared/config/site.ts`의 메인 내비게이션은 이미 `/projects` 링크를 가리킨다.
- `posts`, `projects`에는 `cover_media_asset_id` 컬럼이 이미 있다.
- blog 저장소는 더 이상 `media_db`나 media compose를 소유하지 않고, media 관련 source of truth는 별도 `s-nowk/media` 저장소다.

현재 확정 범위
- blog 저장소의 다음 범위는 프로젝트 공개 화면 구현으로 넘어간다.
- 이번 단계의 목표는 프로젝트 목록 화면과 프로젝트 상세 화면을 서비스 랜딩 UX 기준으로 구현하는 것이다.
- 홈에서 만든 프로젝트 카드 언어는 가능한 범위에서 재사용하되, 상세 화면은 서술형 정보 구조를 별도로 가질 수 있다.
- API 연동은 이미 가능하지만, 우선 화면 정보구조와 route 구조를 먼저 정리한 뒤 mock data 유지 또는 API 전환 지점을 정한다.

세부 단계
- [ ] FE-PROJ-01 프로젝트 공개 화면 요구사항/정보구조 정리
  - [ ] FE-PROJ-01-1 README 기준 프로젝트 목록/상세 목표 다시 확인
  - [ ] FE-PROJ-01-2 현재 `ProjectCard`/`shared/ui` 재사용 범위 확인
  - [ ] FE-PROJ-01-3 `/api/projects`, `/api/projects/{slug}` 연동 여부와 mock 기준 정리
- [ ] FE-PROJ-02 프로젝트 목록 화면 조립
  - [ ] FE-PROJ-02-1 `app/(public)/projects/page.tsx` route 및 metadata 베이스 추가
  - [ ] FE-PROJ-02-2 목록 소개 hero/CTA/섹션 헤더 구성
  - [ ] FE-PROJ-02-3 프로젝트 grid와 상태/태그/링크 흐름 조립
- [ ] FE-PROJ-03 프로젝트 상세 화면 조립
  - [ ] FE-PROJ-03-1 `app/(public)/projects/[slug]/page.tsx` route 및 not-found 흐름 추가
  - [ ] FE-PROJ-03-2 상세 hero/개요/서비스 링크/기술 정보 영역 구성
  - [ ] FE-PROJ-03-3 관련 글/목록 복귀/하단 CTA 영역 구성
- [ ] FE-PROJ-04 공통 표현 정리 및 검증
  - [ ] FE-PROJ-04-1 `ProjectCard` 재사용 범위와 상세 전용 표현 분리
  - [ ] FE-PROJ-04-2 heading hierarchy/metadata/link 구조 확인
  - [ ] FE-PROJ-04-3 `blog/apps/web`에서 `npm run lint`, `npm run build` 확인

계획 메모
- 프로젝트 화면은 홈보다 서비스 소개와 CTA 밀도를 높이고, 블로그보다 랜딩 성격을 더 분명히 가져간다.
- `cover_media_asset_id` 실제 연결 전까지는 placeholder 또는 gradient cover 영역으로 버틸 수 있어야 한다.
- 공개 API는 이미 있으므로, route 구조와 view model 책임이 먼저 흔들리지 않게 잡는 편이 맞다.

다음 시작 지점
- `FE-PROJ-01-1`
- 다음 구현은 `README.md` 기준으로 프로젝트 목록/상세 목표를 다시 확인하고 정보구조를 먼저 정리하는 것이다.

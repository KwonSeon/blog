import type { Post, PostDetail } from "@/src/entities/post";
import type { Project } from "@/src/entities/project";

export const mockProjects: Project[] = [
  {
    id: "project-stockdash",
    slug: "stockdash",
    title: "StockDash",
    description:
      "실시간 종목 흐름과 포트폴리오 변화를 빠르게 훑어볼 수 있는 개인 주식 대시보드입니다.",
    tags: ["Next.js", "Spring", "WebSocket"],
    status: "active",
    detailUrl: "/projects/stockdash",
    demoUrl: "/projects/stockdash",
    featured: true,
  },
  {
    id: "project-blog-platform",
    slug: "blog-platform",
    title: "블로그 플랫폼",
    description:
      "검색 유입과 프로젝트 체험을 함께 설계한 개인 블로그/쇼케이스 플랫폼입니다.",
    tags: ["SSR", "SEO", "Markdown"],
    status: "active",
    detailUrl: "/projects/blog-platform",
  },
  {
    id: "project-c-lab",
    slug: "c-memory-lab",
    title: "C 메모리 실험실",
    description:
      "포인터, 메모리 레이아웃, 동적 할당을 시각적으로 설명하기 위한 학습용 실험 프로젝트입니다.",
    tags: ["C", "Wasm", "Education"],
    status: "experimental",
    detailUrl: "/projects/c-memory-lab",
  },
  {
    id: "project-legacy-archive",
    slug: "legacy-blog-archive",
    title: "Legacy Blog Archive",
    description:
      "이전 블로그와 프로젝트 노트를 정리해 옮기는 아카이브 작업입니다. 새 플랫폼 구조 검증용 레퍼런스로 유지합니다.",
    tags: ["Archive", "Migration", "Jekyll"],
    status: "archived",
    detailUrl: "/projects/legacy-blog-archive",
  },
];

export const mockPostDetails: PostDetail[] = [
  {
    id: "post-c-pointer",
    slug: "understanding-c-pointers",
    title: "C 포인터를 메모리 관점으로 다시 이해하기",
    excerpt:
      "포인터를 문법이 아니라 메모리 배치와 참조 흐름으로 설명해보는 글입니다.",
    lang: "ko",
    category: "tutorial",
    tags: ["C", "포인터", "메모리"],
    publishedAt: "2026-03-01",
    readingTime: "12분",
    relatedProjectSlug: "c-memory-lab",
    relatedProjectTitle: "C 메모리 실험실",
    contentMd: `포인터를 문법 요소로만 익히면 변수 선언과 역참조 연산자만 외우게 됩니다. 이 글은 포인터를 "메모리 위치를 해석하는 규칙"으로 다시 설명하기 위한 메모입니다.

## 포인터를 다시 볼 때 먼저 확인한 것
- 포인터 값은 메모리 주소 자체보다 어떤 타입으로 해석할 것인가와 함께 읽어야 합니다.
- 역참조는 값 복사가 아니라 해당 위치의 메모리를 현재 타입 규칙으로 읽는 동작입니다.
- 배열, 함수 인자, 동적 할당을 볼 때도 결국 메모리 위치와 수명 관리 문제로 이어집니다.

## 작은 예제로 다시 보면
\`\`\`c
int value = 42;
int *ptr = &value;

printf("%d\\n", *ptr);
\`\`\`

위 코드는 \`ptr\`에 숫자 42가 들어 있는 것이 아니라, \`value\`가 저장된 위치가 들어 있다는 사실을 보여줍니다.

## 왜 프로젝트로 연결했는가
이 글에서 정리한 기준은 [C 메모리 실험실](/projects/c-memory-lab)에서 시각화 실험으로 이어집니다. 글은 개념을 설명하고, 프로젝트는 그 개념을 직접 확인하게 만드는 역할을 나눠 갖습니다.`,
  },
  {
    id: "post-blog-home",
    slug: "designing-project-first-home",
    title: "프로젝트를 먼저 보여주는 블로그 홈을 설계한 이유",
    excerpt:
      "일반적인 글 목록형 홈이 아니라 프로젝트 랜딩 중심 구조를 선택한 배경과 기준을 정리합니다.",
    lang: "ko",
    category: "devlog",
    tags: ["Next.js", "정보구조", "설계"],
    publishedAt: "2026-02-24",
    readingTime: "9분",
    relatedProjectSlug: "blog-platform",
    relatedProjectTitle: "블로그 플랫폼",
    contentMd: `홈을 글 목록으로 시작할지, 프로젝트를 먼저 보여줄지는 공개 블로그의 성격을 정하는 문제였습니다. 이 글은 그 선택을 왜 했는지 설명하는 설계 메모입니다.

## 홈에서 먼저 보여주고 싶었던 것
- 검색으로 들어온 사용자가 글만 읽고 떠나지 않도록 프로젝트 진입점을 앞단에 배치했습니다.
- 글과 프로젝트를 같은 레벨에서 보여주면 기록과 결과물을 같이 운영하는 이유가 더 선명해집니다.
- 홈은 아카이브가 아니라 랜딩 페이지에 가깝기 때문에 CTA와 탐색 흐름을 먼저 설계해야 했습니다.

## 정보구조를 나눌 때 세운 기준
> 홈은 전체 아카이브를 다 보여주는 곳이 아니라, 다음 행동을 고르게 만드는 곳이어야 한다고 봤습니다.

글, 프로젝트, 프로모 슬롯, 하단 CTA를 각각 다른 위젯으로 분리해 이후 공개 화면이 늘어나도 조립 책임이 흔들리지 않게 만들었습니다.

## 이어지는 구현
이 기준은 이후 [블로그 플랫폼](/projects/blog-platform) 상세 화면과 공개 글 상세 화면 설계로 그대로 이어집니다.`,
  },
  {
    id: "post-app-router",
    slug: "nextjs-app-router-notes",
    title: "App Router 공개 화면에서 유지한 기본 규칙",
    excerpt:
      "Server Component 우선, 링크 중심 마크업, 메타데이터 확장 가능성 같은 공개 화면 기본 원칙을 정리합니다.",
    lang: "ko",
    category: "web",
    tags: ["Next.js", "SSR", "SEO"],
    publishedAt: "2026-02-18",
    readingTime: "11분",
    contentMd: `공개 화면을 App Router로 만들 때는 기능보다 먼저 유지할 규칙을 적어두는 편이 안정적이었습니다. 이 글은 그때 남긴 기본 원칙입니다.

## 공개 화면에서 유지한 규칙
- Server Component를 기본으로 두고 상호작용이 꼭 필요한 부분만 client로 분리합니다.
- page는 조립만 담당하고, 실질적인 표현은 entity, widget, shared 레이어로 밀어냅니다.
- metadata, canonical, not-found 처리처럼 검색 유입에 직접 영향을 주는 책임은 route 단계에서 먼저 고정합니다.

## 왜 이 순서가 필요했는가
규칙이 없는 상태에서 화면을 늘리면 \`/\`, \`/projects\`, \`/posts\`가 각자 다른 문법으로 자라기 쉽습니다. 공개 화면은 일관성이 중요하므로 구현 전에 구조를 먼저 묶어두는 편이 낫습니다.

## 다음 단계에서 보는 것
공개 글 상세에서는 이 원칙 위에 markdown renderer와 관련 프로젝트 CTA를 붙여 article 중심 화면으로 확장합니다.`,
  },
  {
    id: "post-retro",
    slug: "building-in-public-retrospective",
    title: "만들면서 공개하는 방식의 장단점",
    excerpt:
      "프로젝트와 기록을 함께 운영하면서 얻은 장점과, 감수해야 했던 운영 비용을 회고합니다.",
    lang: "ko",
    category: "retrospective",
    tags: ["회고", "사이드 프로젝트"],
    publishedAt: "2026-02-10",
    readingTime: "8분",
    contentMd: `만들면서 공개하는 방식은 기록이 곧 제품 설명이 된다는 장점이 있지만, 정리 비용과 공개 압박도 함께 생깁니다. 이 글은 그 균형에 대한 짧은 회고입니다.

## 장점
- 구현 배경과 의사결정을 나중에 다시 추적하기 쉽습니다.
- 프로젝트 소개와 기술 기록이 서로 유입을 만들어줍니다.
- 완성 전 단계에서도 학습 로그를 남길 수 있습니다.

## 비용
- 문서를 유지하지 않으면 공개 기록이 곧바로 부정확한 정보가 됩니다.
- 작은 UI 변경도 설명과 스크린샷 갱신을 같이 요구합니다.
- 일정이 밀릴 때는 기록이 가장 먼저 뒤로 밀리기 쉽습니다.

## 지금의 결론
글과 프로젝트를 같은 플랫폼에서 운영한다면, 공개 화면은 기능보다 흐름을 먼저 완성해야 유지 비용을 줄일 수 있습니다.`,
  },
  {
    id: "post-spring-api",
    slug: "spring-next-blog-architecture-notes",
    title: "Spring API와 Next.js 공개 화면을 같이 설계할 때 메모한 기준",
    excerpt:
      "관리 기능과 공개 화면의 책임을 분리하면서도 SEO와 운영 흐름을 같이 유지하기 위해 정리한 설계 기준입니다.",
    lang: "ko",
    category: "devlog",
    tags: ["Spring", "Next.js", "Architecture"],
    publishedAt: "2026-02-04",
    readingTime: "14분",
    relatedProjectSlug: "blog-platform",
    relatedProjectTitle: "블로그 플랫폼",
    contentMd: `Spring API와 Next.js 공개 화면을 같이 설계할 때 가장 먼저 분리한 것은 "관리 책임"과 "공개 책임"이었습니다. 이 글은 그 기준을 정리한 아키텍처 메모입니다.

## API와 공개 화면을 같이 볼 때 필요한 분리
- 관리자 화면은 작성과 발행 흐름을 책임지고, 공개 화면은 읽기와 탐색 흐름을 책임집니다.
- API 응답은 최소 필드로 유지하되, 프론트는 view model을 통해 화면용 표현을 확장할 수 있게 둡니다.
- slug 기반 공개 URL은 SEO와 운영 편의성을 동시에 만족시키는 핵심 식별자입니다.

## 실제로 남긴 메모
\`\`\`text
public api: 목록/상세 조회 책임
admin ui: 작성/미리보기/발행 책임
web public: seo, routing, rendering 책임
\`\`\`

## 왜 이 글이 프로젝트와 연결되는가
이 기준은 [블로그 플랫폼](/projects/blog-platform)에서 공개 홈, 프로젝트 상세, 글 상세를 같은 설계 언어로 묶는 토대가 됩니다.`,
  },
];

function toMockPostSummary(post: PostDetail): Post {
  return {
    id: post.id,
    slug: post.slug,
    title: post.title,
    excerpt: post.excerpt,
    lang: post.lang,
    category: post.category,
    tags: post.tags,
    publishedAt: post.publishedAt,
    readingTime: post.readingTime,
    relatedProjectSlug: post.relatedProjectSlug,
    relatedProjectTitle: post.relatedProjectTitle,
  };
}

export const mockPosts: Post[] = mockPostDetails.map(toMockPostSummary);

export const heroData = {
  headline: "직접 만든 프로젝트와 개발 기록을 한 화면에서 공개합니다",
  subheadline: "프로젝트를 먼저 경험하고, 글로 설계와 시행착오를 읽는 홈",
  description:
    "개발하며 만든 서비스, 실험 중인 도구, 그리고 그 배경을 정리한 글을 분리된 섹션으로 보여주는 개인 기술 블로그 플랫폼입니다.",
  currentFocus: ["C", "시스템 프로그래밍", "Next.js"],
  stats: {
    projects: mockProjects.length,
    posts: 24,
    yearsOfDev: 5,
  },
};

export function getMockProjectBySlug(slug: string) {
  return mockProjects.find((project) => project.slug === slug);
}

export function getMockPostBySlug(slug: string) {
  return mockPosts.find((post) => post.slug === slug);
}

export function getMockPostDetailBySlug(slug: string) {
  return mockPostDetails.find((post) => post.slug === slug);
}

export function getMockPostsByProjectSlug(slug: string) {
  return mockPosts.filter((post) => post.relatedProjectSlug === slug);
}

export function filterMockPosts(params: {
  q?: string;
  category?: string;
  lang?: string;
}) {
  const q = params.q?.trim().toLowerCase();
  const category = params.category?.trim();
  const lang = params.lang?.trim();

  return mockPosts.filter((post) => {
    const matchesQuery =
      !q ||
      post.title.toLowerCase().includes(q) ||
      post.excerpt.toLowerCase().includes(q) ||
      post.tags.some((tag) => tag.toLowerCase().includes(q));
    const matchesCategory = !category || post.category === category;
    const matchesLang = !lang || post.lang === lang;

    return matchesQuery && matchesCategory && matchesLang;
  });
}

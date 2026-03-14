import type { Post } from "@/src/entities/post";
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

export const mockPosts: Post[] = [
  {
    id: "post-c-pointer",
    slug: "understanding-c-pointers",
    title: "C 포인터를 메모리 관점으로 다시 이해하기",
    excerpt:
      "포인터를 문법이 아니라 메모리 배치와 참조 흐름으로 설명해보는 글입니다.",
    category: "tutorial",
    tags: ["C", "포인터", "메모리"],
    publishedAt: "2026-03-01",
    readingTime: "12분",
    relatedProjectSlug: "c-memory-lab",
    relatedProjectTitle: "C 메모리 실험실",
  },
  {
    id: "post-blog-home",
    slug: "designing-project-first-home",
    title: "프로젝트를 먼저 보여주는 블로그 홈을 설계한 이유",
    excerpt:
      "일반적인 글 목록형 홈이 아니라 프로젝트 랜딩 중심 구조를 선택한 배경과 기준을 정리합니다.",
    category: "devlog",
    tags: ["Next.js", "정보구조", "설계"],
    publishedAt: "2026-02-24",
    readingTime: "9분",
    relatedProjectSlug: "blog-platform",
    relatedProjectTitle: "블로그 플랫폼",
  },
  {
    id: "post-app-router",
    slug: "nextjs-app-router-notes",
    title: "App Router 공개 화면에서 유지한 기본 규칙",
    excerpt:
      "Server Component 우선, 링크 중심 마크업, 메타데이터 확장 가능성 같은 공개 화면 기본 원칙을 정리합니다.",
    category: "web",
    tags: ["Next.js", "SSR", "SEO"],
    publishedAt: "2026-02-18",
    readingTime: "11분",
  },
  {
    id: "post-retro",
    slug: "building-in-public-retrospective",
    title: "만들면서 공개하는 방식의 장단점",
    excerpt:
      "프로젝트와 기록을 함께 운영하면서 얻은 장점과, 감수해야 했던 운영 비용을 회고합니다.",
    category: "retrospective",
    tags: ["회고", "사이드 프로젝트"],
    publishedAt: "2026-02-10",
    readingTime: "8분",
  },
  {
    id: "post-spring-api",
    slug: "spring-next-blog-architecture-notes",
    title: "Spring API와 Next.js 공개 화면을 같이 설계할 때 메모한 기준",
    excerpt:
      "관리 기능과 공개 화면의 책임을 분리하면서도 SEO와 운영 흐름을 같이 유지하기 위해 정리한 설계 기준입니다.",
    category: "devlog",
    tags: ["Spring", "Next.js", "Architecture"],
    publishedAt: "2026-02-04",
    readingTime: "14분",
    relatedProjectSlug: "blog-platform",
    relatedProjectTitle: "블로그 플랫폼",
  },
];

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

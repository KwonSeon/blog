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
];

export const heroData = {
  headline: "만들고, 기록하고, 바로 공개합니다",
  subheadline: "프로젝트 랜딩과 기술 기록을 한 화면에 담는 공간",
  description:
    "직접 운영하는 프로젝트와 그 과정을 정리한 글을 함께 보여주는 공개 홈의 초안입니다.",
  currentFocus: ["C", "시스템 프로그래밍", "Next.js"],
  stats: {
    projects: mockProjects.length,
    posts: mockPosts.length,
    yearsOfDev: 5,
  },
};

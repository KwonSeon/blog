export const siteConfig = {
  name: "s-nowk",
  description: "개발 기록과 직접 만든 서비스를 함께 공개하는 블로그 플랫폼",
  url: process.env.NEXT_PUBLIC_SITE_URL ?? "http://localhost:3000",
  author: {
    name: "kwonseon",
    role: "소프트웨어 개발자",
    focus: ["C", "시스템 프로그래밍", "웹 개발"],
  },
  navigation: {
    main: [
      { label: "프로젝트", href: "/projects" },
      { label: "글", href: "/posts" },
    ],
  },
  social: {
    github: "",
    email: "",
  },
} as const;

export type SiteConfig = typeof siteConfig;

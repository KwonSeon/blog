export interface SiteSettings {
  siteTitle: string;
  heroTitle: string;
  heroDescription: string;
  githubUrl: string;
  homeOrder: "POSTS_FIRST" | "PROJECTS_FIRST";
  postLayout: "GRID" | "LIST";
  contentWidth: "STANDARD" | "WIDE";
  showHero: boolean;
  showPosts: boolean;
  showProjects: boolean;
  revision: number;
}
export const defaultSiteSettings: SiteSettings = {
  siteTitle: "s-nowk",
  heroTitle: "만들고, 배우고,\n기록합니다.",
  heroDescription:
    "직접 만드는 서비스와 그 과정에서 만난 문제들. 개발의 생각과 경험을 차곡차곡 남기는 공간입니다.",
  githubUrl: "https://github.com/KwonSeon",
  homeOrder: "POSTS_FIRST",
  postLayout: "GRID",
  contentWidth: "STANDARD",
  showHero: true,
  showPosts: true,
  showProjects: true,
  revision: 0,
};
export function layoutStyle(settings: SiteSettings) {
  return {
    "--blog-content-width":
      settings.contentWidth === "WIDE" ? "88rem" : "72rem",
  } as React.CSSProperties;
}

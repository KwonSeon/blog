import { Container, CTAButton } from "@/src/shared/ui";
import { HomePostsSection } from "@/src/widgets/home-posts";
import { HomeProjectsSection } from "@/src/widgets/home-projects";
import type { Post } from "@/src/entities/post";
import type { Project } from "@/src/entities/project";
import type { SiteSettings } from "@/src/shared/lib/site-settings";
export function HomeContent({
  settings,
  posts,
  projects,
}: {
  settings: SiteSettings;
  posts: Post[];
  projects: Project[];
}) {
  const sections =
    settings.homeOrder === "POSTS_FIRST"
      ? ["posts", "projects"]
      : ["projects", "posts"];
  return (
    <>
      {settings.showHero && (
        <section
          data-home-section="hero"
          className="border-b border-border py-16 sm:py-24"
        >
          <Container>
            <p className="text-sm tracking-widest text-primary">
              {settings.siteTitle}
            </p>
            <h1 className="mt-5 max-w-3xl whitespace-pre-line break-words text-4xl sm:text-6xl font-semibold tracking-tight leading-tight">
              {settings.heroTitle}
            </h1>
            <p className="mt-6 max-w-xl whitespace-pre-line text-lg leading-8 text-muted-foreground">
              {settings.heroDescription}
            </p>
            <div className="mt-8 flex flex-wrap gap-3">
              <CTAButton href="/posts">모든 글 읽기</CTAButton>
              {settings.githubUrl && (
                <CTAButton
                  href={settings.githubUrl}
                  external
                  variant="secondary"
                >
                  GitHub
                </CTAButton>
              )}
            </div>
          </Container>
        </section>
      )}
      {sections.map((section) =>
        section === "posts"
          ? settings.showPosts && (
              <HomePostsSection
                key="posts"
                posts={posts}
                layout={settings.postLayout}
              />
            )
          : settings.showProjects && (
              <HomeProjectsSection key="projects" projects={projects} />
            ),
      )}
    </>
  );
}

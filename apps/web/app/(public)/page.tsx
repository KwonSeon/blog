import { Container, CTAButton } from "@/src/shared/ui";
import { HomePostsSection } from "@/src/widgets/home-posts";
import { HomeProjectsSection } from "@/src/widgets/home-projects";
import { getHomePageData } from "@/src/shared/lib/api/public-page-data";
export const metadata = {
  title: "개발과 일상의 기록",
  description:
    "직접 만드는 서비스, 개발 과정에서 배운 것, 오래 남기고 싶은 기록.",
};
export default async function Home() {
  const { projects, posts } = await getHomePageData();
  return (
    <>
      <section className="border-b border-border py-16 sm:py-24">
        <Container>
          <p className="text-sm tracking-widest text-primary">S-NOWK JOURNAL</p>
          <h1 className="mt-5 max-w-3xl text-4xl sm:text-6xl font-semibold tracking-tight leading-tight">
            만들고, 배우고,
            <br />
            기록합니다.
          </h1>
          <p className="mt-6 max-w-xl text-lg leading-8 text-muted-foreground">
            직접 만드는 서비스와 그 과정에서 만난 문제들. 개발의 생각과 경험을
            차곡차곡 남기는 공간입니다.
          </p>
          <div className="mt-8 flex flex-wrap gap-3">
            <CTAButton href="/posts">모든 글 읽기</CTAButton>
            <CTAButton
              href="https://github.com/KwonSeon"
              external
              variant="secondary"
            >
              GitHub
            </CTAButton>
          </div>
        </Container>
      </section>
      <HomePostsSection posts={posts} />
      <HomeProjectsSection projects={projects} />
    </>
  );
}

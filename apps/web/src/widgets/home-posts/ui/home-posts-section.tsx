import { PostCard, type Post } from "@/src/entities/post";
import { Container, SectionHeader } from "@/src/shared/ui";

interface HomePostsSectionProps {
  posts: Post[];
  layout?: "GRID" | "LIST";
}

export function HomePostsSection({
  posts,
  layout = "GRID",
}: HomePostsSectionProps) {
  return (
    <section
      data-home-section="posts"
      className="border-t border-border bg-secondary/20 py-16 sm:py-20"
      aria-labelledby="home-posts-heading"
    >
      <Container>
        <SectionHeader
          headingId="home-posts-heading"
          title="최신 글"
          description="개발 과정에서 배운 것과 오래 남기고 싶은 이야기."
          viewAllHref="/posts"
          viewAllLabel="전체 글 보기"
        />

        {!posts.length && (
          <p className="py-8 text-muted-foreground">
            첫 번째 글을 준비하고 있습니다.
          </p>
        )}
        <div
          data-post-layout={layout}
          className={
            layout === "LIST"
              ? "grid gap-5"
              : "grid gap-6 md:grid-cols-2 xl:grid-cols-3"
          }
        >
          {posts.map((post, index) => (
            <PostCard
              key={post.id}
              post={post}
              className={
                layout === "LIST"
                  ? "[&_img]:max-h-56"
                  : index === 0
                    ? "md:col-span-2 xl:col-span-1"
                    : undefined
              }
            />
          ))}
        </div>
      </Container>
    </section>
  );
}

import { PostCard, type Post } from "@/src/entities/post";
import { Container, SectionHeader } from "@/src/shared/ui";

interface HomePostsSectionProps {
  posts: Post[];
}

export function HomePostsSection({ posts }: HomePostsSectionProps) {
  return (
    <section
      className="border-t border-border bg-secondary/20 py-16 sm:py-20"
      aria-labelledby="home-posts-heading"
    >
      <Container>
        <SectionHeader
          headingId="home-posts-heading"
          title="최신 글"
          description="튜토리얼, 시스템 프로그래밍 메모, 설계 기록, 회고를 분리된 텍스트 영역으로 보여줍니다."
          viewAllHref="/posts"
          viewAllLabel="전체 글 보기"
        />

        <div className="grid gap-2 md:grid-cols-2 xl:grid-cols-3">
          {posts.map((post, index) => (
            <PostCard
              key={post.id}
              post={post}
              className={index === 0 ? "md:col-span-2 xl:col-span-1" : undefined}
            />
          ))}
        </div>
      </Container>
    </section>
  );
}

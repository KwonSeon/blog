import type { Project } from "@/src/entities/project";
import {
  POST_CATEGORY_LABELS,
  type PostDetail,
} from "@/src/entities/post/model/types";
import { CTAButton, StatusBadge } from "@/src/shared/ui";

interface PostDetailHeroProps {
  post: PostDetail;
  relatedProject?: Project;
  headingId?: string;
}

export function PostDetailHero({
  post,
  relatedProject,
  headingId = "post-detail-heading",
}: PostDetailHeroProps) {
  return (
    <div className="max-w-4xl">
      <div className="flex flex-wrap items-center gap-2">
        <StatusBadge variant="accent">
          {POST_CATEGORY_LABELS[post.category]}
        </StatusBadge>
        <StatusBadge variant="outline">{post.lang.toUpperCase()}</StatusBadge>
        <span className="text-sm text-muted-foreground">slug: {post.slug}</span>
      </div>

      <p className="mt-5 text-xs uppercase tracking-[0.24em] text-primary">
        Post Detail
      </p>
      <h1
        id={headingId}
        className="mt-4 text-balance text-4xl font-semibold tracking-tight text-foreground sm:text-5xl"
      >
        {post.title}
      </h1>
      <p className="mt-6 max-w-2xl text-base leading-8 text-muted-foreground">
        {post.excerpt}
      </p>

      <div className="mt-8 flex flex-col gap-3 sm:flex-row">
        <CTAButton href="/posts" size="lg">
          글 목록으로 돌아가기
        </CTAButton>
        {relatedProject ? (
          <CTAButton
            href={`/projects/${relatedProject.slug}`}
            variant="outline"
            size="lg"
          >
            연결 프로젝트 보기
          </CTAButton>
        ) : (
          <CTAButton href="/" variant="outline" size="lg">
            홈으로 이동
          </CTAButton>
        )}
      </div>
    </div>
  );
}

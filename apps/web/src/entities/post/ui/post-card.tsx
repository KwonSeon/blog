import Link from "next/link";
import { cn } from "@/lib/utils";
import {
  POST_CATEGORY_LABELS,
  type Post,
} from "@/src/entities/post/model/types";
import { StatusBadge } from "@/src/shared/ui";

interface PostCardProps {
  post: Post;
  className?: string;
}

export function PostCard({ post, className }: PostCardProps) {
  const formattedDate = new Date(post.publishedAt).toLocaleDateString("ko-KR", {
    year: "numeric",
    month: "long",
    day: "numeric",
  });

  return (
    <article
      className={cn(
        "group rounded-3xl border border-transparent p-5 transition-colors hover:border-border hover:bg-surface",
        className,
      )}
    >
      <div className="mb-3">
        <StatusBadge variant="accent">
          {POST_CATEGORY_LABELS[post.category]}
        </StatusBadge>
      </div>

      <h3 className="text-lg font-semibold tracking-tight text-foreground">
        <Link
          href={`/posts/${post.slug}`}
          className="transition-colors hover:text-primary focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-ring"
        >
          {post.title}
        </Link>
      </h3>

      <p className="mt-3 text-sm leading-7 text-muted-foreground">
        {post.excerpt}
      </p>

      <div className="mt-4 flex flex-wrap items-center gap-x-4 gap-y-2 text-xs text-muted-foreground">
        <span className="inline-flex items-center gap-1">
          <span aria-hidden="true">발행</span>
          <time dateTime={post.publishedAt}>{formattedDate}</time>
        </span>
        <span className="inline-flex items-center gap-1">
          <span aria-hidden="true">읽기</span>
          {post.readingTime}
        </span>
        {post.relatedProjectSlug && post.relatedProjectTitle ? (
          <Link
            href={`/projects/${post.relatedProjectSlug}`}
            className="inline-flex items-center gap-1 text-primary transition-colors hover:text-primary/80 focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-ring"
          >
            <span aria-hidden="true">연결</span>
            {post.relatedProjectTitle}
          </Link>
        ) : null}
      </div>

      <div className="mt-5 flex flex-wrap gap-2">
        {post.tags.slice(0, 3).map((tag) => (
          <span
            key={tag}
            className="rounded-full border border-border px-2.5 py-1 text-[11px] text-muted-foreground"
          >
            {tag}
          </span>
        ))}
      </div>
    </article>
  );
}

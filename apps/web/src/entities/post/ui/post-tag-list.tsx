import { cn } from "@/lib/utils";

interface PostTagListProps {
  tags: string[];
  className?: string;
  tagClassName?: string;
}

export function PostTagList({ tags, className, tagClassName }: PostTagListProps) {
  return (
    <div className={cn("flex flex-wrap gap-2", className)}>
      {tags.slice(0, 3).map((tag) => (
        <span
          key={tag}
          className={cn(
            "rounded-full border border-border px-2.5 py-1 text-[11px] text-muted-foreground",
            tagClassName,
          )}
        >
          {tag}
        </span>
      ))}
    </div>
  );
}

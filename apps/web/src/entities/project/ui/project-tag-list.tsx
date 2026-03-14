import { cn } from "@/lib/utils";

interface ProjectTagListProps {
  tags: string[];
  className?: string;
  tagClassName?: string;
}

export function ProjectTagList({
  tags,
  className,
  tagClassName,
}: ProjectTagListProps) {
  return (
    <div className={cn("flex flex-wrap gap-2", className)}>
      {tags.map((tag) => (
        <span
          key={tag}
          className={cn(
            "rounded-full bg-secondary px-3 py-1 text-xs text-secondary-foreground",
            tagClassName,
          )}
        >
          {tag}
        </span>
      ))}
    </div>
  );
}

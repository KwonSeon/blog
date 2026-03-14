import { cn } from "@/lib/utils";

type SurfacePadding = "none" | "sm" | "md" | "lg";
type SurfaceElement = "div" | "article" | "section";

interface SurfaceCardProps {
  children: React.ReactNode;
  className?: string;
  interactive?: boolean;
  padding?: SurfacePadding;
  as?: SurfaceElement;
}

const paddingClasses: Record<SurfacePadding, string> = {
  none: "",
  sm: "p-4",
  md: "p-5 sm:p-6",
  lg: "p-6 sm:p-8",
};

export function SurfaceCard({
  children,
  className,
  interactive = false,
  padding = "md",
  as: Component = "div",
}: SurfaceCardProps) {
  return (
    <Component
      className={cn(
        "rounded-3xl border border-border bg-surface text-surface-foreground shadow-[0_12px_40px_-32px_rgba(20,20,20,0.45)]",
        paddingClasses[padding],
        interactive &&
          "transition duration-200 hover:-translate-y-0.5 hover:border-primary/20 hover:shadow-[0_16px_48px_-32px_rgba(20,20,20,0.5)]",
        className,
      )}
    >
      {children}
    </Component>
  );
}

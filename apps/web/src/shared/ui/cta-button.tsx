import Link from "next/link";
import { cn } from "@/lib/utils";

type CTAButtonVariant = "primary" | "secondary" | "outline" | "accent";
type CTAButtonSize = "sm" | "md" | "lg";

const variantClasses: Record<CTAButtonVariant, string> = {
  primary: "bg-primary text-primary-foreground hover:bg-primary/90",
  secondary: "bg-secondary text-secondary-foreground hover:bg-secondary/80",
  outline: "border border-border bg-transparent text-foreground hover:bg-secondary/60",
  accent: "bg-accent text-accent-foreground hover:bg-accent/90",
};

const sizeClasses: Record<CTAButtonSize, string> = {
  sm: "min-h-9 px-4 text-sm",
  md: "min-h-10 px-5 text-sm",
  lg: "min-h-11 px-6 text-base",
};

interface CTAButtonProps {
  children: React.ReactNode;
  href: string;
  variant?: CTAButtonVariant;
  size?: CTAButtonSize;
  className?: string;
  external?: boolean;
}

export function CTAButton({
  children,
  href,
  variant = "primary",
  size = "md",
  className,
  external = false,
}: CTAButtonProps) {
  return (
    <Link
      href={href}
      className={cn(
        "inline-flex items-center justify-center gap-2 rounded-full font-medium transition-colors focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-ring disabled:pointer-events-none disabled:opacity-50",
        variantClasses[variant],
        sizeClasses[size],
        className,
      )}
      target={external ? "_blank" : undefined}
      rel={external ? "noopener noreferrer" : undefined}
    >
      {children}
    </Link>
  );
}

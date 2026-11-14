"use client";
import { Button } from "@astryxdesign/core/Button";
interface Props {
  children: React.ReactNode;
  href: string;
  variant?: "primary" | "secondary" | "outline" | "accent";
  size?: "sm" | "md" | "lg";
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
}: Props) {
  return (
    <Button
      href={href}
      label={typeof children === "string" ? children : "자세히 보기"}
      variant={
        variant === "outline"
          ? "secondary"
          : variant === "accent"
            ? "primary"
            : variant
      }
      size={size}
      className={className}
      target={external ? "_blank" : undefined}
      rel={external ? "noopener noreferrer" : undefined}
    >
      {children}
    </Button>
  );
}

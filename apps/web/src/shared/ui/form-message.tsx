import { cn } from "@/lib/utils";

type FormMessageVariant = "info" | "success" | "danger";

const variantClasses: Record<FormMessageVariant, string> = {
  info: "bg-secondary/70 text-muted-foreground",
  success: "border border-primary/20 bg-primary/10 text-primary",
  danger: "border border-destructive/20 bg-destructive/10 text-destructive",
};

interface FormMessageProps {
  children: React.ReactNode;
  variant?: FormMessageVariant;
  className?: string;
}

export function FormMessage({
  children,
  variant = "info",
  className,
}: FormMessageProps) {
  return (
    <div
      className={cn(
        "rounded-2xl px-4 py-4 text-sm leading-7",
        variantClasses[variant],
        className,
      )}
    >
      {children}
    </div>
  );
}

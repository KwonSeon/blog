import { forwardRef } from "react";
import { cn } from "@/lib/utils";

interface FormTextareaProps
  extends React.TextareaHTMLAttributes<HTMLTextAreaElement> {
  label: string;
  containerClassName?: string;
}

export const FormTextarea = forwardRef<HTMLTextAreaElement, FormTextareaProps>(
  function FormTextarea(
    { label, containerClassName, className, ...props },
    ref,
  ) {
    return (
      <label className={cn("grid gap-2 text-sm text-foreground", containerClassName)}>
        <span className="font-medium">{label}</span>
        <textarea
          {...props}
          ref={ref}
          className={cn(
            "rounded-2xl border border-input bg-background px-4 py-3 text-base text-foreground placeholder:text-muted-foreground disabled:cursor-not-allowed disabled:opacity-60",
            className,
          )}
        />
      </label>
    );
  },
);

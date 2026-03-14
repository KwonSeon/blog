import { cn } from "@/lib/utils";

interface FormTextareaProps
  extends React.TextareaHTMLAttributes<HTMLTextAreaElement> {
  label: string;
  containerClassName?: string;
}

export function FormTextarea({
  label,
  containerClassName,
  className,
  ...props
}: FormTextareaProps) {
  return (
    <label className={cn("grid gap-2 text-sm text-foreground", containerClassName)}>
      <span className="font-medium">{label}</span>
      <textarea
        {...props}
        className={cn(
          "rounded-2xl border border-input bg-background px-4 py-3 text-base text-foreground placeholder:text-muted-foreground disabled:cursor-not-allowed disabled:opacity-60",
          className,
        )}
      />
    </label>
  );
}

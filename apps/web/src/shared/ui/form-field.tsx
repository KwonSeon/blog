import { cn } from "@/lib/utils";

interface FormFieldProps extends React.InputHTMLAttributes<HTMLInputElement> {
  label: string;
  containerClassName?: string;
}

export function FormField({
  label,
  containerClassName,
  className,
  ...props
}: FormFieldProps) {
  return (
    <label className={cn("grid gap-2 text-sm text-foreground", containerClassName)}>
      <span className="font-medium">{label}</span>
      <input
        {...props}
        className={cn(
          "min-h-12 rounded-2xl border border-input bg-background px-4 text-base text-foreground placeholder:text-muted-foreground disabled:cursor-not-allowed disabled:opacity-60",
          className,
        )}
      />
    </label>
  );
}

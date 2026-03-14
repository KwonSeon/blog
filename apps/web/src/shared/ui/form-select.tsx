import { cn } from "@/lib/utils";

interface FormSelectOption {
  value: string;
  label: string;
}

interface FormSelectProps extends React.SelectHTMLAttributes<HTMLSelectElement> {
  label: string;
  options: FormSelectOption[];
  containerClassName?: string;
}

export function FormSelect({
  label,
  options,
  containerClassName,
  className,
  ...props
}: FormSelectProps) {
  return (
    <label className={cn("grid gap-2 text-sm text-foreground", containerClassName)}>
      <span className="font-medium">{label}</span>
      <select
        {...props}
        className={cn(
          "min-h-12 rounded-2xl border border-input bg-background px-4 text-base text-foreground disabled:cursor-not-allowed disabled:opacity-60",
          className,
        )}
      >
        {options.map((option) => (
          <option key={option.value} value={option.value}>
            {option.label}
          </option>
        ))}
      </select>
    </label>
  );
}

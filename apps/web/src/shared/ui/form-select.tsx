"use client";
import { Selector } from "@astryxdesign/core/Selector";
interface Props {
  label: string;
  options: { value: string; label: string }[];
  value?: string;
  onChange?: (value: string) => void;
  disabled?: boolean;
  containerClassName?: string;
}
export function FormSelect({
  label,
  options,
  value,
  onChange,
  disabled,
  containerClassName,
}: Props) {
  return (
    <div className={containerClassName}>
      <Selector
        label={label}
        options={options}
        value={value}
        onChange={onChange}
        isDisabled={disabled}
        size="lg"
        width="100%"
      />
    </div>
  );
}

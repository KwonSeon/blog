"use client";
import { useState } from "react";
import { TextInput, type TextInputType } from "@astryxdesign/core/TextInput";
interface Props extends React.InputHTMLAttributes<HTMLInputElement> {
  label: string;
  containerClassName?: string;
}
export function FormField({
  label,
  containerClassName,
  className,
  value,
  defaultValue,
  onChange,
  type = "text",
  disabled,
  required,
  readOnly,
  name,
  placeholder,
  autoComplete,
  maxLength,
  onBlur,
  onFocus,
}: Props) {
  const [local, setLocal] = useState(String(defaultValue ?? ""));
  return (
    <TextInput
      label={label}
      value={String(value ?? local)}
      onChange={(v, e) => {
        if (maxLength && v.length > maxLength) return;
        setLocal(v);
        onChange?.(e);
      }}
      type={type as TextInputType}
      size="lg"
      isDisabled={disabled}
      isRequired={required}
      isReadOnly={readOnly}
      htmlName={name}
      placeholder={placeholder}
      autoComplete={autoComplete}
      onBlur={onBlur}
      onFocus={onFocus}
      containerClassName={containerClassName}
      controlClassName={className}
      width="100%"
    />
  );
}

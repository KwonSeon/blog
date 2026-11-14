"use client";
import { forwardRef, useState } from "react";
import { TextArea } from "@astryxdesign/core/TextArea";
interface Props extends React.TextareaHTMLAttributes<HTMLTextAreaElement> {
  label: string;
  containerClassName?: string;
}
export const FormTextarea = forwardRef<HTMLTextAreaElement, Props>(
  function FormTextarea(
    {
      label,
      containerClassName,
      value,
      defaultValue,
      onChange,
      disabled,
      required,
      readOnly,
      name,
      placeholder,
      rows,
      maxLength,
      onPaste,
      onBlur,
    },
    ref,
  ) {
    const [local, setLocal] = useState(String(defaultValue ?? ""));
    return (
      <TextArea
        ref={ref}
        label={label}
        value={String(value ?? local)}
        onChange={(v, e) => {
          setLocal(v);
          onChange?.(e);
        }}
        isDisabled={disabled}
        isRequired={required}
        isReadOnly={readOnly}
        htmlName={name}
        placeholder={placeholder}
        rows={rows}
        maxLength={maxLength}
        onPaste={onPaste}
        onBlur={onBlur}
        containerClassName={containerClassName}
        width="100%"
      />
    );
  },
);

"use client";
import { Button } from "@astryxdesign/core/Button";
import { Children } from "react";
export function ActionButton({
  children,
  disabled,
  onClick,
  type = "button",
  "aria-label": ariaLabel,
}: React.ButtonHTMLAttributes<HTMLButtonElement>) {
  const label =
    ariaLabel ??
    Children.toArray(children)
      .filter((x) => typeof x === "string")
      .join("") ??
    "실행";
  return (
    <Button
      label={label || "실행"}
      type={type}
      isDisabled={disabled}
      onClick={onClick}
      size="lg"
    >
      {children}
    </Button>
  );
}

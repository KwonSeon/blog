"use client";
import { AppShell } from "@astryxdesign/core/AppShell";
import { Layout } from "@astryxdesign/core/Layout";
export function BlogShell({
  header,
  footer,
  children,
}: {
  header: React.ReactNode;
  footer: React.ReactNode;
  children: React.ReactNode;
}) {
  return (
    <AppShell
      height="auto"
      contentPadding={0}
      mobileNav={false}
      topNav={header}
    >
      <Layout height="auto" padding={0} content={children} footer={footer} />
    </AppShell>
  );
}

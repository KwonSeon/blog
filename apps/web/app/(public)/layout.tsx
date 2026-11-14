import { BlogShell } from "@/src/shared/ui/blog-shell";
import { SiteFooter } from "@/src/widgets/site-footer";
import { SiteHeader } from "@/src/widgets/site-header";

export default function PublicLayout({
  children,
}: Readonly<{
  children: React.ReactNode;
}>) {
  return (
    <BlogShell header={<SiteHeader />} footer={<SiteFooter />}>
      {children}
    </BlogShell>
  );
}

import { getSiteSettings } from "@/src/shared/lib/server/site-settings";
import { layoutStyle } from "@/src/shared/lib/site-settings";
import { BlogShell } from "@/src/shared/ui/blog-shell";
import { SiteFooter } from "@/src/widgets/site-footer";
import { SiteHeader } from "@/src/widgets/site-header";

export default async function PublicLayout({
  children,
}: Readonly<{
  children: React.ReactNode;
}>) {
  const settings = await getSiteSettings();
  return (
    <div className="blog-layout" style={layoutStyle(settings)}>
      <BlogShell
        header={
          <SiteHeader
            title={settings.siteTitle}
            githubUrl={settings.githubUrl}
          />
        }
        footer={<SiteFooter title={settings.siteTitle} />}
      >
        {children}
      </BlogShell>
    </div>
  );
}

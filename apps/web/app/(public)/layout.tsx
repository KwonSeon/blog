import {advertisingConfig} from "@/src/shared/lib/advertising";
import {siteConfig} from "@/src/shared/config/site";
export function generateMetadata(){const config=advertisingConfig(process.env,siteConfig.url);return config.production&&config.client?{other:{"google-adsense-account":config.client}}:{};}
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
    <div className="blog-layout" style={layoutStyle(settings)} data-post-layout={settings.postLayout} data-home-order={settings.homeOrder} data-show-hero={settings.showHero} data-show-posts={settings.showPosts} data-show-projects={settings.showProjects}>
      <BlogShell
        header={
          <SiteHeader
            title={settings.siteTitle}
            githubUrl={settings.githubUrl}
          />
        }
        footer={<><SiteFooter title={settings.siteTitle} /><nav aria-label="블로그 안내" className="flex flex-wrap justify-center gap-x-5 gap-y-3 border-t border-border px-4 py-6 text-sm text-muted-foreground"><a href="/about">소개</a><a href="/contact">문의</a><a href="/editorial">운영 안내</a><a href="/privacy">개인정보</a><a href="/subscribe">이메일 구독</a></nav></>}
      >
        {children}
      </BlogShell>
    </div>
  );
}

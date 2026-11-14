import { JsonLd } from "@/src/shared/ui/json-ld";
import { siteConfig } from "@/src/shared/config/site";
import { HomeContent } from "@/src/widgets/home-content";
import { getHomePageData } from "@/src/shared/lib/api/public-page-data";
import { getSiteSettings } from "@/src/shared/lib/server/site-settings";
export async function generateMetadata() {
  const settings = await getSiteSettings();
  return {
    title: { absolute: settings.siteTitle },
    description: settings.heroDescription,
    alternates: {canonical:"/"},
  };
}
export default async function Home() {
  const [data, settings] = await Promise.all([
    getHomePageData(),
    getSiteSettings(),
  ]);
  return (
    <><JsonLd data={{"@context":"https://schema.org","@type":"WebSite","@id":`${siteConfig.url}/#website`,url:siteConfig.url,name:settings.siteTitle,description:settings.heroDescription,inLanguage:"ko"}} /><HomeContent
      settings={settings}
      posts={data.posts}
      projects={data.projects}
    /></>
  );
}

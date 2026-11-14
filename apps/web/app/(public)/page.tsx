import { HomeContent } from "@/src/widgets/home-content";
import { getHomePageData } from "@/src/shared/lib/api/public-page-data";
import { getSiteSettings } from "@/src/shared/lib/server/site-settings";
export async function generateMetadata() {
  const settings = await getSiteSettings();
  return {
    title: { absolute: settings.siteTitle },
    description: settings.heroDescription,
  };
}
export default async function Home() {
  const [data, settings] = await Promise.all([
    getHomePageData(),
    getSiteSettings(),
  ]);
  return (
    <HomeContent
      settings={settings}
      posts={data.posts}
      projects={data.projects}
    />
  );
}

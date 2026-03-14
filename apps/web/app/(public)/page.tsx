import type { Metadata } from "next";
import { HomeCTA } from "@/src/widgets/home-cta";
import { HomeHero } from "@/src/widgets/home-hero";
import { HomePostsSection } from "@/src/widgets/home-posts";
import { HomeProjectsSection } from "@/src/widgets/home-projects";
import { siteConfig } from "@/src/shared/config/site";
import { PromoSlot } from "@/src/shared/ui";
import {
  heroData,
  mockPosts,
  mockProjects,
} from "@/src/shared/lib/mock/home-data";

export const metadata: Metadata = {
  title: "홈",
  description: siteConfig.description,
};

export default function HomePage() {
  return (
    <>
      <HomeHero
        headline={heroData.headline}
        subheadline={heroData.subheadline}
        description={heroData.description}
        currentFocus={heroData.currentFocus}
        stats={heroData.stats}
      />
      <HomeProjectsSection projects={mockProjects} />
      <div className="border-y border-border bg-secondary/25 py-7">
        <PromoSlot size="leaderboard" showPlaceholder />
      </div>
      <HomePostsSection posts={mockPosts} />
      <HomeCTA />
    </>
  );
}

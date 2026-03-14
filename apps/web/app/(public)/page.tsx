import type { Metadata } from "next";
import { HomeCTA } from "@/src/widgets/home-cta";
import { HomeHero } from "@/src/widgets/home-hero";
import { HomePostsSection } from "@/src/widgets/home-posts";
import { HomeProjectsSection } from "@/src/widgets/home-projects";
import { PromoSlot } from "@/src/shared/ui";
import {
  heroData,
  mockPosts,
  mockProjects,
} from "@/src/shared/lib/mock/home-data";
import { buildPublicMetadata } from "@/src/shared/lib/seo/public-metadata";

export const metadata: Metadata = buildPublicMetadata({
  title: "홈",
  description: "개발 기록과 직접 만든 서비스를 함께 공개하는 블로그 홈 화면입니다.",
  path: "/",
  keywords: ["홈", "프로젝트 허브", "블로그 홈"],
});

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

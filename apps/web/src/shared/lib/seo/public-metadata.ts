import type { Metadata } from "next";
import { siteConfig } from "@/src/shared/config/site";

type PublicMetadataType = "website" | "article";

interface BuildPublicMetadataInput {
  title: string;
  description: string;
  path: string;
  type?: PublicMetadataType;
  keywords?: string[];
  robots?: Metadata["robots"];
  authors?: string[];
  publishedTime?: string;
  modifiedTime?: string;
  section?: string;
  tags?: string[];
}

function buildAbsoluteUrl(path: string) {
  return `${siteConfig.url.replace(/\/$/, "")}${path}`;
}

function dedupeKeywords(keywords: string[]) {
  return Array.from(new Set(keywords.filter(Boolean)));
}

export function buildPublicMetadata({
  title,
  description,
  path,
  type = "website",
  keywords = [],
  robots,
  authors,
  publishedTime,
  modifiedTime,
  section,
  tags,
}: BuildPublicMetadataInput): Metadata {
  const resolvedAuthors = authors?.length ? authors : [siteConfig.author.name];
  const resolvedKeywords = dedupeKeywords([
    ...siteConfig.seo.defaultKeywords,
    ...keywords,
  ]);

  return {
    title,
    description,
    keywords: resolvedKeywords,
    authors: resolvedAuthors.map((name) => ({ name })),
    alternates: {
      canonical: path,
    },
    robots,
    openGraph: {
      title: `${title} | ${siteConfig.name}`,
      description,
      url: buildAbsoluteUrl(path),
      siteName: siteConfig.name,
      locale: siteConfig.seo.locale,
      type,
      ...(type === "article"
        ? {
            authors: resolvedAuthors,
            publishedTime,
            modifiedTime,
            section,
            tags,
          }
        : {}),
    },
    twitter: {
      card: "summary",
      title: `${title} | ${siteConfig.name}`,
      description,
    },
  };
}

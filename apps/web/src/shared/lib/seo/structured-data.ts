export function serializeJsonLd(value: unknown): string {
  return JSON.stringify(value).replace(/</g, "\\u003c").replace(/>/g, "\\u003e").replace(/\u2028/g, "\\u2028").replace(/\u2029/g, "\\u2029");
}
export function absolutePublicUrl(base: string, path: string): string | undefined {
  try { const url = new URL(path, base); return ["https:", "http:"].includes(url.protocol) && !url.username && !url.password ? url.href : undefined; } catch { return undefined; }
}
// The blog API persists and returns UTC LocalDateTime values.
export function utcDate(value?: string): string | undefined {
  if (!value) return undefined;
  const normalized = /(?:Z|[+-]\d{2}:\d{2})$/.test(value) ? value : `${value}Z`;
  const date = new Date(normalized);
  return Number.isNaN(date.valueOf()) ? undefined : date.toISOString();
}
export function breadcrumbData(base: string, items: {name: string; path: string}[]) {
  return { "@context": "https://schema.org", "@type": "BreadcrumbList", itemListElement: items.map((item, index) => ({ "@type": "ListItem", position: index + 1, name: item.name, item: absolutePublicUrl(base, item.path) })) };
}
export function blogPostingData(base: string, siteName: string, post: {slug: string; title: string; excerpt: string; lang: string; publishedAt: string; updatedAt?: string; coverUrl?: string}) {
  const url = absolutePublicUrl(base, `/posts/${encodeURIComponent(post.slug)}`);
  const image = post.coverUrl ? absolutePublicUrl(base, post.coverUrl) : undefined;
  return { "@context": "https://schema.org", "@type": "BlogPosting", "@id": `${url}#article`, url, mainEntityOfPage: { "@type": "WebPage", "@id": url }, headline: post.title, description: post.excerpt, inLanguage: post.lang, datePublished: utcDate(post.publishedAt), dateModified: utcDate(post.updatedAt), ...(image ? {image: [image]} : {}), publisher: { "@type": "Organization", name: siteName, url: absolutePublicUrl(base, "/") } };
}

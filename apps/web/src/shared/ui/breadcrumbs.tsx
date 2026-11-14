import Link from "next/link";
import { siteConfig } from "@/src/shared/config/site";
import { breadcrumbData } from "@/src/shared/lib/seo/structured-data";
import { JsonLd } from "./json-ld";
export function Breadcrumbs({ items }: { items: {name: string; path: string}[] }) {
  return <><JsonLd data={breadcrumbData(siteConfig.url, items)} /><nav aria-label="현재 위치" className="mb-8 text-sm text-muted-foreground"><ol className="flex flex-wrap gap-x-2 gap-y-1">{items.map((item, i) => <li key={item.path} className="flex gap-2">{i > 0 && <span aria-hidden="true">/</span>}{i === items.length - 1 ? <span aria-current="page">{item.name}</span> : <Link className="underline underline-offset-4" href={item.path}>{item.name}</Link>}</li>)}</ol></nav></>;
}

"use client";
import Link from "next/link";
import { usePathname } from "next/navigation";
import { CTAButton } from "@/src/shared/ui";
import { AdminLogoutButton } from "@/src/widgets/admin-session-guard/ui/admin-logout-button";
const links = [
  { href: "/admin", label: "대시보드" },
  { href: "/admin/posts", label: "글 관리" },
  { href: "/admin/projects", label: "프로젝트" },
  { href: "/admin/settings", label: "블로그 꾸미기" },
];
export function AdminShell({ children }: { children: React.ReactNode }) {
  const path = usePathname();
  if (path === "/admin/login") return children;
  return (
    <div className="min-h-screen lg:grid lg:grid-cols-[220px_minmax(0,1fr)]">
      <aside className="border-b lg:border-r border-border bg-secondary/20 p-5 lg:sticky lg:top-0 lg:h-screen">
        <Link href="/admin" className="block text-xl font-semibold mb-6">
          s-nowk Studio
        </Link>
        <nav aria-label="관리자 메뉴" className="flex flex-wrap gap-2 lg:grid">
          {links.map((link) => {
            const active =
              link.href === "/admin"
                ? path === "/admin"
                : path.startsWith(link.href);
            return (
              <Link
                key={link.href}
                href={link.href}
                aria-current={active ? "page" : undefined}
                className={`rounded-xl px-4 py-3 text-sm ${active ? "bg-primary text-primary-foreground" : "text-muted-foreground hover:bg-secondary"}`}
              >
                {link.label}
              </Link>
            );
          })}
        </nav>
        <div className="mt-6 flex flex-wrap gap-3">
          <CTAButton href="/" variant="secondary">
            블로그 보기
          </CTAButton>
          <AdminLogoutButton />
        </div>
      </aside>
      <div className="min-w-0">{children}</div>
    </div>
  );
}

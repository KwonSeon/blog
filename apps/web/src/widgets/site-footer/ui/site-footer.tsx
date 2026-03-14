import Link from "next/link";
import { siteConfig } from "@/src/shared/config/site";
import { Container } from "@/src/shared/ui";

export function SiteFooter() {
  const currentYear = new Date().getFullYear();

  return (
    <footer className="border-t border-border bg-secondary/30 py-8">
      <Container>
        <div className="flex flex-col gap-4 text-sm text-muted-foreground sm:flex-row sm:items-center sm:justify-between">
          <p>
            © {currentYear} {siteConfig.name}. All rights reserved.
          </p>
          <nav className="flex flex-wrap items-center gap-4" aria-label="푸터 메뉴">
            {siteConfig.navigation.main.map((item) => (
              <Link
                key={item.href}
                href={item.href}
                className="transition-colors hover:text-foreground"
              >
                {item.label}
              </Link>
            ))}
            {siteConfig.social.email ? (
              <Link
                href={`mailto:${siteConfig.social.email}`}
                className="transition-colors hover:text-foreground"
              >
                연락하기
              </Link>
            ) : null}
          </nav>
        </div>
      </Container>
    </footer>
  );
}

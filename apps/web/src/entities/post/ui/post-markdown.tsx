import { Fragment } from "react";
import { cn } from "@/lib/utils";

type MarkdownBlock =
  | { type: "heading"; level: 1 | 2 | 3; text: string }
  | { type: "paragraph"; text: string }
  | { type: "list"; items: string[] }
  | { type: "blockquote"; text: string[] }
  | { type: "code"; language: string; content: string };

interface PostMarkdownProps {
  content: string;
  className?: string;
}

function parseMarkdown(content: string): MarkdownBlock[] {
  const lines = content.replace(/\r\n/g, "\n").split("\n");
  const blocks: MarkdownBlock[] = [];
  let index = 0;

  while (index < lines.length) {
    const line = lines[index];
    const trimmed = line.trim();

    if (!trimmed) {
      index += 1;
      continue;
    }

    if (trimmed.startsWith("```")) {
      const language = trimmed.slice(3).trim();
      const contentLines: string[] = [];
      index += 1;

      while (index < lines.length && !lines[index].trim().startsWith("```")) {
        contentLines.push(lines[index]);
        index += 1;
      }

      if (index < lines.length) {
        index += 1;
      }

      blocks.push({
        type: "code",
        language,
        content: contentLines.join("\n"),
      });
      continue;
    }

    const headingMatch = trimmed.match(/^(#|##|###)\s+(.*)$/);
    if (headingMatch) {
      blocks.push({
        type: "heading",
        level: headingMatch[1].length as 1 | 2 | 3,
        text: headingMatch[2],
      });
      index += 1;
      continue;
    }

    if (trimmed.startsWith("- ")) {
      const items: string[] = [];

      while (index < lines.length && lines[index].trim().startsWith("- ")) {
        items.push(lines[index].trim().slice(2).trim());
        index += 1;
      }

      blocks.push({ type: "list", items });
      continue;
    }

    if (trimmed.startsWith("> ")) {
      const quoteLines: string[] = [];

      while (index < lines.length && lines[index].trim().startsWith("> ")) {
        quoteLines.push(lines[index].trim().slice(2).trim());
        index += 1;
      }

      blocks.push({ type: "blockquote", text: quoteLines });
      continue;
    }

    const paragraphLines: string[] = [];

    while (index < lines.length) {
      const current = lines[index].trim();

      if (
        !current ||
        current.startsWith("```") ||
        current.startsWith("- ") ||
        current.startsWith("> ") ||
        /^(#|##|###)\s+/.test(current)
      ) {
        break;
      }

      paragraphLines.push(current);
      index += 1;
    }

    blocks.push({
      type: "paragraph",
      text: paragraphLines.join(" "),
    });
  }

  return blocks;
}

function renderInline(text: string, keyPrefix: string) {
  const nodes: React.ReactNode[] = [];
  const regex = /(`[^`]+`)|(\[[^\]]+\]\([^)]+\))/g;
  let lastIndex = 0;
  let match: RegExpExecArray | null;

  while ((match = regex.exec(text)) !== null) {
    if (match.index > lastIndex) {
      nodes.push(text.slice(lastIndex, match.index));
    }

    const token = match[0];

    if (token.startsWith("`")) {
      nodes.push(
        <code
          key={`${keyPrefix}-code-${match.index}`}
          className="rounded-md bg-secondary px-1.5 py-0.5 font-mono text-[0.95em] text-foreground"
        >
          {token.slice(1, -1)}
        </code>,
      );
    } else {
      const linkMatch = token.match(/^\[([^\]]+)\]\(([^)]+)\)$/);

      if (linkMatch) {
        const [, label, href] = linkMatch;
        const isExternal = /^https?:\/\//.test(href);

        nodes.push(
          <a
            key={`${keyPrefix}-link-${match.index}`}
            href={href}
            className="font-medium text-primary underline underline-offset-4 transition-colors hover:text-primary/80"
            target={isExternal ? "_blank" : undefined}
            rel={isExternal ? "noopener noreferrer" : undefined}
          >
            {label}
          </a>,
        );
      } else {
        nodes.push(token);
      }
    }

    lastIndex = regex.lastIndex;
  }

  if (lastIndex < text.length) {
    nodes.push(text.slice(lastIndex));
  }

  return nodes;
}

export function PostMarkdown({ content, className }: PostMarkdownProps) {
  const blocks = parseMarkdown(content);

  return (
    <div className={cn("space-y-6 text-base leading-8 text-foreground", className)}>
      {blocks.map((block, index) => {
        const key = `block-${index}`;

        if (block.type === "heading") {
          if (block.level === 1) {
            return (
              <h1
                key={key}
                className="mt-10 text-3xl font-semibold tracking-tight text-foreground sm:text-4xl"
              >
                {renderInline(block.text, key)}
              </h1>
            );
          }

          if (block.level === 2) {
            return (
              <h2
                key={key}
                className="mt-10 text-2xl font-semibold tracking-tight text-foreground"
              >
                {renderInline(block.text, key)}
              </h2>
            );
          }

          return (
            <h3
              key={key}
              className="mt-8 text-xl font-semibold tracking-tight text-foreground"
            >
              {renderInline(block.text, key)}
            </h3>
          );
        }

        if (block.type === "paragraph") {
          return (
            <p key={key} className="text-base leading-8 text-muted-foreground">
              {renderInline(block.text, key)}
            </p>
          );
        }

        if (block.type === "list") {
          return (
            <ul
              key={key}
              className="space-y-3 pl-5 text-base leading-8 text-muted-foreground"
            >
              {block.items.map((item, itemIndex) => (
                <li key={`${key}-item-${itemIndex}`} className="list-disc">
                  {renderInline(item, `${key}-item-${itemIndex}`)}
                </li>
              ))}
            </ul>
          );
        }

        if (block.type === "blockquote") {
          return (
            <blockquote
              key={key}
              className="rounded-2xl border-l-4 border-primary/50 bg-secondary/40 px-5 py-4 text-base leading-8 text-foreground"
            >
              {block.text.map((line, lineIndex) => (
                <Fragment key={`${key}-quote-${lineIndex}`}>
                  {lineIndex > 0 ? <br /> : null}
                  {renderInline(line, `${key}-quote-${lineIndex}`)}
                </Fragment>
              ))}
            </blockquote>
          );
        }

        return (
          <pre
            key={key}
            className="overflow-x-auto rounded-2xl bg-secondary px-5 py-4 text-sm leading-7 text-foreground"
          >
            {block.language ? (
              <p className="mb-3 text-xs uppercase tracking-[0.2em] text-muted-foreground">
                {block.language}
              </p>
            ) : null}
            <code>{block.content}</code>
          </pre>
        );
      })}
    </div>
  );
}

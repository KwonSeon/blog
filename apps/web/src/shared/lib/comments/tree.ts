export type CommentItem = { id: string; parentId: string | null; depth: number; author: string; body: string; createdAt: string; deleted: boolean; canEdit: boolean; canDelete: boolean };
// Iterative traversal keeps deeply nested replies off the JavaScript call stack.
export function commentTree(items: CommentItem[]): CommentItem[] {
  const unique = new Map(items.map(item => [item.id, item]));
  const children = new Map<string | null, CommentItem[]>();
  const sorted = [...unique.values()].sort((a, b) => BigInt(a.id) < BigInt(b.id) ? -1 : BigInt(a.id) > BigInt(b.id) ? 1 : 0);
  for (const item of sorted) {
    const key = item.parentId && unique.has(item.parentId) ? item.parentId : null;
    const list = children.get(key) || []; list.push(item); children.set(key, list);
  }
  const stack = [...(children.get(null) || [])].reverse(), result: CommentItem[] = [], seen = new Set<string>();
  while (stack.length) { const item = stack.pop()!; if (seen.has(item.id)) continue; seen.add(item.id); result.push(item); const replies = children.get(item.id) || []; for (let i = replies.length - 1; i >= 0; i--) stack.push(replies[i]); }
  return result;
}

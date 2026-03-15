import { NextResponse } from "next/server";
import { listPublicPosts } from "@/src/shared/lib/api/public-api";

interface PostsProxyRouteProps {
  request: Request;
}

export async function GET(request: PostsProxyRouteProps["request"]) {
  const url = new URL(request.url);
  const q = url.searchParams.get("q")?.trim() ?? undefined;
  const lang = url.searchParams.get("lang")?.trim() ?? undefined;
  const response = await listPublicPosts({
    q,
    lang,
  });

  return NextResponse.json(response);
}

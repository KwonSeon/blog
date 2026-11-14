import { NextResponse } from "next/server";
import { listPublicPosts } from "@/src/shared/lib/api/public-api";

interface PostsProxyRouteProps {
  request: Request;
}

export async function GET(request: PostsProxyRouteProps["request"]) {
  const url = new URL(request.url);
  const q = url.searchParams.get("q")?.trim() ?? undefined;
  const lang = url.searchParams.get("lang")?.trim() ?? undefined;
  if ((q && (q.length > 80 || /[\u0000-\u001f\u007f]/.test(q))) ||
      (lang && !/^[a-z]{2,3}(?:-[A-Z]{2})?$/.test(lang))) {
    return NextResponse.json({ message: "검색어는 80자 이내로 입력해 주세요." }, { status: 400 });
  }
  const response = await listPublicPosts({
    q,
    lang,
  });

  return NextResponse.json(response);
}

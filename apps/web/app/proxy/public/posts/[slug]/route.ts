import { NextResponse } from "next/server";
import { getPublicPost } from "@/src/shared/lib/api/public-api";

interface PostProxyRouteProps {
  params: Promise<{
    slug: string;
  }>;
}

export async function GET(_request: Request, { params }: PostProxyRouteProps) {
  const { slug } = await params;
  const response = await getPublicPost(slug);

  if (!response) {
    return NextResponse.json(
      { message: "글을 찾을 수 없습니다." },
      { status: 404 },
    );
  }

  return NextResponse.json(response);
}

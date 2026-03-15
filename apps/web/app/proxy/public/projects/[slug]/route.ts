import { NextResponse } from "next/server";
import { getPublicProject } from "@/src/shared/lib/api/public-api";

interface ProjectProxyRouteProps {
  params: Promise<{
    slug: string;
  }>;
}

export async function GET(_request: Request, { params }: ProjectProxyRouteProps) {
  const { slug } = await params;
  const response = await getPublicProject(slug);

  if (!response) {
    return NextResponse.json(
      { message: "프로젝트를 찾을 수 없습니다." },
      { status: 404 },
    );
  }

  return NextResponse.json(response);
}

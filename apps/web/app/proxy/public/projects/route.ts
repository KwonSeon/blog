import { NextResponse } from "next/server";
import { listPublicProjects } from "@/src/shared/lib/api/public-api";

export async function GET() {
  const response = await listPublicProjects();

  return NextResponse.json(response);
}

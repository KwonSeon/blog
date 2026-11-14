import { cookies } from "next/headers";
import { decodeJwt } from "jose";
import {
  editorToken,
  sameOrigin,
  cookieOptions,
} from "@/src/shared/lib/auth/session";
import { openIntent, intentName } from "@/src/shared/lib/server/media-intent";
import { proxyAdminApiRequest } from "@/src/shared/lib/server/admin-api-proxy";
export async function POST(request: Request) {
  if (!sameOrigin(request)) return new Response(null, { status: 403 });
  const token = await editorToken(true);
  if (!token) return new Response(null, { status: 401 });
  const input = await request
    .clone()
    .json()
    .catch(() => null);
  if (!input || !/^\d+$/.test(String(input.mediaAssetId)))
    return new Response(null, { status: 400 });
  const jar = await cookies(),
    name = intentName(String(input.mediaAssetId));
  try {
    const intent = await openIntent(jar.get(name)?.value ?? "");
    if (
      intent.sub !== decodeJwt(token).sub ||
      intent.id !== String(input.mediaAssetId) ||
      intent.objectKey !== input.objectKey ||
      intent.originalFilename !== input.originalFilename
    )
      return new Response(null, { status: 403 });
  } catch {
    return Response.json(
      { message: "업로드 요청이 만료되었습니다. 파일을 다시 선택해 주세요." },
      { status: 403 },
    );
  }
  const response = await proxyAdminApiRequest({
    request,
    target: "media",
    path: "/admin/media/uploads/complete",
  });
  if (response.ok)
    jar.set(name, "", {
      ...cookieOptions,
      path: "/admin/api/media",
      maxAge: 0,
    });
  return response;
}

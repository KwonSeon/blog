import { parseApiJson } from "@/src/shared/lib/api/json";
import { cookies } from "next/headers";
import { decodeJwt } from "jose";
import { sameOrigin, cookieOptions } from "@/src/shared/lib/auth/session";
import { sealIntent, intentName } from "@/src/shared/lib/server/media-intent";
import { serverRuntimeConfig } from "@/src/shared/config/server-runtime";
import {
  clearAdminSessionCookie,
  readAdminSessionToken,
} from "@/src/shared/lib/auth/admin-session.server";

interface PresignUploadApiResponse {
  mediaAssetId?: string | number;
  bucket?: string;
  objectKey?: string;
  uploadUrl?: string;
  httpMethod?: string;
  uploadStatus?: string;
  expiresAt?: string;
  message?: string;
}

function rewriteUploadUrl(uploadUrl: string) {
  const rewrittenUrl = new URL(uploadUrl);
  const publicMediaApiUrl = new URL(
    process.env.MEDIA_PUBLIC_BASE_URL ?? serverRuntimeConfig.mediaApiBaseUrl,
  );

  rewrittenUrl.protocol = publicMediaApiUrl.protocol;
  rewrittenUrl.hostname = publicMediaApiUrl.hostname;
  rewrittenUrl.port = publicMediaApiUrl.port;

  return rewrittenUrl.toString();
}

export async function POST(request: Request) {
  if (!sameOrigin(request)) return new Response(null, { status: 403 });
  const accessToken = await readAdminSessionToken(true);

  if (!accessToken) {
    return Response.json(
      {
        message: "관리자 인증이 필요합니다. 다시 로그인하세요.",
      },
      {
        status: 401,
      },
    );
  }

  const contentType = request.headers.get("content-type");
  const input = await request.json().catch(() => null);
  if (
    !input ||
    !["post-cover", "post-inline", "project-cover", "project-inline"].includes(
      input.namespace,
    ) ||
    !["image/jpeg", "image/png", "image/webp", "image/gif"].includes(
      input.contentType,
    ) ||
    !Number.isSafeInteger(input.sizeBytes) ||
    input.sizeBytes < 1 ||
    input.sizeBytes > 20 * 1024 * 1024 ||
    typeof input.originalFilename !== "string" ||
    input.originalFilename.length > 255
  )
    return Response.json(
      { message: "20MB 이하의 JPG, PNG, WebP, GIF 이미지를 선택해 주세요." },
      { status: 400 },
    );
  const body = JSON.stringify(input);

  try {
    const upstreamResponse = await fetch(
      `${serverRuntimeConfig.mediaApiBaseUrl}/admin/media/uploads/presign`,
      {
        method: "POST",
        cache: "no-store",
        headers: {
          Accept: "application/json",
          Authorization: `Bearer ${accessToken}`,
          ...(contentType ? { "Content-Type": contentType } : {}),
        },
        body,
      },
    );

    const payload =
      ((await upstreamResponse
        .text()
        .then(parseApiJson)
        .catch(() => null)) as PresignUploadApiResponse | null) ?? null;

    if (upstreamResponse.status === 401 || upstreamResponse.status === 403) {
      await clearAdminSessionCookie();
    }

    if (!upstreamResponse.ok) {
      return Response.json(
        {
          message: payload?.message ?? "이미지 업로드 URL 발급에 실패했습니다.",
        },
        {
          status: upstreamResponse.status,
        },
      );
    }

    if (
      !payload?.mediaAssetId ||
      !payload.objectKey ||
      !payload.uploadUrl ||
      !payload.httpMethod ||
      !payload.uploadStatus ||
      !payload.expiresAt
    ) {
      return Response.json(
        {
          message: "이미지 업로드 URL 응답이 올바르지 않습니다.",
        },
        {
          status: 502,
        },
      );
    }

    const id = String(payload.mediaAssetId);
    (await cookies()).set(
      intentName(id),
      await sealIntent({
        sub: decodeJwt(accessToken).sub,
        id,
        objectKey: payload.objectKey,
        originalFilename: input.originalFilename,
      }),
      { ...cookieOptions, maxAge: 900, path: "/admin/api/media" },
    );
    return Response.json(
      {
        ...payload,
        uploadUrl: rewriteUploadUrl(payload.uploadUrl),
      },
      {
        status: upstreamResponse.status,
      },
    );
  } catch {
    return Response.json(
      {
        message: "이미지 업로드 URL 프록시 요청 중 오류가 발생했습니다.",
      },
      {
        status: 502,
      },
    );
  }
}

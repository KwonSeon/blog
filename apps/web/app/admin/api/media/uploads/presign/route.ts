import { serverRuntimeConfig } from "@/src/shared/config/server-runtime";
import {
  clearAdminSessionCookie,
  readAdminSessionToken,
} from "@/src/shared/lib/auth/admin-session.server";

interface PresignUploadApiResponse {
  mediaAssetId?: number;
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
  const publicMediaApiUrl = new URL(serverRuntimeConfig.mediaApiBaseUrl);

  rewrittenUrl.protocol = publicMediaApiUrl.protocol;
  rewrittenUrl.hostname = publicMediaApiUrl.hostname;
  rewrittenUrl.port = publicMediaApiUrl.port;

  return rewrittenUrl.toString();
}

export async function POST(request: Request) {
  const accessToken = await readAdminSessionToken();

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
  const body = await request.text();

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
      ((await upstreamResponse.json().catch(() => null)) as PresignUploadApiResponse | null) ??
      null;

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
      !payload?.mediaAssetId
      || !payload.objectKey
      || !payload.uploadUrl
      || !payload.httpMethod
      || !payload.uploadStatus
      || !payload.expiresAt
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

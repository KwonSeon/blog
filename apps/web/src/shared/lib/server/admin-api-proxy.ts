import "server-only";
import { parseApiJson } from "@/src/shared/lib/api/json";

import { serverRuntimeConfig } from "@/src/shared/config/server-runtime";
import {
  clearAdminSessionCookie,
  readAdminSessionToken,
} from "@/src/shared/lib/auth/admin-session.server";

import { sameOrigin } from "@/src/shared/lib/auth/session";

type AdminApiTarget = "blog" | "media";

interface ProxyAdminApiRequestOptions {
  request: Request;
  target: AdminApiTarget;
  path: string;
}

function resolveApiBaseUrl(target: AdminApiTarget) {
  return target === "blog"
    ? serverRuntimeConfig.blogApiBaseUrl
    : serverRuntimeConfig.mediaApiBaseUrl;
}

async function createProxyResponse(response: Response) {
  const headers = new Headers();
  const contentType = response.headers.get("content-type");

  if (contentType) {
    headers.set("content-type", contentType);
  }

  headers.set("cache-control", "no-store");

  const body = contentType?.includes("application/json")
    ? JSON.stringify(parseApiJson(await response.text()))
    : response.body;
  return new Response(body, {
    status: response.status,
    headers,
  });
}

function hasRequestBody(method: string) {
  return !["GET", "HEAD"].includes(method.toUpperCase());
}

export async function proxyAdminApiRequest({
  request,
  target,
  path,
}: ProxyAdminApiRequestOptions) {
  if (hasRequestBody(request.method) && !sameOrigin(request))
    return new Response(null, { status: 403 });
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

  const upstreamUrl = `${resolveApiBaseUrl(target)}${path}`;
  const contentType = request.headers.get("content-type");
  const accept = request.headers.get("accept");
  const bodyText = hasRequestBody(request.method) ? await request.text() : "";

  try {
    const upstreamResponse = await fetch(upstreamUrl, {
      method: request.method,
      cache: "no-store",
      headers: {
        Accept: accept ?? "application/json",
        Authorization: `Bearer ${accessToken}`,
        ...(contentType ? { "Content-Type": contentType } : {}),
      },
      body: bodyText.length > 0 ? bodyText : undefined,
    });

    if (upstreamResponse.status === 401 || upstreamResponse.status === 403) {
      await clearAdminSessionCookie();
    }

    return createProxyResponse(upstreamResponse);
  } catch {
    return Response.json(
      {
        message: "관리자 API 프록시 요청 중 오류가 발생했습니다.",
      },
      {
        status: 502,
      },
    );
  }
}

import * as oidc from "openid-client";
import { cookies } from "next/headers";
import {
  ssoClient,
  ssoSettings,
  sealTransaction,
  SSO_TRANSACTION_COOKIE,
  SSO_COOKIE_PATH,
} from "@/src/shared/lib/auth/sso";
export async function GET() {
  try {
    const verifier = oidc.randomPKCECodeVerifier(),
      state = oidc.randomState(),
      nonce = oidc.randomNonce();
    const url = oidc.buildAuthorizationUrl(await ssoClient(), {
      redirect_uri: ssoSettings().redirectUri.href,
      scope: "openid profile email",
      code_challenge: await oidc.calculatePKCECodeChallenge(verifier),
      code_challenge_method: "S256",
      state,
      nonce,
    });
    (await cookies()).set(
      SSO_TRANSACTION_COOKIE,
      sealTransaction({ verifier, state, nonce, expires: Date.now() + 300000 }),
      {
        httpOnly: true,
        secure: true,
        sameSite: "lax",
        path: SSO_COOKIE_PATH,
        maxAge: 300,
      },
    );
    return Response.redirect(url, 302);
  } catch {
    return Response.json(
      { message: "통합 로그인 연결을 준비 중입니다." },
      { status: 503 },
    );
  }
}

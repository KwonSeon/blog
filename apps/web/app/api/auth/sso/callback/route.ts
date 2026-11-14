import * as oidc from "openid-client";
import { cookies } from "next/headers";
import {
  ssoClient,
  safeReturnPath,
  ssoSettings,
  openTransaction,
  SSO_TRANSACTION_COOKIE,
  SSO_COOKIE_PATH,
} from "@/src/shared/lib/auth/sso";
import { saveTokens } from "@/src/shared/lib/auth/session";
export async function GET(request: Request) {
  const jar = await cookies(),
    value = jar.get(SSO_TRANSACTION_COOKIE)?.value;
  jar.set(SSO_TRANSACTION_COOKIE, "", {
    httpOnly: true,
    secure: true,
    sameSite: "lax",
    path: SSO_COOKIE_PATH,
    maxAge: 0,
  });
  try {
    if (!value) throw new Error("Missing transaction");
    const transaction = openTransaction(value),
      settings = ssoSettings();
    const url = new URL(settings.redirectUri);
    url.search = new URL(request.url).search;
    const tokens = await oidc.authorizationCodeGrant(await ssoClient(), url, {
      pkceCodeVerifier: transaction.verifier,
      expectedState: transaction.state,
      expectedNonce: transaction.nonce,
      idTokenExpected: true,
    });
    await saveTokens(tokens);
    return Response.redirect(new URL(safeReturnPath(transaction.returnTo), settings.redirectUri), 303);
  } catch {
    return Response.json(
      {
        message:
          "로그인을 완료하지 못했습니다. 로그인 화면에서 다시 시도해 주세요.",
      },
      { status: 401 },
    );
  }
}

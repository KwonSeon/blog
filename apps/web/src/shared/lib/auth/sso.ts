import { createHmac, timingSafeEqual } from "node:crypto";
import * as oidc from "openid-client";

export const SSO_MODE_COOKIE = "blog_auth_mode";
export const SSO_TRANSACTION_COOKIE = "blog_sso_transaction";
export const SSO_COOKIE_PATH = "/api/auth/sso";

export function ssoSettings() {
  if (process.env.IDENTITY_SSO_ENABLED !== "true")
    throw new Error("SSO disabled");
  const issuer = new URL(process.env.IDENTITY_SSO_ISSUER || "");
  const redirectUri = new URL(process.env.IDENTITY_SSO_REDIRECT_URI || "");
  const clientId = process.env.IDENTITY_SSO_CLIENT_ID || "";
  const secret = process.env.IDENTITY_SSO_CLIENT_SECRET || "";
  for (const url of [issuer, redirectUri]) {
    if (url.protocol !== "https:" || url.username || url.password || url.hash)
      throw new Error("SSO requires HTTPS");
  }
  if (
    !clientId ||
    secret.length < 32 ||
    redirectUri.pathname !== "/api/auth/sso/callback" ||
    redirectUri.search
  )
    throw new Error("Invalid SSO configuration");
  return { issuer, redirectUri, clientId, secret };
}

let configuration: Promise<oidc.Configuration> | undefined;
export function ssoClient() {
  const { issuer, clientId, secret } = ssoSettings();
  configuration ??= oidc
    .discovery(issuer, clientId, secret, oidc.ClientSecretBasic(secret), {
      execute: [oidc.enableNonRepudiationChecks],
      timeout: 10,
    })
    .catch((error) => {
      configuration = undefined;
      throw error;
    });
  return configuration;
}

export function safeReturnPath(value: unknown) {
  return typeof value === "string" && /^\/(posts|projects)\/[A-Za-z0-9_-]{1,150}$/.test(value) ? value : "/account";
}

type Transaction = {
  returnTo?: string;
  state: string;
  nonce: string;
  verifier: string;
  expires: number;
};
export function sealTransaction(value: Transaction) {
  const data = Buffer.from(JSON.stringify(value)).toString("base64url");
  return `${data}.${createHmac("sha256", ssoSettings().secret).update(data).digest("base64url")}`;
}
export function openTransaction(value: string): Transaction {
  if (value.length > 2048) throw new Error("Invalid transaction");
  const [data, signature, extra] = value.split(".");
  const expected = createHmac("sha256", ssoSettings().secret)
    .update(data || "")
    .digest();
  const actual = Buffer.from(signature || "", "base64url");
  if (
    extra ||
    actual.length !== expected.length ||
    !timingSafeEqual(actual, expected)
  )
    throw new Error("Invalid transaction");
  const parsed = JSON.parse(
    Buffer.from(data, "base64url").toString(),
  ) as Transaction;
  if (
    !Number.isFinite(parsed.expires) ||
    parsed.expires < Date.now() ||
    ![parsed.state, parsed.nonce, parsed.verifier].every(
      (v) => typeof v === "string" && /^[\w-]{43,128}$/.test(v),
    )
  )
    throw new Error("Expired transaction");
  return parsed;
}
export async function refreshSsoToken(refreshToken: string) {
  return oidc.refreshTokenGrant(await ssoClient(), refreshToken);
}
export async function revokeSsoToken(refreshToken: string) {
  await oidc.tokenRevocation(await ssoClient(), refreshToken, {
    token_type_hint: "refresh_token",
  });
}

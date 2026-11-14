import "server-only";
import { createHash } from "node:crypto";
import { EncryptJWT, jwtDecrypt, type JWTPayload } from "jose";
import { ssoSettings } from "@/src/shared/lib/auth/sso";
const key = () =>
  createHash("sha256")
    .update(`blog-media-intent:${ssoSettings().secret}`)
    .digest();
export async function sealIntent(value: JWTPayload) {
  return new EncryptJWT(value)
    .setProtectedHeader({ alg: "dir", enc: "A256GCM" })
    .setIssuedAt()
    .setExpirationTime("15m")
    .encrypt(key());
}
export async function openIntent(value: string) {
  return (await jwtDecrypt(value, key())).payload;
}
export const intentName = (id: string) => `blog.upload.${id}`;

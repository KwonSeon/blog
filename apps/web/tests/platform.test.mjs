import { test } from "node:test";
import assert from "node:assert/strict";
import {
  sealTransaction,
  openTransaction,
  ssoSettings,
} from "../src/shared/lib/auth/sso.ts";
import { parseApiJson } from "../src/shared/lib/api/json.ts";
process.env.IDENTITY_SSO_ENABLED = "true";
process.env.IDENTITY_SSO_ISSUER = "https://identity.example.test";
process.env.IDENTITY_SSO_REDIRECT_URI =
  "https://blog.example.test/api/auth/sso/callback";
process.env.IDENTITY_SSO_CLIENT_ID = "blog";
process.env.IDENTITY_SSO_CLIENT_SECRET = "test-only-secret-".repeat(4);
test("SSO transaction rejects forged signature and expiry", () => {
  const original = {
    state: "a".repeat(43),
    nonce: "b".repeat(43),
    verifier: "c".repeat(43),
    expires: Date.now() + 300000,
  };
  assert.deepEqual(openTransaction(sealTransaction(original)), original);
  assert.throws(() => openTransaction(sealTransaction(original) + "x"));
  assert.throws(() =>
    openTransaction(sealTransaction({ ...original, expires: 0 })),
  );
});
test("SSO rejects unsafe callback configuration", () => {
  const saved = process.env.IDENTITY_SSO_REDIRECT_URI;
  for (const value of [
    "http://blog.example.test/api/auth/sso/callback",
    "https://blog.example.test/other",
    "https://blog.example.test/api/auth/sso/callback?next=evil",
  ]) {
    process.env.IDENTITY_SSO_REDIRECT_URI = value;
    assert.throws(ssoSettings);
  }
  process.env.IDENTITY_SSO_REDIRECT_URI = saved;
});
test("64-bit IDs survive nested API responses without rounding counts", () => {
  const data = parseApiJson(
    '{"content":[{"postId":9223372036854775806,"coverMediaAssetId":123456789123456789}],"totalCount":2}',
  );
  assert.equal(data.content[0].postId, "9223372036854775806");
  assert.equal(data.content[0].coverMediaAssetId, "123456789123456789");
  assert.equal(data.totalCount, 2);
});

test("login return paths reject external URLs and encoded redirects", async () => {
  const { safeReturnPath } = await import("../src/shared/lib/auth/sso.ts");
  assert.equal(safeReturnPath("/posts/example"), "/posts/example");
  assert.equal(safeReturnPath("/projects/themappin"), "/projects/themappin");
  for (const path of ["//evil.example", "https://evil.example", "/posts/../admin", "/posts/%2f%2fevil", "/posts/a?next=evil", null])
    assert.equal(safeReturnPath(path), "/account");
});

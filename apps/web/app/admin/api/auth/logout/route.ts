import { clearAdminSessionCookie } from "@/src/shared/lib/auth/admin-session.server";

export async function POST() {
  await clearAdminSessionCookie();

  return Response.json(
    {
      ok: true,
    },
    {
      status: 200,
    },
  );
}

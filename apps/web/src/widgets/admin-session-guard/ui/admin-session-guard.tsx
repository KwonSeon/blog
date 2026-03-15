import { redirect } from "next/navigation";
import { buildAdminLoginPath } from "@/src/shared/lib/auth/admin-path";
import { readAdminSessionToken } from "@/src/shared/lib/auth/admin-session.server";

interface AdminSessionGuardProps {
  children: React.ReactNode;
  nextPath: string;
}

export async function AdminSessionGuard({
  children,
  nextPath,
}: AdminSessionGuardProps) {
  const accessToken = await readAdminSessionToken();

  if (!accessToken) {
    redirect(buildAdminLoginPath(nextPath));
  }

  return <>{children}</>;
}

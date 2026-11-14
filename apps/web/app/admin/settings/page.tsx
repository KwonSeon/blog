import { redirect } from "next/navigation";
import { editorToken } from "@/src/shared/lib/auth/session";
import { getSiteSettings } from "@/src/shared/lib/server/site-settings";
import { BlogSettingsEditor } from "@/src/widgets/blog-settings-editor";
import { Container } from "@/src/shared/ui";
export const metadata = {
  title: "블로그 꾸미기",
  robots: { index: false, follow: false },
};
export default async function Settings() {
  if (!(await editorToken())) redirect("/admin/login");
  const settings = await getSiteSettings();
  return (
    <Container size="wide" className="py-10">
      <h1 className="text-3xl font-semibold">블로그 꾸미기</h1>
      <p className="mt-4 mb-8 text-muted-foreground">
        레이아웃과 기본 정보를 미리 보고 저장하세요. 변경사항은 재배포 없이
        적용됩니다.
      </p>
      <p className="mb-6"><a className="underline" href="/admin/monetization">광고 준비 상태 확인</a></p>
      <p className="mb-6"><a className="underline" href="/admin/notifications">외부 알림 설정</a></p>
      <BlogSettingsEditor initial={settings} />
    </Container>
  );
}

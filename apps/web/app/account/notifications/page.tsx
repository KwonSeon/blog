import { activeToken } from "@/src/shared/lib/auth/session";
import { Container, CTAButton } from "@/src/shared/ui";
import { NotificationInbox } from "@/src/widgets/notification-inbox";
export const metadata = { title: "알림함", robots: { index: false, follow: false } };
export const dynamic = "force-dynamic";
export default async function Notifications() {
  const token = await activeToken();
  return <Container size="narrow" className="py-16"><div className="mb-8 flex flex-wrap items-center justify-between gap-4"><h1 className="text-3xl font-semibold">알림함</h1><CTAButton href="/account" variant="secondary">내 계정</CTAButton></div>
    {token ? <NotificationInbox /> : <><p className="mb-6">내 댓글의 답글과 작성한 글의 새 댓글을 확인하세요.</p><CTAButton href="/api/auth/sso/start?next=%2Faccount%2Fnotifications">로그인하고 알림 보기</CTAButton></>}
  </Container>;
}

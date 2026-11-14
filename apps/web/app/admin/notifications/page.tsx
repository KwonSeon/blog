import {redirect} from "next/navigation";
import {editorToken} from "@/src/shared/lib/auth/session";
import {Container} from "@/src/shared/ui";
import {DeliveryPanel} from "./panel";
export const metadata = {title:"외부 알림", robots:{index:false,follow:false}};
export default async function Notifications() {
  if (!(await editorToken())) redirect("/admin/login");
  return <Container size="narrow" className="space-y-8 py-12"><h1 className="text-3xl font-semibold">외부 알림</h1>
    <p className="leading-7">댓글과 답글은 회원 알림함에서 확인할 수 있습니다. Telegram은 수신에 동의한 계정에 새 알림 안내와 알림함 링크를 보냅니다.</p>
    <DeliveryPanel />
    <p className="text-sm text-muted-foreground">카카오 알림톡은 비즈니스 채널과 발송 서비스 준비 후 지원할 예정입니다.</p>
  </Container>;
}

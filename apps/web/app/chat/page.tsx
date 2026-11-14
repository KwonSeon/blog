import {activeToken} from "@/src/shared/lib/auth/session";
import {Container,CTAButton} from "@/src/shared/ui";
import {MemberChat} from "./room";
export const dynamic="force-dynamic";
export const metadata={title:"회원 채팅",robots:{index:false,follow:false}};
export default async function Chat(){
 const token=await activeToken().catch(()=>null);
 return <Container size="narrow" className="space-y-6 py-12"><div className="flex items-center justify-between gap-4"><h1 className="text-3xl font-semibold">회원 채팅</h1><CTAButton href="/account" variant="secondary">내 계정</CTAButton></div>
 <p className="leading-7">로그인한 모든 회원이 함께 보는 자유 대화방입니다. 개인정보나 비밀 정보를 올리지 마세요. 대화 이력은 최근 90일까지 확인할 수 있으며, 본인 메시지는 삭제할 수 있습니다.</p>
 {token?<MemberChat/>:<CTAButton href="/api/auth/sso/start?next=%2Fchat">로그인하고 참여하기</CTAButton>}
 </Container>;
}

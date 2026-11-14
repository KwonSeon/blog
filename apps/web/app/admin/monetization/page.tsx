import {redirect} from "next/navigation";
import {editorToken} from "@/src/shared/lib/auth/session";
import {advertisingConfig} from "@/src/shared/lib/advertising";
import {siteConfig} from "@/src/shared/config/site";
import {Container,CTAButton} from "@/src/shared/ui";
export const metadata={title:"광고 준비 상태",robots:{index:false,follow:false}};
export default async function Monetization(){if(!(await editorToken()))redirect("/admin/login");const config=advertisingConfig(process.env,siteConfig.url);return <Container size="narrow" className="space-y-8 py-12"><h1 className="text-3xl font-semibold">광고 준비 상태</h1><p>이 화면은 운영 설정을 확인합니다. 실제 사이트 승인 여부는 AdSense에서 확인해 주세요.</p><dl className="grid grid-cols-2 gap-4"><dt>발행자 ID</dt><dd>{config.client?"설정됨":"미설정"}</dd><dt>글 광고 단위</dt><dd>{config.articleSlot?"설정됨":"미설정"}</dd><dt>사이트 승인 확인</dt><dd>{config.siteReady?"설정에서 확인됨":"미확인"}</dd><dt>동의 메시지 준비</dt><dd>{config.cmpReady?"설정에서 확인됨":"미확인"}</dd><dt>광고 상태</dt><dd>{config.enabled?"켜짐":"꺼짐"}</dd></dl><p className="text-sm leading-7 text-muted-foreground">소개·문의 페이지를 갖추는 것만으로 광고 승인이 보장되지는 않습니다. 공개한 글과 프로젝트가 독자에게 도움이 되는 독창적인 내용을 갖췄는지 검토한 뒤 신청하세요. 개발 주소에는 실제 광고를 요청하지 않습니다.</p><div className="flex flex-wrap gap-3"><CTAButton href="https://adsense.google.com/" external>AdSense에서 확인</CTAButton><CTAButton href="/admin/settings" variant="secondary">블로그 설정</CTAButton></div></Container>}

import { Container } from "@/src/shared/ui";
import { NewsletterConfirmation } from "@/src/widgets/newsletter-confirmation";
export const metadata = { title: "구독 확인", robots: { index: false, follow: false }, referrer: "no-referrer" as const };
export default function Confirm() { return <Container size="narrow" className="py-16"><h1 className="mb-6 text-3xl font-semibold">이메일 구독 확인</h1><NewsletterConfirmation action="confirm" /></Container>; }

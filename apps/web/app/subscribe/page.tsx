import { Container } from "@/src/shared/ui";
import { NewsletterForm } from "@/src/widgets/newsletter-form";
export const metadata = { title: "이메일 구독", description: "s-nowk의 새 개발 기록을 이메일로 받아보세요." };
export default function Subscribe() { return <Container size="narrow" className="py-16"><h1 className="mb-5 text-3xl font-semibold">새 글을 이메일로 받아보세요</h1><p className="mb-8 leading-7 text-muted-foreground">직접 만든 서비스와 개발 과정의 기록을 새 글이 올라올 때 알려드립니다.</p><NewsletterForm /></Container>; }

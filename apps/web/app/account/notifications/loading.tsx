import { Container } from "@/src/shared/ui";
import { NotificationInboxLoading } from "@/src/widgets/notification-inbox";
export default function Loading() { return <Container size="narrow" className="py-16"><h1 className="mb-8 text-3xl font-semibold">알림함</h1><NotificationInboxLoading /></Container>; }

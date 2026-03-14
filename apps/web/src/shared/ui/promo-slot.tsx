import { cn } from "@/lib/utils";

type PromoSlotSize = "banner" | "rectangle" | "leaderboard";

interface PromoSlotProps {
  slotId?: string;
  size?: PromoSlotSize;
  className?: string;
  showPlaceholder?: boolean;
}

const sizeClasses: Record<PromoSlotSize, string> = {
  banner: "h-[90px]",
  rectangle: "h-[250px] max-w-[300px]",
  leaderboard: "h-[90px] max-w-[728px]",
};

export function PromoSlot({
  slotId,
  size = "banner",
  className,
  showPlaceholder = false,
}: PromoSlotProps) {
  if (!showPlaceholder) {
    return (
      <div
        className={cn("mx-auto w-full", sizeClasses[size], className)}
        data-ad-slot={slotId}
        aria-hidden="true"
      />
    );
  }

  return (
    <div
      className={cn(
        "mx-auto flex w-full items-center justify-center rounded-2xl border border-dashed border-muted-foreground/30 bg-muted/40 px-4 text-xs text-muted-foreground",
        sizeClasses[size],
        className,
      )}
      data-ad-slot={slotId}
      aria-hidden="true"
    >
      광고 또는 프로모션 슬롯
    </div>
  );
}

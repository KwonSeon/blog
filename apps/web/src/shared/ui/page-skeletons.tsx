"use client";

import { Skeleton } from "@astryxdesign/core/Skeleton";
import { Container } from "@/src/shared/ui/container";

function Shape({ width = "100%", height = 18, index = 0 }: { width?: string | number; height?: number; index?: number }) {
  return <Skeleton width={width} height={height} radius={2} index={index} data-skeleton-shape />;
}

function TextLines({ count = 3 }: { count?: number }) {
  return <div className="space-y-3">{Array.from({ length: count }, (_, index) => <Shape key={index} width={index === count - 1 ? "72%" : "100%"} index={index} />)}</div>;
}

function LoadingRegion({ kind, label, children }: { kind: string; label: string; children: React.ReactNode }) {
  return <div className="page-skeleton" data-page-skeleton={kind} role="status" aria-label={label}>
    <span className="sr-only">{label}</span>
    <div aria-hidden="true" aria-busy="true">{children}</div>
  </div>;
}

function PostCards() {
  return <div className="post-skeleton-grid grid gap-6 md:grid-cols-2 xl:grid-cols-3">{[0, 1, 2].map(index => <div key={index} className="space-y-5 rounded-3xl p-5">
    <Shape width={80} height={24} /><Shape width="80%" height={28} /><TextLines /><Shape width="60%" height={14} />
  </div>)}</div>;
}

function ProjectCards() {
  return <div className="grid gap-6 lg:grid-cols-2">{[0, 1].map(index => <div key={index} className="overflow-hidden rounded-[var(--radius-container)] border border-border">
    <Shape height={176} /><div className="space-y-5 p-6"><Shape width="65%" height={28} /><TextLines /><Shape width={140} /></div>
  </div>)}</div>;
}

export function PostsSkeleton() {
  return <LoadingRegion kind="posts" label="글 목록을 불러오는 중입니다"><Container>
    <div className="grid gap-8 py-16 sm:py-20 lg:grid-cols-[minmax(0,1.2fr)_minmax(280px,0.8fr)] lg:py-24">
      <div className="space-y-6"><Shape width={70} height={14} /><Shape width="88%" height={48} /><TextLines count={2} /><div className="flex gap-3 pt-2"><Shape height={48} /><Shape width={96} height={48} /></div><Shape width={180} height={40} /></div>
      <div className="space-y-7 rounded-[var(--radius-container)] border border-border p-8"><Shape width={120} height={14} /><TextLines count={2} />{[0, 1, 2].map(index => <Shape key={index} height={60} />)}</div>
    </div><div className="space-y-6 pb-16"><Shape width={160} height={32} /><Shape height={52} /><PostCards /></div>
  </Container></LoadingRegion>;
}

export function PostDetailSkeleton() {
  return <LoadingRegion kind="post-detail" label="글을 불러오는 중입니다"><Container size="narrow" className="space-y-8 py-12 sm:py-20">
    <Shape width="55%" height={16} /><Shape width={96} height={36} /><Shape width={170} height={16} />
    <div className="space-y-4"><Shape height={48} /><Shape width="65%" height={48} /></div><TextLines count={2} />
    <div className="space-y-9 pt-4"><Shape width="50%" height={28} /><TextLines count={5} /><TextLines count={5} /></div>
  </Container></LoadingRegion>;
}

export function ProjectsSkeleton() {
  return <LoadingRegion kind="projects" label="프로젝트 목록을 불러오는 중입니다"><Container className="py-16">
    <div className="mb-12 max-w-3xl space-y-6"><Shape width={100} height={14} /><Shape width="70%" height={44} /><TextLines count={2} /><Shape width={160} height={16} /></div><ProjectCards />
  </Container></LoadingRegion>;
}

export function ProjectDetailSkeleton() {
  return <LoadingRegion kind="project-detail" label="프로젝트를 불러오는 중입니다"><Container className="space-y-8 py-16">
    <Shape width="45%" height={16} /><div className="max-w-4xl space-y-6"><Shape width={180} height={24} /><Shape width={100} height={14} /><Shape width="80%" height={48} /><TextLines count={2} /><Shape width={240} height={48} /></div>
    <div className="grid gap-6 pt-4 md:grid-cols-2">{[0, 1].map(index => <div key={index} className="space-y-6 rounded-[var(--radius-container)] border border-border p-8"><Shape width="50%" height={28} /><TextLines count={4} /></div>)}</div>
    <div className="max-w-3xl space-y-6 pt-4"><Shape width="45%" height={28} /><TextLines count={5} /></div>
  </Container></LoadingRegion>;
}

export function HomeSkeleton() {
  return <LoadingRegion kind="home" label="블로그를 불러오는 중입니다">
    <div className="home-skeleton-hero border-b border-border py-16 sm:py-24"><Container><div className="max-w-3xl space-y-6"><Shape width={100} height={16} /><Shape width="90%" height={64} /><Shape width="65%" height={64} /><div className="max-w-xl"><TextLines count={2} /></div><Shape width={240} height={48} /></div></Container></div>
    <Container><div className="flex flex-col"><div className="home-skeleton-posts space-y-8 py-16"><Shape width={180} height={32} /><PostCards /></div><div className="home-skeleton-projects space-y-8 py-16"><Shape width={180} height={32} /><ProjectCards /></div></div></Container>
  </LoadingRegion>;
}

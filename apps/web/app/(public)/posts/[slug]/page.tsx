import { ContentLike } from "@/src/widgets/content-like";
import Image from "next/image";
import type { Metadata } from "next";
import { notFound } from "next/navigation";
import { PostMarkdown, PostRelatedProjectCta } from "@/src/entities/post";
import { getPostDetailPageData } from "@/src/shared/lib/api/public-page-data";
import { buildPublicMetadata } from "@/src/shared/lib/seo/public-metadata";
import { CTAButton, Container } from "@/src/shared/ui";
interface Props {params:Promise<{slug:string}>}
export async function generateMetadata({params}:Props):Promise<Metadata>{
 const {slug}=await params;const data=await getPostDetailPageData(slug);
 if(!data)return {title:"글을 찾을 수 없음",robots:{index:false,follow:false}};
 return buildPublicMetadata({title:data.post.title,description:data.post.excerpt,path:`/posts/${slug}`,type:"article"});
}
export default async function PostDetailPage({params}:Props){
 const {slug}=await params;const data=await getPostDetailPageData(slug);if(!data)notFound();const {post,relatedProject}=data;
 return <Container size="narrow" className="py-12 sm:py-20"><article><header><CTAButton href="/posts" variant="secondary" size="sm">글 목록</CTAButton><div className="mt-8 flex gap-3 text-sm text-muted-foreground"><time dateTime={post.publishedAt}>{new Date(post.publishedAt).toLocaleDateString("ko-KR")}</time><span>{post.readingTime}</span></div><h1 className="mt-4 text-3xl sm:text-5xl font-semibold tracking-tight leading-tight break-keep">{post.title}</h1><p className="mt-6 text-lg leading-8 text-muted-foreground">{post.excerpt}</p></header>{post.coverUrl?<Image src={post.coverUrl} alt={post.title} width={1200} height={675} unoptimized priority className="my-10 w-full rounded-[var(--radius-container)] object-cover"/>:null}<PostMarkdown content={post.contentMd} className="mt-10"/></article><ContentLike kind="posts" id={post.id} path={`/posts/${slug}`} /><div className="mt-16 border-t border-border pt-8">{relatedProject?<PostRelatedProjectCta project={relatedProject}/>:null}<div className="mt-6"><CTAButton href="/posts">다른 글 읽기</CTAButton></div></div></Container>;
}

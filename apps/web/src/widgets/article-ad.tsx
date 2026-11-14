"use client";
import Script from "next/script";
import { useRef } from "react";
declare global {interface Window {adsbygoogle?: Record<string,never>[];googlefc?:{callbackQueue:(()=>void)[];showRevocationMessage?:()=>void};}}
export function ArticleAd({client,slot}:{client:string;slot:string}) {
 const element=useRef<HTMLModElement>(null),requested=useRef(false);
 function requestAd(){if(requested.current||!element.current||element.current.dataset.adsbygoogleStatus)return;requested.current=true;try{(window.adsbygoogle=window.adsbygoogle||[]).push({});}catch{}}
 return <aside aria-label="광고" className="my-12 border-y border-border py-6"><p className="mb-3 text-center text-xs text-muted-foreground">광고</p><ins ref={element} className="adsbygoogle" style={{display:"block",minHeight:280}} data-ad-client={client} data-ad-slot={slot} data-ad-format="auto" data-full-width-responsive="true"/><Script id="blog-adsense" async crossOrigin="anonymous" strategy="afterInteractive" src={`https://pagead2.googlesyndication.com/pagead/js/adsbygoogle.js?client=${client}`} onReady={requestAd}/></aside>;
}

"use client";
import Script from "next/script";
import { Button } from "@astryxdesign/core/Button";
export function AdvertisingChoices({client}:{client:string}){return <><Script id="blog-adsense" async crossOrigin="anonymous" strategy="afterInteractive" src={`https://pagead2.googlesyndication.com/pagead/js/adsbygoogle.js?client=${client}`}/><Button label="광고 개인정보 선택 변경" variant="secondary" onClick={()=>{window.googlefc=window.googlefc||{callbackQueue:[]};window.googlefc.callbackQueue.push(()=>window.googlefc?.showRevocationMessage?.());}}/></>;}

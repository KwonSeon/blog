export function advertisingConfig(env: Record<string,string|undefined>, siteUrl: string) {
  let production=false;try{const url=new URL(siteUrl);production=url.protocol==="https:"&&url.hostname==="s-nowk.com";}catch{}
  const client=/^ca-pub-\d{16}$/.test(env.BLOG_ADSENSE_CLIENT??"")?env.BLOG_ADSENSE_CLIENT!:null;
  const articleSlot=/^\d{6,20}$/.test(env.BLOG_ADSENSE_ARTICLE_SLOT??"")?env.BLOG_ADSENSE_ARTICLE_SLOT!:null;
  const siteReady=env.BLOG_ADSENSE_SITE_READY==="true",cmpReady=env.BLOG_ADSENSE_CMP_READY==="true";
  return {production,client,articleSlot,siteReady,cmpReady,enabled:production&&!!client&&!!articleSlot&&siteReady&&cmpReady&&env.BLOG_ADSENSE_ENABLED==="true"};
}
export function adsText(client: string|null): string|null {return client&&/^ca-pub-\d{16}$/.test(client)?`google.com, ${client.slice(3)}, DIRECT, f08c47fec0942fa0\n`:null;}

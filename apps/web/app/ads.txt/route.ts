import {advertisingConfig,adsText} from "@/src/shared/lib/advertising";
import {siteConfig} from "@/src/shared/config/site";
export const dynamic="force-dynamic";
export function GET(){const config=advertisingConfig(process.env,siteConfig.url);const body=config.production?adsText(config.client):null;return new Response(body,{status:body?200:404,headers:{"Content-Type":"text/plain; charset=utf-8","Cache-Control":"public, max-age=300"}});}

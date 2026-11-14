import test from "node:test";
import assert from "node:assert/strict";
import {advertisingConfig,adsText} from "../src/shared/lib/advertising.ts";
const ready={BLOG_ADSENSE_CLIENT:"ca-pub-1234567890123456",BLOG_ADSENSE_ARTICLE_SLOT:"1234567890",BLOG_ADSENSE_SITE_READY:"true",BLOG_ADSENSE_CMP_READY:"true",BLOG_ADSENSE_ENABLED:"true"};
test("ads require explicit production approval, consent setup, client and slot",()=>{assert.equal(advertisingConfig(ready,"https://s-nowk.com").enabled,true);for(const key of Object.keys(ready)){assert.equal(advertisingConfig({...ready,[key]:undefined},"https://s-nowk.com").enabled,false);}assert.equal(advertisingConfig(ready,"https://blog-dev.s-nowk.com").enabled,false);assert.equal(advertisingConfig(ready,"http://localhost:3000").enabled,false);});
test("publisher text rejects malformed or injected IDs",()=>{assert.equal(adsText(ready.BLOG_ADSENSE_CLIENT),"google.com, pub-1234567890123456, DIRECT, f08c47fec0942fa0\n");assert.equal(adsText("ca-pub-123\nother.com"),null);assert.equal(adsText(null),null);});

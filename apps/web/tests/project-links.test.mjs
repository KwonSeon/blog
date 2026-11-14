import test from 'node:test';
import assert from 'node:assert/strict';
import {safeProjectUrl} from '../src/shared/lib/projects.ts';
test('project links allow HTTPS repositories but reject executable and credential-bearing URLs',()=>{
 assert.equal(safeProjectUrl('https://github.com/KwonSeon/blog'),'https://github.com/KwonSeon/blog');
 for(const value of ['javascript:alert(1)','data:text/html,x','//evil.test','https://user:password@example.com','http://example.com','not a url','']) assert.equal(safeProjectUrl(value),undefined);
});

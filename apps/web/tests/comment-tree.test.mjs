import test from 'node:test';
import assert from 'node:assert/strict';
import {commentTree} from '../src/shared/lib/comments/tree.ts';
const item=(id,parentId=null)=>({id,parentId,depth:0,author:'회원',body:'',createdAt:'',deleted:false,canEdit:false,canDelete:false});
test('comment tree keeps exact large IDs, parent order and latest duplicate state',()=>{
 const a='9007199254741000',b='9007199254741001',c='9007199254741002';
 const rows=commentTree([item(a),item(b),item(c,a),{...item(a),deleted:true}]);assert.deepEqual(rows.map(x=>x.id),[a,c,b]);assert.equal(rows[0].deleted,true);
});
test('deep replies do not recurse or lose descendants',()=>{
 const rows=Array.from({length:20000},(_,i)=>item(String(i+1),i?String(i):null));assert.equal(commentTree(rows).length,20000);
});
test('missing parent remains visible and malicious cycles terminate',()=>{
 assert.equal(commentTree([item('3','2')]).length,1);assert.deepEqual(commentTree([item('1','2'),item('2','1')]),[]);
});

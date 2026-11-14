import test from 'node:test';
import assert from 'node:assert/strict';
import {connectChat} from '../src/shared/lib/chat/socket.ts';
class Socket {
 static OPEN=1;static instances=[];
 readyState=1;sent=[];closed=false;
 constructor(url,protocols){this.url=String(url);this.protocols=protocols;Socket.instances.push(this);}
 send(value){this.sent.push(value);}
 close(){this.closed=true;this.onclose?.();}
}
test('restricted STOMP subscribes after CONNECTED, handles fragmented hints and cleans up',()=>{
 const priorSocket=globalThis.WebSocket,priorLocation=globalThis.location;
 globalThis.WebSocket=Socket;globalThis.location={origin:'https://chat.test'};
 let hints=0;const states=[];let close;
 try{
  close=connectChat(()=>hints++,value=>states.push(value));const socket=Socket.instances.at(-1);
  assert.equal(socket.url,'wss://chat.test/api/chat/ws');socket.onopen();assert.match(socket.sent[0],/^CONNECT\n/);assert.doesNotMatch(socket.sent[0],/token|Authorization/);
  socket.onmessage({data:'CONNECTED\nversion:1.2\n\n\0'});assert.equal(states.at(-1),true);assert.match(socket.sent[1],/destination:\/topic\/member-chat/);
  socket.onmessage({data:'MESSAGE\ndestination:/topic/member-chat\n\n{"cha'});assert.equal(hints,1);
  socket.onmessage({data:'nged":true}\0\n'});assert.equal(hints,2);
  close();assert.equal(socket.closed,true);assert.equal(states.at(-1),true);
 }finally{close?.();globalThis.WebSocket=priorSocket;globalThis.location=priorLocation;}
});
test('oversized or binary broker frames close the socket',()=>{
 const priorSocket=globalThis.WebSocket,priorLocation=globalThis.location;globalThis.WebSocket=Socket;globalThis.location={origin:'https://chat.test'};
 try{for(const data of ['x'.repeat(16385),new Uint8Array(3)]){const stop=connectChat(()=>{},()=>{});const socket=Socket.instances.at(-1);socket.onmessage({data});assert.equal(socket.closed,true);stop();}}
 finally{globalThis.WebSocket=priorSocket;globalThis.location=priorLocation;}
});

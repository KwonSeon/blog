// Restricted STOMP 1.2 consumer: authentication stays in the HttpOnly cookie.
// The only broker payload is an invalidation hint. Message history always comes from HTTP.
export function connectChat(invalidate:()=>void,status:(live:boolean)=>void){
  let stopped=false,socket:WebSocket|undefined,reconnect:ReturnType<typeof setTimeout>|undefined,heartbeat:ReturnType<typeof setInterval>|undefined;
  function connect(){
    if(stopped)return;
    const url=new URL("/api/chat/ws",location.origin);url.protocol=url.protocol==="https:"?"wss:":"ws:";
    socket=new WebSocket(url,["v12.stomp"]);let buffer="",lastReceived=Date.now(),connected=false;
    socket.onopen=()=>socket?.send("CONNECT\naccept-version:1.2\nheart-beat:10000,10000\n\n\0");
    socket.onmessage=event=>{
      if(typeof event.data!=="string"){socket?.close();return;}
      lastReceived=Date.now();buffer+=event.data;if(buffer.length>16384){socket?.close();return;}
      let boundary;
      while((boundary=buffer.indexOf("\0"))>=0){
        const frame=buffer.slice(0,boundary).replace(/^\s+/,"");buffer=buffer.slice(boundary+1);
        if(frame.startsWith("CONNECTED\n")||frame.startsWith("CONNECTED\r\n")){
          connected=true;socket?.send("SUBSCRIBE\nid:member-chat\ndestination:/topic/member-chat\nack:auto\n\n\0");status(true);invalidate();
        }else if(frame.startsWith("MESSAGE\n")||frame.startsWith("MESSAGE\r\n"))invalidate();
        else if(frame.startsWith("ERROR"))socket?.close();
      }
      if(!buffer.trim())buffer="";
    };
    socket.onerror=()=>socket?.close();
    socket.onclose=()=>{if(!stopped)status(false);if(heartbeat)clearInterval(heartbeat);if(!stopped)reconnect=setTimeout(connect,5000);};
    heartbeat=setInterval(()=>{if(Date.now()-lastReceived>(connected?30000:10000)){socket?.close();return;}if(socket?.readyState===WebSocket.OPEN)socket.send("\n");},10000);
  }
  connect();return()=>{stopped=true;if(reconnect)clearTimeout(reconnect);if(heartbeat)clearInterval(heartbeat);socket?.close();};
}

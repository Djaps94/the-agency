package sockets;

import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

@ServerEndpoint(value = "/socket/agents")
public class AgencySocket {
	
	@OnOpen
	public void onOpen(Session session){
		
	}
	
	@OnClose
	public void onClose(Session session){
		
	}
	
	@OnMessage
	public void onMessage(Session session, String message){
		
	}

}

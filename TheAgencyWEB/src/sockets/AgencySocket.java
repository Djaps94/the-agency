package sockets;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import javax.ejb.ActivationConfigProperty;
import javax.ejb.EJB;
import javax.ejb.MessageDriven;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

import com.fasterxml.jackson.databind.ObjectMapper;

import beans.AgencyManagerLocal;
import beans.SessionHolderLocal;
import model.AID;
import model.AgentType;
import util.SocketMessage;

@ServerEndpoint(value = "/socket/agents")
@MessageDriven(activationConfig = {
		@ActivationConfigProperty(propertyName = "destinationType", propertyValue = "javax.jms.Queue"),
		@ActivationConfigProperty(propertyName = "destination", propertyValue = "java:/jms/queue/SocketQueue")
})
public class AgencySocket implements MessageListener{
	
	@EJB
	private SessionHolderLocal sessionHolder;
	
	@EJB
	private AgencyManagerLocal agency;
	
	@OnOpen
	public void onOpen(Session session){
		sessionHolder.addSession(session.getId(), session);
	}
	
	@OnClose
	public void onClose(Session session){
		sessionHolder.removeSession(session.getId());
	}
	
	@OnMessage
	public void onMessage(Session session, String message){
		if(session.isOpen()){
			ObjectMapper mapper = new ObjectMapper();
			try {
				SocketMessage msg = mapper.readValue(message, SocketMessage.class);
				switch(msg.getMsgType()){
				case GET_AGENTS: getRunningAgents(session, mapper); break;
				case  GET_TYPES: getAgentTypes(session, mapper); break;
				default:
					break;
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public void onMessage(Message message) {
		
	}
	
	private void getRunningAgents(Session session, ObjectMapper mapper) throws IOException{
		List<AID> runningAgents = new ArrayList<AID>();
		runningAgents.addAll(agency.getRunningAgents());
		for(Entry<String, List<AID>> entry : agency.getCenterAgents().entrySet()){
			runningAgents.addAll(entry.getValue());
		}
		String output = mapper.writeValueAsString(runningAgents);
		session.getBasicRemote().sendText(output);
	}
	
	private void getAgentTypes(Session session, ObjectMapper mapper) throws IOException{
		Set<AgentType> type = new HashSet<AgentType>();
		type.addAll(agency.getSupportedTypes());
		agency.getOtherSupportedTypes().entrySet().stream().forEach(entry -> type.addAll(entry.getValue()));
		String output = mapper.writeValueAsString(type);
		session.getBasicRemote().sendText(output);
	}

}

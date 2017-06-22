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
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;
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
import util.SocketMessage.messageType;

@ServerEndpoint(value = "/agents")
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
		try {
			SocketMessage msg   = (SocketMessage) ((ObjectMessage)message).getObject();
			ObjectMapper mapper = new ObjectMapper();
			String data = mapper.writeValueAsString(msg);
			for(Entry<String, Session> entry : sessionHolder.getEntry())
				entry.getValue().getBasicRemote().sendText(data);
		} catch (JMSException | IOException e) {
			e.printStackTrace();
		}
	}
	
	private void getRunningAgents(Session session, ObjectMapper mapper) throws IOException{
		List<AID> runningAgents = new ArrayList<AID>();
		runningAgents.addAll(agency.getRunningAgents());
		for(Entry<String, List<AID>> entry : agency.getCenterAgents().entrySet()){
			runningAgents.addAll(entry.getValue());
		}
		SocketMessage msg = new SocketMessage();
		msg.setMsgType(messageType.GET_AGENTS);
		msg.setRunningAgents(runningAgents);
		String output = mapper.writeValueAsString(msg);
		session.getBasicRemote().sendText(output);
	}
	
	private void getAgentTypes(Session session, ObjectMapper mapper) throws IOException{
		Set<AgentType> type = new HashSet<AgentType>();
		type.addAll(agency.getSupportedTypes());
		agency.getOtherSupportedTypes().entrySet().stream().forEach(entry -> type.addAll(entry.getValue()));
		SocketMessage msg = new SocketMessage();
		msg.setMsgType(messageType.GET_TYPES);
		msg.setAgentTypes(type);
		String output = mapper.writeValueAsString(msg);
		session.getBasicRemote().sendText(output);
	}
	
	private void startAgent(Session session, ObjectMapper mapper, AID aid){
		
	}

}

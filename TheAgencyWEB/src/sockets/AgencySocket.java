package sockets;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.TimeoutException;

import javax.ejb.ActivationConfigProperty;
import javax.ejb.EJB;
import javax.ejb.MessageDriven;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;
import javax.websocket.CloseReason;
import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

import com.fasterxml.jackson.databind.ObjectMapper;

import beans.AgencyManagerLocal;
import beans.AgencyRegistryLocal;
import beans.AgentManagerLocal;
import beans.AgentRegistryLocal;
import beans.SessionHolderLocal;
import exceptions.ConnectionException;
import intercommunication.ReceiverLocal;
import model.ACLMessage;
import model.AID;
import model.AgentCenter;
import model.AgentType;
import model.ServiceMessage;
import model.ServiceMessage.OperationType;
import service.MessageRequestLocal;
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
	
	@EJB
	private MessageRequestLocal requester;
	
	@EJB
	private AgencyRegistryLocal registry;
	
	@EJB
	private AgentManagerLocal agentManager;
	
	@EJB
	private ReceiverLocal handler;
	
	@EJB
	private AgentRegistryLocal agentRegistry;
	
	@OnOpen
	public void onOpen(Session session){
		sessionHolder.addSession(session.getId(), session);
	}
	
	@OnClose
	public void onClose(Session session, CloseReason closeReason) {
		sessionHolder.removeSession(session.getId());
	}
	
	
	@OnMessage
	public void onMessage(Session session, String message){
		if(session.isOpen()){
			ObjectMapper mapper = new ObjectMapper();
			try {
				SocketMessage msg = mapper.readValue(message, SocketMessage.class);
				switch(msg.getMsgType()){
				case   GET_AGENTS: getRunningAgents(session, mapper); break;
				case    GET_TYPES: getAgentTypes(session, mapper); break;
				case  START_AGENT: startAgent(session, mapper, msg); break;
				case   STOP_AGENT: stopAgent(session, mapper, msg.getAid()); break;
				case SEND_MESSAGE: sendMessage(msg.getMessage()); break;
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
		
		Iterator<AID> aids = agentRegistry.getRunningAID();
		
		while(aids.hasNext())
			runningAgents.add(aids.next());
		
		Iterator<Entry<String, List<AID>>> agents = agency.getCenterAgents();
		
		while(agents.hasNext())
			runningAgents.addAll(agents.next().getValue());
		
		SocketMessage msg = new SocketMessage();
		msg.setMsgType(messageType.GET_AGENTS);
		msg.setRunningAgents(runningAgents);
		String output = mapper.writeValueAsString(msg);
		session.getBasicRemote().sendText(output);
	}
	
	private void getAgentTypes(Session session, ObjectMapper mapper) throws IOException{
		Set<AgentType> type = new HashSet<AgentType>();
		
		Iterator<AgentType> iter = agency.getSupportedTypes();
		
		while(iter.hasNext())
			type.add(iter.next());
		
		agency.getOtherSupportedTypesStream().forEach(entry -> type.addAll(entry.getValue()));
		
		SocketMessage msg = new SocketMessage();
		msg.setMsgType(messageType.GET_TYPES);
		msg.setAgentTypes(type);
		String output = mapper.writeValueAsString(msg);
		session.getBasicRemote().sendText(output);
	}
	
	private void startAgent(Session session, ObjectMapper mapper, SocketMessage msg){
		if(agentRegistry.getRunningAIDStream().anyMatch(agent -> agent.getName().equals(msg.getAgentName())))
			return;
		
		AID agent = new AID();
		AgentType t = new AgentType(msg.getTypeName(), msg.getTypeModule());
		agent.setType(t); agent.setName(msg.getAgentName());
		Iterator<Entry<String, Set<AgentType>>> iter = agency.getOtherSupportedTypes();
		
		if(agency.isSupportedContained(t)){
			agentManager.startAgent(agent);
		}else{
			while(iter.hasNext()){
				Entry<String, Set<AgentType>> entry = iter.next();
				
				if(entry.getValue().contains(t)){
					Optional<AgentCenter> center = registry.getCenters().filter(cent -> cent.getAlias().equals(entry.getKey())).findFirst();
					ServiceMessage message = new ServiceMessage(OperationType.RUN_AGENT);
					message.setAid(agent);
					
					if(center.isPresent())
						try {
							requester.sendMessage(center.get().getAddress(), message);
						} catch (ConnectionException | IOException | TimeoutException | InterruptedException e) {
							System.out.println("Could not initialise agent");
							return;
						}
				}
			}
		}
	}
	
	private void stopAgent(Session session, ObjectMapper mapper, AID agentID){
		if(agentID == null)
			return;
		
		if(agentID.getHost().getAddress().equals(registry.getThisCenter().getAddress())){
			agentManager.stopAgent(agentID);
		}else{
			ServiceMessage message = new ServiceMessage(OperationType.STOP_AGENT);
			message.setAid(agentID);
			
			try {
				requester.sendMessage(agentID.getHost().getAddress(), message);
			} catch (ConnectionException | IOException | TimeoutException | InterruptedException e) {
				return;
			}
		}
	}
	
	private void sendMessage(ACLMessage message){
		handler.recieveAgentMessage(message);
	}

}

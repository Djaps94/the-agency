package handshake;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import beans.AgencyManagerLocal;
import beans.AgencyRegistryLocal;
import beans.AgentManagerLocal;
import beans.NetworkManagmentLocal;
import beans.SocketSenderLocal;
import exceptions.ConnectionException;
import exceptions.NodeExistsException;
import exceptions.RegisterSlaveException;
import model.AID;
import model.AgentCenter;
import model.AgentType;
import model.HandshakeMessage;
import model.HandshakeMessage.handshakeType;
import util.SocketMessage;
import util.SocketMessage.messageType;

@Stateless
public class HandshakeDealer implements HandshakeDealerLocal{

	@EJB
	private AgencyRegistryLocal registry;
	
	@EJB
	private NetworkManagmentLocal nodesManagment;
	
	@EJB
	private HandshakeRequesterLocal requester;
	
	@EJB
	private AgencyManagerLocal manager;
	
	@EJB
	private AgentManagerLocal agentManager;
	
	@EJB
	private SocketSenderLocal socketSender;
	
	public HandshakeDealer() { }
	
	public List<AgentCenter> registerCenter(HandshakeMessage message) throws RegisterSlaveException, ConnectionException, NodeExistsException, IOException, TimeoutException, InterruptedException{
		if(nodesManagment.isMaster()){
			
			for(AgentCenter center : registry.getCenters())
				requester.sendMessage(center.getAddress(), message);
			
			registry.addCenter(message.getCenter());
			List<AgentCenter> list = registry.getCenters()
											.stream()
											.filter(center -> !center.getAlias().equals(message.getCenter().getAlias()))
											.collect(Collectors.toList());
			
			list.add(registry.getThisCenter());									
			return list;
		}
		
		registry.addCenter(message.getCenter());
		return new ArrayList<AgentCenter>();
	}
	
	public Map<String, Set<AgentType>> registerAgentTypes(HandshakeMessage message) throws ConnectionException, IOException, TimeoutException, InterruptedException{
		Map<String,Set<AgentType>> returnSet = new HashMap<String, Set<AgentType>>();
		if(nodesManagment.isMaster()){
			returnSet.put(registry.getThisCenter().getAlias(),manager.getSupportedTypes());
			if(!manager.getOtherSupportedTypes().isEmpty())
				manager.getOtherSupportedTypes().entrySet()
												.stream()
												.forEach(entrySet -> returnSet.put(entrySet.getKey(), entrySet.getValue()));

			manager.addOtherTypes(message.getCenter().getAlias(), message.getAgentTypes());
			message.setType(handshakeType.DELIVER_TYPES);
			for(AgentCenter center : registry.getCenters()){
				if(!center.getAlias().equals(message.getCenter().getAlias()))
						requester.sendMessage(center.getAddress(), message);
			}
			SocketMessage msg = new SocketMessage();
			msg.setMsgType(messageType.ADD_TYPE);
			msg.setAgentTypes(message.getAgentTypes());
			socketSender.socketSend(msg);
			return returnSet;
		}	
		return returnSet;
	}
	
	public void addTypes(HandshakeMessage message){
		manager.addOtherTypes(message.getCenter().getAlias(), message.getAgentTypes());
	}
	
	public void rollback(HandshakeMessage message) throws ConnectionException, IOException, TimeoutException, InterruptedException{
		if(nodesManagment.isMaster()){
			registry.deleteCenter(message.getCenter());
			manager.deleteOtherTypes(message.getCenter().getAlias());
			manager.getRunningAgents().removeAll(message.getRunningAgents());
			for(AgentCenter center : registry.getCenters())
				requester.sendMessage(center.getAddress(), message);
		}
		
		registry.deleteCenter(message.getCenter());
		manager.deleteOtherTypes(message.getCenter().getAlias());
		manager.getRunningAgents().removeAll(message.getRunningAgents());
		//TODO: ws
	}
	
	public Map<String,List<AID>> getRunningAgents(){
		Map<String,List<AID>> agents = new HashMap<String, List<AID>>();
		agents.put(registry.getThisCenter().getAlias(), manager.getRunningAgents());
		return agents;
	}

	@Override
	public void deleteAgent(HandshakeMessage message) {
		manager.getCenterAgents().get(message.getCenter().getAlias()).remove(message.getAid());
	}

	@Override
	public void addAgent(HandshakeMessage message) {
		if(manager.getCenterAgents().containsKey(message.getCenter().getAlias())){
			manager.getCenterAgents().get(message.getCenter().getAlias()).add(message.getAid());
		}else{
			List<AID> list = new ArrayList<>();
			list.add(message.getAid());
			manager.getCenterAgents().put(message.getCenter().getAlias(), list);
		}
	}

	@Override
	public AID runAgent(HandshakeMessage message) {
		AID aid = agentManager.startAgent(message.getAid());
		SocketMessage msg = new SocketMessage();
		msg.setMsgType(messageType.START_AGENT);
		msg.setAid(aid);
		socketSender.socketSend(msg);
		return aid;
	}
	
	public AID stopAgent(HandshakeMessage message){
		AID aid = agentManager.stopAgent(message.getAid());
		SocketMessage msg = new SocketMessage();
		msg.setMsgType(messageType.STOP_AGENT);
		msg.setAid(aid);
		socketSender.socketSend(msg);
		return aid;
	}
	
}

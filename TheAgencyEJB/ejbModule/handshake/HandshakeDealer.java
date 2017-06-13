package handshake;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import beans.AgencyManagerLocal;
import beans.AgencyRegistryLocal;
import beans.NetworkManagmentLocal;
import exceptions.ConnectionException;
import exceptions.RegisterSlaveException;
import model.AgentCenter;
import model.AgentType;
import model.HandshakeMessage;
import model.HandshakeMessage.handshakeType;

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
	
	public HandshakeDealer() { }
	
	public List<AgentCenter> registerCenter(HandshakeMessage message) throws RegisterSlaveException, ConnectionException{
		if(nodesManagment.isMaster()){
			registry.addCenter(message.getCenter());
			
			for(AgentCenter center : registry.getCenters()){
				if(!center.getAlias().equals(message.getCenter().getAlias()))
						requester.sendMessage(center.getAddress(), message);
			}
			List<AgentCenter> list = registry.getCenters().stream()
											 .filter(center -> !center.getAlias().equals(message.getCenter().getAlias()))
											 .collect(Collectors.toList());
			
			list.add(registry.getThisCenter());									
			return list;
		}
		
		registry.addCenter(message.getCenter());
		return new ArrayList<AgentCenter>();
	}
	
	public Map<String, Set<AgentType>> registerAgentTypes(HandshakeMessage message) throws ConnectionException{
		Map<String,Set<AgentType>> returnSet = new HashMap<String, Set<AgentType>>();
		if(nodesManagment.isMaster()){
			returnSet.put(registry.getThisCenter().getAlias(),manager.getSupportedTypes());
			manager.getOtherSupportedTypes().entrySet()
											.stream()
											.forEach(entrySet -> returnSet.put(entrySet.getKey(), entrySet.getValue()));
			
			manager.addOtherTypes(message.getCenter().getAlias(), message.getAgentTypes());
			message.setType(handshakeType.DELIVER_TYPES);
			for(AgentCenter center : registry.getCenters()){
				if(!center.getAlias().equals(message.getCenter().getAlias()))
						requester.sendMessage(center.getAddress(), message);
			}
			return returnSet;
		}	
		return returnSet;
	}
	
	public void addTypes(HandshakeMessage message){
		manager.addOtherTypes(message.getCenter().getAlias(), message.getAgentTypes());
	}
	
	public void rollback(HandshakeMessage message) throws ConnectionException{
		if(nodesManagment.isMaster()){
			registry.deleteCenter(message.getCenter());
			manager.deleteOtherTypes(message.getCenter().getAlias());
			for(AgentCenter center : registry.getCenters())
				requester.sendMessage(center.getAddress(), message);
		}
		
		registry.deleteCenter(message.getCenter());
		manager.deleteOtherTypes(message.getCenter().getAlias());
	}
	
}

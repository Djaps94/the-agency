package handshake;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
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
			return registry.getCenters().stream()
										.filter(center -> !center.getAlias().equals(message.getCenter().getAlias()))
										.collect(Collectors.toList());
		}
		
		registry.addCenter(message.getCenter());
		return new ArrayList<AgentCenter>();
	}
	
	public Set<AgentType> registerAgentTypes(HandshakeMessage message) throws ConnectionException{
		Set<AgentType> returnSet = new HashSet<AgentType>();
		if(nodesManagment.isMaster()){
			returnSet.addAll(manager.getSupportedTypes());
			returnSet.addAll(manager.getOtherSupportedTypes());
			manager.getOtherSupportedTypes().addAll(message.getAgentTypes());
			message.setType(handshakeType.DELIVER_TYPES);
			for(AgentCenter center : registry.getCenters()){
				if(!center.getAlias().equals(message.getCenter().getAlias()))
						requester.sendMessage(center.getAddress(), message);
			}
			return returnSet;
		}	
		return returnSet;
	}
	
}

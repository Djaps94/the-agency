package rest;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import beans.AgencyManagerLocal;
import beans.AgencyRegistryLocal;
import beans.AgentManagerLocal;
import beans.AgentRegistryLocal;
import exceptions.ConnectionException;
import intercommunication.ReceiverLocal;
import intercommunication.DispatcherLocal;
import intercommunication.MediatorDispatcherLocal;
import model.ACLMessage;
import model.ACLMessage.Performative;
import model.AID;
import model.AgentCenter;
import model.AgentType;
import model.ServiceMessage;
import model.ServiceMessage.OperationType;
import service.MessageRequestLocal;

@Stateless
@Path("/agency")
public class AgencyEndPoint {
	
	@EJB
	private AgencyManagerLocal manager;
	
	@EJB
	private MessageRequestLocal request;
	
	@EJB
	private AgencyRegistryLocal registry;
	
	@EJB
	private AgentManagerLocal agentManager;
	
	@EJB
	private DispatcherLocal dispatcher;
	
	@EJB
	private MediatorDispatcherLocal rabbit;
	
	@EJB
	private ReceiverLocal handler;
	
	@EJB
	private AgentRegistryLocal agentRegistry;

	@GET
	@Path("/agents/classes")
	@Produces(MediaType.APPLICATION_JSON)
	public List<AgentType> getAgentType(){
		List<AgentType> types = new ArrayList<>();
		
		Iterator<Entry<String, Set<AgentType>>> iter = manager.getOtherSupportedTypes();
		
		while(iter.hasNext())
			iter.next().getValue().forEach(type -> types.add(type));
		
		manager.getSupportedTypesStream().forEach(el -> types.add(el));
		return types;
		
	}
	
	@GET
	@Path("/agents/running")
	@Produces(MediaType.APPLICATION_JSON)
	public List<AID> getRunningAgents(){
		List<AID> agents = new ArrayList<AID>();
		Iterator<AID> agentsIter = agentRegistry.getRunningAID();
		
		while(agentsIter.hasNext())
			agents.add(agentsIter.next());
		
		Iterator<Entry<String, List<AID>>> aids = manager.getCenterAgents();
		
		while(aids.hasNext())
			agents.addAll(aids.next().getValue());
		
		return agents;
	}
	
	@GET
	@Path("/messages")
	@Produces(MediaType.APPLICATION_JSON)
	public List<String> getPerformatives(){
		return Stream.of(Performative.values()).map(Performative::name).collect(Collectors.toList());
	}
	
	@POST
	@Path("/messages")
	@Consumes(MediaType.APPLICATION_JSON)
	public void sendMessageToAgent(ACLMessage message){
		handler.recieveAgentMessage(message);
	}
	
	@PUT
	@Path("/agents/running/{type}/{name}")
	@Produces(MediaType.APPLICATION_JSON)
	public AID runAgent(@PathParam("type") String type, @PathParam("name") String name){
		if(agentRegistry.getRunningAIDStream().anyMatch(agent -> agent.getName().equals(name)))
			return null;
		
		String[] typesPart = type.split(":");
		AID agent = new AID();
		AgentType t = new AgentType(typesPart[1].trim(), typesPart[0].trim());
		agent.setType(t); 
		agent.setName(name);
		
		if(manager.isSupportedContained(t)){
			return agentManager.startAgent(agent);
		}else{
			Iterator<Entry<String, Set<AgentType>>> suppTypes = manager.getOtherSupportedTypes();
			while(suppTypes.hasNext()){
				Entry<String, Set<AgentType>> entry = suppTypes.next();
				
				if(entry.getValue().contains(t)){
					Optional<AgentCenter> center = registry.getCenters().filter(cent -> cent.getAlias().equals(entry.getKey())).findFirst();
					ServiceMessage message = new ServiceMessage(OperationType.RUN_AGENT);
					message.setAid(agent);
					
					if(center.isPresent())
						try {
							ServiceMessage msg = request.sendMessage(center.get().getAddress(), message);
							return msg.getAid();
						} catch (ConnectionException | IOException | TimeoutException | InterruptedException e) {
							System.out.println("Could not initialise agent");
							return null;
						}
				}
			}
		}
		
		return agent;
	}
	
	@DELETE
	@Path("/agents/running")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public AID deleteAgent(AID agentID){
		if(agentID.getHost().getAddress().equals(registry.getThisCenter().getAddress())){
			return agentManager.stopAgent(agentID);
		}else{
			ServiceMessage message = new ServiceMessage(OperationType.STOP_AGENT);
			message.setAid(agentID);
			
			try {
				ServiceMessage msg = request.sendMessage(agentID.getHost().getAddress(), message);
				return msg.getAid();
			} catch (ConnectionException | IOException | TimeoutException | InterruptedException e) {
				return null;
			}
		}
	}
}

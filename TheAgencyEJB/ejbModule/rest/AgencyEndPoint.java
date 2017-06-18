package rest;

import java.io.IOException;
import java.util.ArrayList;
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
import exceptions.ConnectionException;
import handshake.HandshakeRequesterLocal;
import model.ACLMessage;
import model.ACLMessage.Performative;
import model.AID;
import model.Agent;
import model.AgentCenter;
import model.AgentType;
import model.HandshakeMessage;
import model.HandshakeMessage.handshakeType;

@Stateless
@Path("/agency")
public class AgencyEndPoint {
	
	@EJB
	private AgencyManagerLocal manager;
	
	@EJB
	private HandshakeRequesterLocal requester;
	
	@EJB
	private AgencyRegistryLocal registry;
	
	@EJB
	private AgentManagerLocal agentManager;

	@GET
	@Path("/agents/classes")
	@Produces(MediaType.APPLICATION_JSON)
	public List<AgentType> getAgentType(){
		List<AgentType> types = new ArrayList<>();
		for(Entry<String, Set<AgentType>> entry : manager.getOtherSupportedTypes().entrySet()){
			entry.getValue().forEach(type -> types.add(type));
		}
		manager.getSupportedTypes().forEach(el -> types.add(el));
		return types;
		
	}
	
	@GET
	@Path("/agents/running")
	@Produces(MediaType.APPLICATION_JSON)
	public List<Agent> getRunningAgents(){
		List<Agent> agents = new ArrayList<Agent>();
		agents.addAll(manager.getRunningAgents());
		agents.addAll(manager.getCenterAgents().get(registry.getThisCenter().getAlias()));
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
		//TODO: look up for agent on this center and send message through jms
		//TODO: If's not on this center, find first center and send message to it
	}
	@PUT
	@Path("/agents/running/{type}/{name}")
	@Produces(MediaType.APPLICATION_JSON)
	public Agent runAgent(@PathParam("type") String type, @PathParam("name") String name){
		if(manager.getRunningAgents().stream().anyMatch(agent -> agent.getId().getName().equals(name)))
			return null;
		
		String[] typesPart = type.split(":");
		Agent agent = null;
		AgentType t = new AgentType(typesPart[1].trim(), typesPart[0].trim());
		if(manager.getSupportedTypes().contains(t)){
			return agentManager.startAgent(agent, typesPart, t, name);
		}else{
			for(Entry<String, Set<AgentType>> entry : manager.getOtherSupportedTypes().entrySet()){
				if(entry.getValue().contains(t)){
					Optional<AgentCenter> center = registry.getCenters().stream().filter(cent -> cent.getAlias().equals(entry.getKey())).findFirst();
					HandshakeMessage message = new HandshakeMessage(handshakeType.RUN_AGENT);
					message.setMessage(typesPart);
					message.setAgent(agent);
					message.setAgentType(t);
					message.setCenter(center.get());
					message.setAgentName(name);
					if(center.isPresent())
						try {
							HandshakeMessage msg = requester.sendMessage(center.get().getAddress(), message);
							return msg.getAgent();
						} catch (ConnectionException | IOException | TimeoutException | InterruptedException e) {
							System.out.println("Could not initialise agent");
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
	public Agent deleteAgent(AID agentID){
		if(agentID.getHost().equals(registry.getThisCenter())){
			Optional<Agent> agent = manager.getRunningAgents().stream()
															  .filter(ag -> ag.getId().equals(agentID))
															  .findFirst();
			if(agent.isPresent())
				return agentManager.stopAgent(agent.get());
		}else{
			HandshakeMessage message = new HandshakeMessage(handshakeType.STOP_AGENT);
			message.setAid(agentID);
			try {
				HandshakeMessage msg = requester.sendMessage(agentID.getHost().getAddress(), message);
				return msg.getAgent();
			} catch (ConnectionException | IOException | TimeoutException | InterruptedException e) {
				return null;
			}
		}
		return null;
		
		
		
	}
}

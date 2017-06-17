package rest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import beans.AgencyManagerLocal;
import model.ACLMessage.Performative;
import model.Agent;
import model.AgentType;

@Stateless
@Path("/agency")
public class AgencyEndPoint {
	
	@EJB
	private AgencyManagerLocal manager;

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
		return manager.getRunningAgents();
	}
	
	@GET
	@Path("/messages")
	@Produces(MediaType.APPLICATION_JSON)
	public List<String> getPerformatives(){
		return Stream.of(Performative.values()).map(Performative::name).collect(Collectors.toList());
	}
	
	@PUT
	@Path("/agents/running/{type}/{name}")
	public void runAgent(){
		//TODO: send message to other centers to add new running agent
	}
	
	@DELETE
	@Path("/agents/running/{aid}")
	public void deleteAgent(){
		
	}
}

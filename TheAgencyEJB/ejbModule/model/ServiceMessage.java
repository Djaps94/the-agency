package model;

import java.io.Serializable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ServiceMessage implements Serializable {

	public enum OperationType { REGISTER,
								GET_CENTERS,
								GET_TYPES,
								DELIVER_TYPES,
								ROLLBACK,
								GET_RUNNING,
								TURN_OFF,
								DELETE_AGENT,
								ADD_AGENT,
								RUN_AGENT,
								STOP_AGENT
							  };
	
	private AgentCenter center;
	private OperationType type;
	private List<AgentCenter> centers;
	private Set<AgentType> agentTypes;
	private Map<String, Set<AgentType>> otherTypes;
	private Map<String, List<AID>> otherAgents;
	private AgentType agentType;
	private List<AID> runningAgents;
	private String agentName;
	private AID aid;
	
	public ServiceMessage() { }
	
	public ServiceMessage(AgentCenter center, OperationType type){
		this.center = center;
		this.type   = type;
	}
	
	public ServiceMessage(OperationType type){
		this.type = type;
	}

	public AgentCenter getCenter() {
		return center;
	}

	public void setCenter(AgentCenter center) {
		this.center = center;
	}

	public OperationType getType() {
		return type;
	}

	public void setType(OperationType type) {
		this.type = type;
	}

	public List<AgentCenter> getCenters() {
		return centers;
	}

	public void setCenters(List<AgentCenter> centers) {
		this.centers = centers;
	}

	public Set<AgentType> getAgentTypes() {
		return agentTypes;
	}

	public void setAgentTypes(Iterator<AgentType> agentTypes) {
		while(agentTypes.hasNext())
			this.agentTypes.add(agentTypes.next());
	}

	public Map<String, Set<AgentType>> getOtherTypes() {
		return otherTypes;
	}

	public void setOtherTypes(Map<String, Set<AgentType>> otherTypes) {
		this.otherTypes = otherTypes;
	}

	public Iterator<AID> getRunningAgents() {
		return runningAgents.iterator();
	}

	public void setRunningAgents(Iterator<AID> runningAgents) {
		while(runningAgents.hasNext())
		this.runningAgents.add(runningAgents.next());
	}

	public AgentType getAgentType() {
		return agentType;
	}

	public void setAgentType(AgentType agentType) {
		this.agentType = agentType;
	}

	public Map<String, List<AID>> getOtherAgents() {
		return otherAgents;
	}

	public void setOtherAgents(Map<String, List<AID>> otherAgents) {
		this.otherAgents = otherAgents;
	}

	public String getAgentName() {
		return agentName;
	}

	public void setAgentName(String agentName) {
		this.agentName = agentName;
	}

	public AID getAid() {
		return aid;
	}

	public void setAid(AID aid) {
		this.aid = aid;
	}
	
	
}

package model;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class HandshakeMessage {

	public enum handshakeType { REGISTER,
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
	private handshakeType type;
	private List<AgentCenter> centers;
	private Set<AgentType> agentTypes;
	private Map<String, Set<AgentType>> otherTypes;
	private Map<String, List<Agent>> otherAgents;
	private String[] message;
	private Agent agent;
	private AgentType agentType;
	private List<Agent> runningAgents;
	private String agentName;
	private AID aid;
	
	public HandshakeMessage() { }
	
	public HandshakeMessage(AgentCenter center, handshakeType type){
		this.center = center;
		this.type   = type;
	}
	
	public HandshakeMessage(handshakeType type){
		this.type = type;
	}

	public AgentCenter getCenter() {
		return center;
	}

	public void setCenter(AgentCenter center) {
		this.center = center;
	}

	public handshakeType getType() {
		return type;
	}

	public void setType(handshakeType type) {
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

	public void setAgentTypes(Set<AgentType> agentTypes) {
		this.agentTypes = agentTypes;
	}

	public String[] getMessage() {
		return message;
	}

	public void setMessage(String[] message) {
		this.message = message;
	}

	public Map<String, Set<AgentType>> getOtherTypes() {
		return otherTypes;
	}

	public void setOtherTypes(Map<String, Set<AgentType>> otherTypes) {
		this.otherTypes = otherTypes;
	}

	public List<Agent> getRunningAgents() {
		return runningAgents;
	}

	public void setRunningAgents(List<Agent> runningAgents) {
		this.runningAgents = runningAgents;
	}

	public Agent getAgent() {
		return agent;
	}

	public void setAgent(Agent agent) {
		this.agent = agent;
	}

	public AgentType getAgentType() {
		return agentType;
	}

	public void setAgentType(AgentType agentType) {
		this.agentType = agentType;
	}

	public Map<String, List<Agent>> getOtherAgents() {
		return otherAgents;
	}

	public void setOtherAgents(Map<String, List<Agent>> otherAgents) {
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

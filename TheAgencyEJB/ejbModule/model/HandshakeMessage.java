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
								TURN_OFF
							  };
	
	private AgentCenter center;
	private handshakeType type;
	private List<AgentCenter> centers;
	private Set<AgentType> agentTypes;
	private Map<String, Set<AgentType>> otherTypes;
	private String message;
	private List<Agent> runningAgents;
	
	public HandshakeMessage() { }
	
	public HandshakeMessage(AgentCenter center, handshakeType type){
		this.center = center;
		this.type   = type;
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

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
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
	
	
}

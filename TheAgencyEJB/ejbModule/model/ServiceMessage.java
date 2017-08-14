package model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class ServiceMessage implements Serializable {

	private static final long serialVersionUID = 4215142248690881085L;

	public enum OperationType { REGISTER, STREAM_MESSAGE,
		GET_CENTERS, GET_TYPES, DELIVER_TYPES, ROLLBACK, GET_RUNNING, TURN_OFF, DELETE_AGENT, ADD_AGENT, RUN_AGENT, STOP_AGENT
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
	private String messageInfo;
	
	public ServiceMessage() { }
	
	public ServiceMessage(AgentCenter center, OperationType type){
		this.center = center;
		this.type   = type;
	}
	
	public ServiceMessage(OperationType type) {
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

	public void setAgentTypes(Set<AgentType> agentTypes) {
		this.agentTypes = agentTypes;
	}

	public Map<String, Set<AgentType>> getOtherTypes() {
		return otherTypes;
	}

	public void setOtherTypes(Map<String, Set<AgentType>> otherTypes) {
		this.otherTypes = otherTypes;
	}

	public Map<String, List<AID>> getOtherAgents() {
		return otherAgents;
	}

	public void setOtherAgents(Map<String, List<AID>> otherAgents) {
		this.otherAgents = otherAgents;
	}

	public AgentType getAgentType() {
		return agentType;
	}

	public void setAgentType(AgentType agentType) {
		this.agentType = agentType;
	}

	public List<AID> getRunningAgents() {
		return runningAgents;
	}

	public void setRunningAgents(List<AID> runningAgents) {
		this.runningAgents = runningAgents;
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

	@JsonIgnore
	public void setTypesViaIter(Iterator<AgentType> typeIterator) {
		if(this.agentTypes == null)
			this.agentTypes = new HashSet<>();
		while(typeIterator.hasNext())
			this.agentTypes.add(typeIterator.next());
	}
	
	@JsonIgnore
	public void setAIDsViaIter(Iterator<AID> aids){
		if(this.runningAgents == null)
			this.runningAgents = new ArrayList<>();
		while(aids.hasNext())
			this.runningAgents.add(aids.next());
	}

	public String getMessageInfo() {
		return messageInfo;
	}

	public void setMessageInfo(String messageInfo) {
		this.messageInfo = messageInfo;
	}
	
}

package util;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import model.ACLMessage;
import model.AID;
import model.AgentType;

@SuppressWarnings("serial")
public class SocketMessage implements Serializable{

	public enum messageType {
		GET_TYPES, GET_AGENTS, ADD_TYPE, SEND_MESSAGE, START_AGENT, STOP_AGENT, REMOVE_AGENTS, REMOVE_TYPES
	};
	
	private AID aid;
	private AgentType type;
	private List<AID> runningAgents = new ArrayList<>();
	private Set<AgentType> agentTypes = new HashSet<>();
	private String agentName;
	private String typeModule;
	private String typeName;
	private messageType msgType;
	private ACLMessage message;
	
	
	public SocketMessage() { }


	public AID getAid() {
		return aid;
	}


	public void setAid(AID aid) {
		this.aid = aid;
	}


	public AgentType getType() {
		return type;
	}


	public void setType(AgentType type) {
		this.type = type;
	}


	public List<AID> getRunningAgents() {
		return runningAgents;
	}


	public void setRunningAgents(Iterator<AID> running) {
		while(running.hasNext())
			this.runningAgents.add(running.next());
	}


	public Set<AgentType> getAgentTypes() {
		return agentTypes;
	}


	public void setAgentTypes(Set<AgentType> agentTypes) {
		this.agentTypes = agentTypes;
	}


	public messageType getMsgType() {
		return msgType;
	}


	public void setMsgType(messageType msgType) {
		this.msgType = msgType;
	}


	public String getAgentName() {
		return agentName;
	}


	public void setAgentName(String agentName) {
		this.agentName = agentName;
	}


	public String getTypeModule() {
		return typeModule;
	}


	public void setTypeModule(String typeModule) {
		this.typeModule = typeModule;
	}


	public String getTypeName() {
		return typeName;
	}


	public void setTypeName(String typeName) {
		this.typeName = typeName;
	}


	public ACLMessage getMessage() {
		return message;
	}


	public void setMessage(ACLMessage message) {
		this.message = message;
	}
}

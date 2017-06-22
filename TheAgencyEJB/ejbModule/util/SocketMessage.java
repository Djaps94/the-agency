package util;

import java.io.Serializable;
import java.util.List;
import java.util.Set;

import model.AID;
import model.AgentType;

@SuppressWarnings("serial")
public class SocketMessage implements Serializable{

	public enum messageType { GET_TYPES,
							  GET_AGENTS,
							  ADD_TYPE,
							  SEND_MESSAGE,
							  START_AGENT,
							  STOP_AGENT
	};
	
	private AID aid;
	private AgentType type;
	private List<AID> runningAgents;
	private Set<AgentType> agentTypes;
	private messageType msgType;
	
	
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


	public void setRunningAgents(List<AID> runningAgents) {
		this.runningAgents = runningAgents;
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
}

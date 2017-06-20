package intercommunication;

import java.io.Serializable;

import model.ACLMessage;

@SuppressWarnings("serial")
public class InterCenterMessage implements Serializable{
	
	private ACLMessage message;
	private String agentName;
	
	public InterCenterMessage(ACLMessage message, String name){
		this.message   = message;
		this.agentName = name;
	}
	
	public ACLMessage getMessage() {
		return message;
	}
	public void setMessage(ACLMessage message) {
		this.message = message;
	}
	public String getAgentName() {
		return agentName;
	}
	public void setAgentName(String agentName) {
		this.agentName = agentName;
	}
	

}

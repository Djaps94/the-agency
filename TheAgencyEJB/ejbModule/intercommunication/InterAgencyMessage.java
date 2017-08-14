package intercommunication;

import java.io.Serializable;

import model.ACLMessage;
import model.AID;

@SuppressWarnings("serial")
public class InterAgencyMessage implements Serializable{
	
	private ACLMessage message;
	private AID aid;
	
	public InterAgencyMessage() { }
	
	public InterAgencyMessage(ACLMessage message, AID aid){
		this.message   = message;
		this.aid = aid;
	}
	
	public ACLMessage getMessage() {
		return message;
	}
	public void setMessage(ACLMessage message) {
		this.message = message;
	}
	public AID getAid() {
		return aid;
	}
	public void setAid(AID aid) {
		this.aid = aid;
	}
	

}

package intercommunication;

import javax.ejb.Local;

import model.ACLMessage;

@Local
public interface HandlerLocal {

	public void sendAgentMessage(ACLMessage message);
}

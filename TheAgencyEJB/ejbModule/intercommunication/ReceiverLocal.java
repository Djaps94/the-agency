package intercommunication;

import javax.ejb.Local;

import model.ACLMessage;

@Local
public interface ReceiverLocal {

	public void recieveAgentMessage(ACLMessage message);
}

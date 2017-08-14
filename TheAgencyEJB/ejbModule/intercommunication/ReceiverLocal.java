package intercommunication;

import javax.ejb.Local;

import model.ACLMessage;

@Local
public interface ReceiverLocal {

	void recieveAgentMessage(ACLMessage message);
}

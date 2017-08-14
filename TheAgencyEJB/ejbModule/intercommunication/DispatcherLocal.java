package intercommunication;

import javax.ejb.Local;

import model.ACLMessage;
import model.AID;

@Local
public interface DispatcherLocal {
	
	void sendMesssage(ACLMessage message, AID aid);

}

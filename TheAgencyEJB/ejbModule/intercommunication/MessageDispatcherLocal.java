package intercommunication;

import javax.ejb.Local;

import model.ACLMessage;

@Local
public interface MessageDispatcherLocal {
	
	public void sendMesssage(ACLMessage message, String name);

}

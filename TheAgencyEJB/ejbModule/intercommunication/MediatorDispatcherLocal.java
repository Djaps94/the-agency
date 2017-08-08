package intercommunication;

import javax.ejb.Local;

import model.ACLMessage;
import model.AID;

@Local
public interface MediatorDispatcherLocal {

	public void notifyCenter(ACLMessage message, AID aid, String address);
}

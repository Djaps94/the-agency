package intercommunication;

import javax.ejb.Local;

import model.ACLMessage;
import model.AID;

@Local
public interface RabbitDispatcherLocal {

	public void notifyCenter(ACLMessage message, AID aid, String address);
}

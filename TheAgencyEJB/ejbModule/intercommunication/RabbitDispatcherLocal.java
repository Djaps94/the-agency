package intercommunication;

import javax.ejb.Local;

import model.ACLMessage;

@Local
public interface RabbitDispatcherLocal {

	public void notifyCenter(ACLMessage message, String name);
}

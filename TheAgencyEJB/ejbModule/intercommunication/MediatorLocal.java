package intercommunication;

import javax.ejb.Local;

@Local
public interface MediatorLocal {

	public void recieveAgentMessage();
}

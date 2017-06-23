package intercommunication;

import javax.ejb.Local;

@Local
public interface HandlerRabbitLocal {

	public void recieveMessage();
}

package beans;

import javax.ejb.Local;

import util.SocketMessage;

@Local
public interface SocketSenderLocal {

	public void socketSend(SocketMessage message);
}

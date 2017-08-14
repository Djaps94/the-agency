package beans;

import javax.ejb.Local;

import util.SocketMessage;

@Local
public interface SocketSenderLocal {

	void socketSend(SocketMessage message);
}

package beans;

import javax.ejb.Local;
import javax.websocket.Session;

@Local
public interface SessionHolderLocal {
	
	public void addSession(String id, Session session);
	public void removeSession(String id);
	public boolean isContained(String id);

}

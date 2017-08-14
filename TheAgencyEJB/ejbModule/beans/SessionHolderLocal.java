package beans;

import java.util.Set;
import java.util.Map.Entry;

import javax.ejb.Local;
import javax.websocket.Session;

@Local
public interface SessionHolderLocal {
	
	void addSession(String id, Session session);
	void removeSession(String id);
	boolean isContained(String id);
	Set<Entry<String, Session>> getEntry();

}

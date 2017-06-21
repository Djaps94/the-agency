package beans;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.ejb.LocalBean;
import javax.ejb.Singleton;
import javax.websocket.Session;


@Singleton
@LocalBean
public class SessionHolder implements SessionHolderLocal {

	
	private Map<String, Session> sessionMap;
	
    public SessionHolder() {
       
    }
    
    @PostConstruct
    private void initialise(){
    	sessionMap = new HashMap<String, Session>();
    }
    
    public void addSession(String id, Session session){
    	if(!isContained(id))
    		sessionMap.put(id, session);
    }
    
    public void removeSession(String id){
    	sessionMap.remove(id);
    }
    
    public boolean isContained(String id){
    	return sessionMap.containsKey(id);
    }

}

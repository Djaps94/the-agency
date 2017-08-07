package intercommunication;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Singleton;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import beans.AgencyManagerLocal;
import model.ACLMessage;
import model.AID;
import model.Agent;

/**
 * Session Bean implementation class MessageDispatcher
 * Aka Diana Burnwood
 */
@Singleton
@LocalBean
public class MessageDispatcher implements MessageDispatcherLocal {
	
    @EJB
    private AgencyManagerLocal manager;
	
	public MessageDispatcher() {
    
    }

    public void sendMesssage(ACLMessage message, AID aid){
		try {
	  		InitialContext context = new InitialContext();
			Agent a = (Agent)context.lookup("java:module/"+aid.getType().getName());
			a.setId(aid);
			
			while(manager.getStartedAgents().hasNext()){
				Agent agent = manager.getStartedAgents().next();
				if(agent.getId().getName().equals(a.getId().getName())){
					agent.handleMessage(message);
					return;
				}
			}
			a.handleMessage(message);
		} catch (NamingException e) {
			e.printStackTrace();
		}
    }
    

}

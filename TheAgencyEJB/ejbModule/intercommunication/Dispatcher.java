package intercommunication;

import java.util.Iterator;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Singleton;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import beans.AgentRegistryLocal;
import model.ACLMessage;
import model.AID;
import model.Agent;

/**
 * Session Bean implementation class Dispatcher
 * Aka Diana Burnwood
 */
@Singleton
@LocalBean
public class Dispatcher implements DispatcherLocal {
	
    @EJB
    private AgentRegistryLocal registry;
    
	public Dispatcher() {
    
    }

    public void sendMesssage(ACLMessage message, AID aid){
		try {
	  		InitialContext context = new InitialContext();
			Agent a = (Agent)context.lookup("java:module/"+aid.getType().getName());
			a.setId(aid);
			Iterator<Agent> iter = registry.getRunningAgents();
			
			while(iter.hasNext()){
				Agent agent = iter.next();
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

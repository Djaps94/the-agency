package intercommunication;

import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;

import beans.AgencyManagerLocal;
import beans.AgencyRegistryLocal;
import model.ACLMessage;
import model.AID;


@Stateless
@LocalBean
public class Receiver implements ReceiverLocal{

	@EJB
	private AgencyManagerLocal agency;
	
	@EJB
	private AgencyRegistryLocal registry;
	
	@EJB
	private MediatorDispatcherLocal rabbit;
	
	@EJB
	private DispatcherLocal dispatcher;
	
    public Receiver() {

    }
	
	@Override
	public void recieveAgentMessage(ACLMessage message) {
		if(message == null)
			return;
		for(AID aid : message.getRecievers()){
			if(aid.getHost().getAlias().equals(registry.getThisCenter().getAlias())){
				dispatcher.sendMesssage(message, aid);
			}else{
				Iterator<Entry<String, List<AID>>> agents = agency.getCenterAgents();
				while(agents.hasNext()){
					Entry<String, List<AID>> entry = agents.next();
					if(entry.getKey().equals(aid.getHost().getAlias())){
						rabbit.notifyCenter(message, aid, entry.getKey());
						break;
					}
				}
			}
		}
	}

}

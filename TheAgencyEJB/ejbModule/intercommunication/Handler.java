package intercommunication;

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
public class Handler implements HandlerLocal{

	@EJB
	private AgencyManagerLocal agency;
	
	@EJB
	private AgencyRegistryLocal registry;
	
	@EJB
	private RabbitDispatcherLocal rabbit;
	
	@EJB
	private MessageDispatcherLocal dispatcher;
	
    public Handler() {

    }
	
	@Override
	public void sendAgentMessage(ACLMessage message) {
		if(message == null)
			return;
		for(AID aid : message.getRecievers()){
			if(aid.getHost().getAlias().equals(registry.getThisCenter().getAlias())){
				dispatcher.sendMesssage(message, aid);
			}else{
				for(Entry<String, List<AID>> entry : agency.getCenterAgents().entrySet()){
					if(entry.getKey().equals(aid.getHost().getAlias())){
						rabbit.notifyCenter(message, aid, entry.getKey());
						break;
					}
				}
			}
		}
	}

}

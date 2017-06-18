package intercommunication;

import java.util.List;
import java.util.Map.Entry;
import java.util.Optional;

import javax.ejb.ActivationConfigProperty;
import javax.ejb.EJB;
import javax.ejb.MessageDriven;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;

import beans.AgencyManagerLocal;
import model.ACLMessage;
import model.Agent;


@MessageDriven(
		activationConfig = { @ActivationConfigProperty(
				propertyName = "destinationType", propertyValue = "javax.jms.Queue"),
							 @ActivationConfigProperty(
				propertyName = "destination", propertyValue = "java:/jms/queue/Handler"
									 )
		})
public class Handler implements MessageListener {

	@EJB
	private AgencyManagerLocal manager;
	
    public Handler() {

    }
	

    public void onMessage(Message message) {
    	try {
			ACLMessage msg = (ACLMessage) ((ObjectMessage)message).getObject();
			String name = (String) ((ObjectMessage)message).getObjectProperty("Agent");
			Optional<Agent> agent = manager.getRunningAgents().stream().filter(ag -> ag.getId().getName().equals(name))
																	   .findFirst();
			if(agent.isPresent())
				agent.get().handleMessage(msg);
			else{
				for(Entry<String, List<Agent>> entry : manager.getCenterAgents().entrySet()){
					if(entry.getValue().stream().anyMatch(element -> element.getId().getName().equals(name))){
						//Send message to entry.key() via Rest or RabbitMQ
					}
				}
			}
    	} catch (JMSException e) {
			e.printStackTrace();
		}
    }

}

package intercommunication;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.ejb.LocalBean;
import javax.ejb.Singleton;
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.ObjectMessage;
import javax.jms.Queue;
import javax.jms.QueueSender;
import javax.jms.QueueSession;
import javax.jms.Session;

import model.ACLMessage;

/**
 * Session Bean implementation class MessageDispatcher
 * Aka Diana Burnwood
 */
@Singleton
@LocalBean
public class MessageDispatcher implements MessageDispatcherLocal {

	@Resource(mappedName = "java:/ConnectionFactory")
	private ConnectionFactory factory;
	
	@Resource(mappedName = "java:/jms/queue/Handler")
	private Queue queue;
	
	private Connection connection;
	private QueueSession session;
	private QueueSender sender;
	
    public MessageDispatcher() {
    
    }
    
    @PostConstruct
    private void initialise(){
       	try {
        	connection = factory.createConnection();
			session    = (QueueSession) connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
			sender     = session.createSender(queue);
    	} catch (JMSException e) {
			e.printStackTrace();
		}
    }
    
    public void sendMesssage(ACLMessage message, String name){
    	try {
			ObjectMessage msg = session.createObjectMessage(message);
			msg.setStringProperty("Agent", name);
			sender.send(msg);
		} catch (JMSException e) {
			e.printStackTrace();
		}
    }
    

}

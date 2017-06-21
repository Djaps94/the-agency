package beans;

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

import util.SocketMessage;

@Singleton
@LocalBean
public class SocketSender implements SocketSenderLocal {
	
	@Resource(mappedName = "java:/ConnectionFactory")
	private ConnectionFactory factory;
	
	@Resource(mappedName = "java:/jms/queue/SocketQueue")
	private Queue queue;
	
	private Connection connection;
	private QueueSession session;
	private QueueSender sender;

    public SocketSender() {
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
    
    public void socketSend(SocketMessage message){
		try {
			ObjectMessage msg = session.createObjectMessage(message);
			sender.send(msg);
		} catch (JMSException e) {
			e.printStackTrace();
		}
    }

}

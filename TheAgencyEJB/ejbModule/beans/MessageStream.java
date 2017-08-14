package beans;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;

import exceptions.ConnectionException;
import model.ServiceMessage;
import model.ServiceMessage.OperationType;
import service.MessageRequestLocal;
import util.SocketMessage;
import util.SocketMessage.messageType;

@Stateless
@LocalBean
public class MessageStream implements MessageStreamLocal {

 
	@EJB
	private MessageRequestLocal request;
	
	@EJB
	private AgencyRegistryLocal registry;
	
	@EJB
	private SocketSenderLocal socketSender;
	
    public MessageStream() {
    	
    }
    
    public void streamMessage(String content, String address) {
    	if(registry.getThisCenter().getAddress().equals(address)){
			SocketMessage m = new SocketMessage();
			m.setInfoStream(content);
			m.setMsgType(messageType.STREAM_MESSAGE);
			socketSender.socketSend(m);
		} else {
			ServiceMessage msg = new ServiceMessage(OperationType.STREAM_MESSAGE);
			msg.setMessageInfo(content);
			
			try {
				request.sendMessage(address, msg);
			} catch (ConnectionException | IOException | TimeoutException | InterruptedException e) {
				return;
			}
		}
    }

}

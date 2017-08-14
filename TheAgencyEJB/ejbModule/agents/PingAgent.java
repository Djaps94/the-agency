package agents;

import javax.ejb.EJB;
import javax.ejb.Stateful;

import com.fasterxml.jackson.annotation.JsonTypeName;

import beans.MessageStreamLocal;
import intercommunication.DispatcherLocal;
import model.ACLMessage;
import model.AID;
import model.Agent;

@SuppressWarnings("serial")
@Stateful
@JsonTypeName("Ping")
public class PingAgent extends Agent {

	@EJB
	private DispatcherLocal dispatcher;
	
	@EJB
	private MessageStreamLocal streamer;
		
	@Override
	public void handleMessage(ACLMessage message) {
		switch(message.getPerformative()){
		case INFORM: {
			streamer.streamMessage("Message recieved from: "+message.getSender().getName()+"."+"Message content: "+message.getContent(), message.getStreamTo());
		}
			break;
		default:
			break;
		}

	}
	
	@Override
	public AID getId() {
		return id;
	}
	
	@Override
	public void setId(AID id) {
		this.id = id;
	}

}

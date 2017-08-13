package agents;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateful;

import com.fasterxml.jackson.annotation.JsonTypeName;

import beans.MessageStreamLocal;
import intercommunication.ReceiverLocal;
import model.ACLMessage;
import model.ACLMessage.Performative;
import model.AID;
import model.Agent;

@SuppressWarnings("serial")
@Stateful
@JsonTypeName("Pong")
public class PongAgent extends Agent {

	@EJB
	private ReceiverLocal handler;
	
	@EJB
	private MessageStreamLocal streamer;
	
	@Override
	public void handleMessage(ACLMessage message) {
		switch(message.getPerformative()){
		case REQUEST: {
			streamer.streamMessage("Message recieved from: "+message.getSender().getName()+"."+"Message content: "+message.getContent(), message.getStreamTo());
			
			ACLMessage msg = new ACLMessage();
			msg.setStreamTo(message.getStreamTo());
			msg.setPerformative(Performative.INFORM);
			msg.setContent("Regards from buddy Pong.");
			List<AID> recievers = new ArrayList<AID>();
			recievers.add(message.getReplyTo());
			msg.setRecievers(recievers);
			msg.setSender(getId());
			handler.recieveAgentMessage(msg);
		}
			break;
		default:
			break;
		
		}

	}

}

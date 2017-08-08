package agents;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateful;

import com.fasterxml.jackson.annotation.JsonTypeName;

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
	
	@Override
	public void handleMessage(ACLMessage message) {
		switch(message.getPerformative()){
		case ACCEPT_PROPOSAL:
			break;
		case AGREE:
			break;
		case CANCEL:
			break;
		case CFP:
			break;
		case CONFIRM:
			break;
		case DISCONFIRM:
			break;
		case FAILURE:
			break;
		case INFORM:
			break;
		case INFORM_IF:
			break;
		case INFORM_REF:
			break;
		case NOT_UNDERSTOOD:
			break;
		case PROPAGATE:
			break;
		case PROPOSE:
			break;
		case PROXY:
			break;
		case QUERY_IF:
			break;
		case QUERY_REF:
			break;
		case REFUSE:
			break;
		case REJECT_PROPOSAL:
			break;
		case REQUEST: {
			System.out.println("Message recieved from: "+message.getSender().getName());
			System.out.println("Message content: "+message.getContent());
			ACLMessage msg = new ACLMessage();
			msg.setPerformative(Performative.INFORM);
			msg.setContent("Regards from buddy Pong.");
			List<AID> recievers = new ArrayList<AID>();
			recievers.add(message.getReplyTo());
			msg.setRecievers(recievers);
			msg.setSender(getId());
			handler.recieveAgentMessage(msg);
		}
			break;
		case REQUEST_WHEN:
			break;
		case REQUEST_WHENEVER:
			break;
		case SUBSCRIBE:
			break;
		default:
			break;
		
		}

	}

}

package agents;

import javax.ejb.EJB;
import javax.ejb.Stateful;

import com.fasterxml.jackson.annotation.JsonTypeName;

import intercommunication.MessageDispatcherLocal;
import model.ACLMessage;
import model.AID;
import model.Agent;

@SuppressWarnings("serial")
@Stateful
@JsonTypeName("Ping")
public class PingAgent extends Agent {

	@EJB
	private MessageDispatcherLocal dispatcher;
	
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
		case INFORM: {
			System.out.println("Message recieved from: "+message.getSender().getName());
			System.out.println("Message content: "+message.getContent());
		}
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
	
	@Override
	public AID getId() {
		return id;
	}
	
	@Override
	public void setId(AID id) {
		this.id = id;
	}

}

package agents;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.ejb.EJB;
import javax.ejb.Stateful;

import intercommunication.HandlerLocal;
import model.ACLMessage;
import model.ACLMessage.Performative;
import model.AID;
import model.Agent;

@SuppressWarnings("serial")
@Stateful
public class ContractNetInitiator extends Agent{

	@EJB
	private HandlerLocal handler;
	
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
			System.out.println("Message from "+message.getSender().getName());
			System.out.println(message.getContent());
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
		case PROPOSE: {
			System.out.println("Propose came from: "+message.getSender().getName());
			Random rn = new Random();
			int offer = rn.nextInt(5);
			int proposale = Integer.parseInt(message.getContent());
			List<AID> recievers = new ArrayList<AID>();
			recievers.add(message.getSender());
			ACLMessage msg = new ACLMessage();
			msg.setSender(getId());
			msg.setRecievers(recievers);
			if(offer != proposale){
				msg.setPerformative(Performative.REJECT_PROPOSAL);
				msg.setContent("I reject your proposal "+message.getSender().getName());
				handler.sendAgentMessage(msg);
			}else{
				msg.setPerformative(Performative.ACCEPT_PROPOSAL);
				msg.setContent("I accept your proposale of"+Integer.toString(proposale));
				handler.sendAgentMessage(msg);
			}
		}
			break;
		case PROXY:
			break;
		case QUERY_IF:
			break;
		case QUERY_REF:
			break;
		case REFUSE: {
			System.out.println("Message from "+message.getSender().getName());
			System.out.println("Okey, you refuse, i understand.");
		}
			break;
		case REJECT_PROPOSAL:
			break;
		case REQUEST:
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

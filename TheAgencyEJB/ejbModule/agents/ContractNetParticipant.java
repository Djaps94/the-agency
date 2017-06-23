package agents;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.ejb.EJB;
import javax.ejb.Stateful;

import intercommunication.HandlerLocal;
import model.ACLMessage;
import model.AID;
import model.Agent;
import model.ACLMessage.Performative;

@SuppressWarnings("serial")
@Stateful
public class ContractNetParticipant  extends Agent{
	
	@EJB
	private HandlerLocal handler;

	@Override
	public void handleMessage(ACLMessage message) {
		switch(message.getPerformative()){
		case ACCEPT_PROPOSAL: {
			System.out.println(message.getSender().getName()+", accepts proposal");
			ACLMessage msg = new ACLMessage();
			msg.setContent("We are very pleased, transaction will be completed soon enough.");
			msg.setPerformative(Performative.INFORM);
			msg.setSender(getId());
			List<AID> receivers = new ArrayList<AID>();
			receivers.add(message.getSender());
			handler.sendAgentMessage(msg);
		}
			break;
		case AGREE:
			break;
		case CANCEL:
			break;
		case CFP: {
			System.out.println("Call for proposal from: "+message.getSender().getName());
			Random rd = new Random();
			int acceptance = rd.nextInt(5);
			int sent = Integer.parseInt(message.getContent());
			ACLMessage msg = new ACLMessage();
			List<AID> recievers = new ArrayList<AID>();
			recievers.add(message.getSender());
			msg.setRecievers(recievers);
			if(sent != acceptance){
				msg.setPerformative(Performative.REFUSE);
				msg.setContent("I refuse your call!");
				handler.sendAgentMessage(msg);
			}else{
				msg.setSender(getId());
				msg.setContent(Integer.toString(acceptance));
				msg.setPerformative(Performative.PROPOSE);
				handler.sendAgentMessage(msg);
			}
		}
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
		case REJECT_PROPOSAL: {
			System.out.println("Message from "+message.getSender().getName());
			System.out.println(message.getContent());
		}
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

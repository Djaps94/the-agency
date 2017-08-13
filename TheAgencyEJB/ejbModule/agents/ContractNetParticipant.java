package agents;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.ejb.EJB;
import javax.ejb.Stateful;

import beans.MessageStreamLocal;
import intercommunication.ReceiverLocal;
import model.ACLMessage;
import model.ACLMessage.Performative;
import model.AID;
import model.Agent;

@SuppressWarnings("serial")
@Stateful
public class ContractNetParticipant  extends Agent{
	
	@EJB
	private ReceiverLocal handler;
	
	@EJB
	private MessageStreamLocal streamer;

	@Override
	public void handleMessage(ACLMessage message) {
		switch(message.getPerformative()){
		case ACCEPT_PROPOSAL: {
			streamer.streamMessage(message.getSender().getName()+", accepts proposal", message.getStreamTo());
			
			ACLMessage msg = new ACLMessage();
			msg.setContent("We are very pleased, transaction will be completed soon enough.");
			msg.setPerformative(Performative.INFORM);
			msg.setSender(getId());
			List<AID> receivers = new ArrayList<AID>();
			receivers.add(message.getSender());
			msg.setRecievers(receivers);
			msg.setStreamTo(message.getStreamTo());
			handler.recieveAgentMessage(msg);
		}
			break;
		case CFP: {
			streamer.streamMessage("Call for proposal from: "+message.getSender().getName()+" for "+getId().getName(), message.getStreamTo());
			
			System.out.println("Ovde saaam");
			int acceptance = new Random().nextInt(3);
			int sent = Integer.parseInt(message.getContent());
			ACLMessage msg = new ACLMessage();
			List<AID> recievers = new ArrayList<AID>();
			recievers.add(message.getSender());
			msg.setRecievers(recievers);
			msg.setSender(getId());
			msg.setStreamTo(message.getStreamTo());
			if(sent != acceptance){
				msg.setPerformative(Performative.REFUSE);
				msg.setContent("I refuse your call!");
				handler.recieveAgentMessage(msg);
			}else{
				msg.setContent(Integer.toString(acceptance));
				msg.setPerformative(Performative.PROPOSE);
				handler.recieveAgentMessage(msg);
			}
		}
			break;
		case REJECT_PROPOSAL: {
			streamer.streamMessage("Message from "+message.getSender().getName()+". "+message.getContent(), message.getStreamTo());
		}
			break;
		default:
			break;
		}
		
	}

}

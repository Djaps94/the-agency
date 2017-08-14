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
public class ContractNetInitiator extends Agent{

	@EJB
	private ReceiverLocal handler;
	
	@EJB
	private MessageStreamLocal streamer;

	@Override
	public void handleMessage(ACLMessage message) {
		switch(message.getPerformative()){
		case INFORM: {
			streamer.streamMessage("Message from "+message.getSender().getName()+"."+message.getContent(), message.getStreamTo());
		}
			break;
		case PROPOSE: {
			streamer.streamMessage("Propose came from: "+message.getSender().getName()+", with propose of "+message.getContent(), message.getStreamTo());
		
			int offer = new Random().nextInt(5);
			int proposale = Integer.parseInt(message.getContent());
			List<AID> recievers = new ArrayList<AID>();
			recievers.add(message.getSender());
			
			ACLMessage msg = new ACLMessage();
			msg.setSender(getId());
			msg.setRecievers(recievers);
			msg.setStreamTo(message.getStreamTo());
			if(offer != proposale){
				msg.setPerformative(Performative.REJECT_PROPOSAL);
				msg.setContent("I reject your proposal "+message.getSender().getName());
				handler.recieveAgentMessage(msg);
			}else{
				msg.setPerformative(Performative.ACCEPT_PROPOSAL);
				msg.setContent("I accept your proposale of"+Integer.toString(proposale));
				handler.recieveAgentMessage(msg);
			}
		}
			break;
		case REFUSE: {
			streamer.streamMessage("Message from "+message.getSender().getName()+". Okey, you refuse, i understand.", message.getStreamTo());
		}
			break;
		default:
			break;

		}

	}

}

package agents;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateful;

import com.jaunt.NotFound;
import com.jaunt.ResponseException;
import com.jaunt.UserAgent;

import intercommunication.ReceiverLocal;
import model.ACLMessage;
import model.ACLMessage.Performative;
import model.AID;
import model.Agent;


@Stateful
public class WeatherUmbrella extends Agent{

	@EJB
	private ReceiverLocal receiver;
	
	private static final long serialVersionUID = -8529262492470047623L;

	public WeatherUmbrella() {
        
    }

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
			
			UserAgent scraper = new UserAgent();
			List<AID> recievers = new ArrayList<>();
			ACLMessage msg = new ACLMessage();
			recievers.add(msg.getSender());
			msg.setRecievers(recievers);
			try {
				scraper.visit("https://www.weather2umbrella.com/");
				msg.setPerformative(Performative.INFORM);
				String link = scraper.doc.findFirst("<li id=menu-item-24>").getChildElements().get(0).getAt("href");
				scraper.visit(link);
			} catch (ResponseException | NotFound e) {
				msg.setPerformative(Performative.FAILURE);
				msg.setContent(message.getContent());
				receiver.recieveAgentMessage(msg);
			}
			
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

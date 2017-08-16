package agents;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.Stateful;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import model.ACLMessage;
import model.ACLMessage.Performative;
import model.Agent;


@Stateful
public class WeatherMaster extends Agent{

	private static final long serialVersionUID = -7609277672062916060L;

	public WeatherMaster() {
       
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
		case INFORM: {
			System.out.println("IT IS WORKING");
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
			//TODO: Does this node support weather slaves?
			List<ACLMessage> messageList = new ArrayList<>();
			String[] cities = message.getContent().split(",");
			for(String city : cities){
				ACLMessage msg = new ACLMessage();
				msg.setContent(city.trim());
				msg.setPerformative(Performative.REQUEST);
				msg.setSender(getId());
				messageList.add(msg);
			}
			
			if(message.isAccu()){
				AccuWeather(messageList);
			}else if(message.isUmbrella()){
				
			}else if(message.isMix()) {
				
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
	
	private void AccuWeather(List<ACLMessage> messages){
		for(ACLMessage msg : messages){
			try {
				InitialContext context = new InitialContext();
				Agent accu = (Agent)context.lookup("java:module/WeatherAccu");
				accu.handleMessage(msg);
			} catch (NamingException e) {
				e.printStackTrace();
			}
		}
	}

}

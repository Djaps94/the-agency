package agents;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateful;

import com.jaunt.Element;
import com.jaunt.NotFound;
import com.jaunt.ResponseException;
import com.jaunt.SearchException;
import com.jaunt.UserAgent;
import com.jaunt.component.Form;

import intercommunication.ReceiverLocal;
import model.ACLMessage;
import model.ACLMessage.Performative;
import model.AID;
import model.Agent;

@Stateful
public class WeatherAccu extends Agent {
    
	@EJB
	private ReceiverLocal receiver;
	
	private static final long serialVersionUID = -7183133844763416517L;

	public WeatherAccu() {
        
    }

	@Override
	public void handleMessage(ACLMessage message) {
		switch(message.getPerformative()) {
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
			recievers.add(message.getSender());
			msg.setRecievers(recievers);
			try {
				scraper.visit("https://www.accuweather.com/sr/rs/serbia-weather");
				msg.setPerformative(Performative.INFORM);
				Form form = scraper.doc.getForm("<form id=findcity>");
				form.setTextField("s", message.getContent().trim());
				form.submit();
				System.out.println(scraper.getLocation());
				try{
					Element li = scraper.doc.findFirst("<ul class=articles>").getElement(0);
					scraper.visit(li.findFirst("<a>").getAt("href"));
					System.out.println(scraper.getLocation());
					//TODO: call page scraping
				} catch (SearchException s){
					//TODO: call page scraping
				}
			} catch (ResponseException | NotFound e) {
				System.out.println("ERROR!");
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
	
	private void scrapeWeather(UserAgent scraper){
		try {
			String fiveDayWeatherLink = scraper.doc.findFirst("<ul class=subnav-tab-buttons>")
													.getElement(2)
													.getElement(0)
													.getAt("href");
			scraper.visit(fiveDayWeatherLink);
		} catch (NotFound | ResponseException e) {
			System.out.println("Error while trying to get to five days weather");
		}
	}

}

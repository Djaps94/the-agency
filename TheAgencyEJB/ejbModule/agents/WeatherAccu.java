package agents;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.Stateful;

import com.jaunt.Element;
import com.jaunt.Elements;
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
import util.Weather;
import util.WeatherDay;

@Stateful
public class WeatherAccu extends Agent {
    
	@EJB
	private ReceiverLocal receiver;
	
	private Weather weather;
	
	private static final long serialVersionUID = -7183133844763416517L;

	public WeatherAccu() {
        
    }

	@PostConstruct
	private void init() {
		weather = new Weather();
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
			weather.setCityName(message.getContent());
			ACLMessage msg = new ACLMessage();
			recievers.add(message.getSender());
			msg.setRecievers(recievers);
			try {
				scraper.visit("https://www.accuweather.com/sr/rs/serbia-weather");
				msg.setPerformative(Performative.INFORM);
				Form form = scraper.doc.getForm("<form id=findcity>");
				form.setTextField("s", message.getContent().trim());
				form.submit();
				try{
					Element li = scraper.doc.findFirst("<ul class=articles>").getElement(0);
					scraper.visit(li.findFirst("<a>").getAt("href"));
					scrapeWeather(scraper);
					msg.setContentObject(weather);
					receiver.recieveAgentMessage(msg);
				} catch (SearchException s){
					scrapeWeather(scraper);
					msg.setContentObject(weather);
					receiver.recieveAgentMessage(msg);
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
			Elements divs = scraper.doc.findEvery("<div id=feed-tabs>");
			Element targetDiv = null;
			for(Element div : divs){
				if(div.getChildElements().size() > 1){
					targetDiv = div;
					break;
				}
			}
			List<Element> weatherDays = targetDiv.getElement(1).getChildElements();
			for(Element element : weatherDays) {
				Element weatherDiv 	= element.getElement(0);
				Element infoDiv 	= weatherDiv.getElement(3);
				String day 	= weatherDiv.getElement(0).getElement(0).getText();
				String date = weatherDiv.getElement(1).getText();
				String conditions 	= infoDiv.getElement(1).getText();
				//TODO: from Far to Celzius &deg /
				String largeTemp 	= transformDegree(infoDiv.getElement(0).getElement(0).getText());
				String smallTemp 	= transformDegree(infoDiv.getElement(0).getElement(1).getText());
				WeatherDay weatherPerDay = new WeatherDay(day, date, largeTemp, smallTemp, conditions);
				weather.addWeatherDay(weatherPerDay);
			}
		} catch (NotFound | ResponseException e) {
			System.out.println("Error while trying to get to five days weather");
		}
	}
	
	private String transformDegree(String temp){
		if(temp.equals("Min"))
			return temp;
		
		String fahrenhait= temp.replace("&deg;", "").replace("/", "").trim();
		int celsius = (int) ((Integer.parseInt(fahrenhait) - 32) / 1.8); 
		return String.valueOf(celsius)+"&deg;";
	}

}

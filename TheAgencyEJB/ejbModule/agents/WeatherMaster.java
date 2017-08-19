package agents;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.Stateful;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import beans.SocketSenderLocal;
import model.ACLMessage;
import model.ACLMessage.Performative;
import model.Agent;
import util.SocketMessage;
import util.SocketMessage.messageType;
import util.Weather;


@Stateful
public class WeatherMaster extends Agent{

	@EJB
	private SocketSenderLocal sender;
	
	private static final long serialVersionUID = -7609277672062916060L;
	
	private int slaves;
	
	private List<Weather> weather;

	public WeatherMaster() {
       
    }
	
	@PostConstruct
	private void init() {
		weather = new ArrayList<>();
	}

	@Override
	public void handleMessage(ACLMessage message) {
		switch(message.getPerformative()){
		case FAILURE:
			break;
		case INFORM: {
			synchronized (message) {
				Weather response = (Weather)message.getContentObject();
				weather.add(response);
				slaves--;
				
				if(slaves == 0){
					SocketMessage socketMsg = new SocketMessage();
					socketMsg.setMsgType(messageType.STREAM_WEATHER);
					socketMsg.setInfoStream(weather);
					sender.socketSend(socketMsg);
					
					weather.clear();
				}
			}
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
			String[] cities = message.getContent().split(";");
			slaves = cities.length;
			for(String city : cities){
				ACLMessage msg = new ACLMessage();
				msg.setContent(city.trim());
				msg.setPerformative(Performative.REQUEST);
				msg.setSender(getId());
				messageList.add(msg);
			}
			
			if(message.isAccu()){
				accuWeather(messageList);
			}else if(message.isUmbrella()){
				umbrellaWeather(messageList);
			}else if(message.isMix()) {
				mixWeather(messageList);
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
	
	private void callWeatherBoy(ACLMessage msg, String boy) { 
		try {
			InitialContext context = new InitialContext();
			Agent accu = (Agent)context.lookup("java:module/"+boy);
			accu.handleMessage(msg);
		} catch (NamingException e) {
			e.printStackTrace();
		}	
	}
	
	private void accuWeather(List<ACLMessage> messages){
		for(ACLMessage msg : messages){
			callWeatherBoy(msg, "WeatherAccu");
		}
	}
	
	private void umbrellaWeather(List<ACLMessage> messages) {
		for(ACLMessage msg : messages){
			callWeatherBoy(msg, "WeatherUmbrella");
		}	
	}
	
	private void mixWeather(List<ACLMessage> messages) {
		for(int i = 0; i < messages.size(); i++){
			if(i%2 == 0)
				callWeatherBoy(messages.get(i), "WeatherAccu");
			else
				callWeatherBoy(messages.get(i), "WeatherUmbrella");
		}
	}

}

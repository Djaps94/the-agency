package agents;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.ejb.Asynchronous;
import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateful;

import intercommunication.HandlerLocal;
import model.ACLMessage;
import model.ACLMessage.Performative;
import model.AID;
import model.Agent;


@SuppressWarnings("serial")
@Stateful
@LocalBean
public class MapReduceSlave extends Agent{

	private Map<Character, Integer> numberOfChars;
	
    @EJB
    private HandlerLocal handler;
    
	public MapReduceSlave() {
   
    }
	
	@PostConstruct
	private void initialise(){
		numberOfChars = new HashMap<Character, Integer>();
	}

	@Override
	@Asynchronous
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
			System.out.println("Message from "+message.getSender().getName());
			System.out.println("Count words in: "+message.getContent());
			InputStream in 		= this.getClass().getResourceAsStream("/"+message.getContent());
			BufferedReader br   = new BufferedReader(new InputStreamReader(in));
			StringBuffer buffer = new StringBuffer();
			br.lines().forEach(line -> buffer.append(line));
			List<Character> characters = buffer.toString().chars().mapToObj(c ->(char)c).collect(Collectors.toList());
			for(Character c : characters){
				if(c.equals(",") || c.equals(".") || c.equals("?") || c.equals("!") || c.equals(" "))
					continue;
				if(numberOfChars.containsKey(c)){
					numberOfChars.put(c, numberOfChars.get(c)+1);
				}else{
					numberOfChars.put(c, 1);
				}
			}
			ACLMessage msg = new ACLMessage();
			msg.setContentObject(numberOfChars);
			msg.setContent("I've finished.");
			List<AID> recievers = new LinkedList<>();
			recievers.add(message.getSender());
			msg.setRecievers(recievers);
			msg.setPerformative(Performative.INFORM);
			handler.sendAgentMessage(msg);
			
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

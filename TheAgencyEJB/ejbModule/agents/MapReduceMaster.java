package agents;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.Stateful;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import beans.AgencyManagerLocal;
import beans.MessageStreamLocal;
import model.ACLMessage;
import model.ACLMessage.Performative;
import model.Agent;

@SuppressWarnings("serial")
@Stateful
public class MapReduceMaster extends Agent{
	
    @EJB
    private AgencyManagerLocal manager;
    
    @EJB
    private MessageStreamLocal streamer;
    
    private Map<Character, Integer> words;
    private int slaves;
    
    @PostConstruct
    private void initialise(){
    	words = new HashMap<Character, Integer>();
    }

	@SuppressWarnings("unchecked")
	@Override
	public void handleMessage(ACLMessage message) {
		switch(message.getPerformative()){
		case INFORM: {
			synchronized (message) {
				streamer.streamMessage(message.getContent(), message.getStreamTo());
				((Map<Character,Integer>)message.getContentObject()).entrySet().forEach(entry -> {
					if(words.containsKey(entry.getKey()))	
						words.put(entry.getKey(), words.get(entry.getKey())+ entry.getValue());
					else
						words.put(entry.getKey(), entry.getValue());
						});
				slaves--;
				if(slaves == 0){
					words.entrySet().stream().sorted(Map.Entry.<Character,Integer>comparingByValue().reversed())
					.limit(10)
					.forEach(System.out::println);
					
				}
			}
			
		}
			break;
		case REQUEST: {
			boolean support = manager.getSupportedTypesStream().anyMatch(type -> type.getName().equals("MapReduceSlave"));
			File file = new File("/home/predrag/workspace/the-agency/TheAgencyEJB/files");
			slaves = file.listFiles().length;
			for(int i = 0; i < slaves; i++){
				ACLMessage msg = new ACLMessage();
				msg.setPerformative(Performative.REQUEST);
				msg.setSender(getId());
				msg.setContent(file.listFiles()[i].getName());
				msg.setStreamTo(message.getStreamTo());
				if(support){
					try {
						InitialContext context = new InitialContext();
						Agent agent = (Agent)context.lookup("java:module/MapReduceSlave");
						agent.handleMessage(msg);
					} catch (NamingException e) {
						e.printStackTrace();
					}
				}else{
					//TODO: preko rabbita posalji
				}
			}
		}
			break;
		default:
			break;
		
		}
		
	}

}

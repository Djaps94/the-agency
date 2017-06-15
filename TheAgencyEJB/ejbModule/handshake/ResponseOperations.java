package handshake;

import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import org.zeromq.ZMQ;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import exceptions.ConnectionException;
import exceptions.NodeExistsException;
import exceptions.RegisterSlaveException;
import model.Agent;
import model.AgentCenter;
import model.AgentType;
import model.HandshakeMessage;

@Stateless
public class ResponseOperations implements ResponseOperationsLocal{
	
	@EJB
	private HandshakeDealerLocal dealer;

	public void sendRegisterResponse(HandshakeMessage message, ZMQ.Socket response, ObjectMapper mapper) throws ConnectionException, RegisterSlaveException, JsonProcessingException, NodeExistsException{
		List<AgentCenter> centers = dealer.registerCenter(message);
		if(centers.isEmpty())
			response.send("Register successful", 0);
		else{
			HandshakeMessage msg = new HandshakeMessage();
			msg.setCenters(centers);
			msg.setType(HandshakeMessage.handshakeType.GET_CENTERS);
			String m = mapper.writeValueAsString(msg);
			response.send(m);
		}
	}
	
	public void sendGetTypesResponse(HandshakeMessage message, ZMQ.Socket response, ObjectMapper mapper) throws ConnectionException, JsonProcessingException{
		Map<String, Set<AgentType>> types = dealer.registerAgentTypes(message);
		HandshakeMessage msg = new HandshakeMessage();
		msg.setOtherTypes(types);
		String m = mapper.writeValueAsString(msg);
		response.send(m);
	}
	
	public void sendGetRunningResponse(ZMQ.Socket response, ObjectMapper mapper) throws JsonProcessingException{
		List<Agent> agents = dealer.getRunningAgents();
		HandshakeMessage msg = new HandshakeMessage();
		msg.setRunningAgents(agents);
		String data = mapper.writeValueAsString(msg);
		response.send(data);
	}
	
	public void addTypes(HandshakeMessage message, ZMQ.Socket response){
		dealer.addTypes(message);
		response.send("Added types to other nodes.");
	}
	
	public void rollback(HandshakeMessage message, ZMQ.Socket response, ObjectMapper mapper){
		try {
			dealer.rollback(message);
			HandshakeMessage msg = new HandshakeMessage();
			msg.setMessage("Rollback completed!");
			String data = mapper.writeValueAsString(msg);
			response.send(data);
		} catch (ConnectionException | JsonProcessingException e) {
			response.send("Shuting down server. Rollback failed.");
		}
		
	}
	
	
}

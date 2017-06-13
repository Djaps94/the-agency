package handshake;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;
import javax.ejb.AccessTimeout;
import javax.ejb.EJB;
import javax.ejb.Singleton;
import javax.ejb.Timeout;
import javax.ejb.Timer;
import javax.ejb.TimerService;

import org.zeromq.ZMQ;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.io.JsonEOFException;
import com.fasterxml.jackson.databind.ObjectMapper;

import beans.AgencyRegistryLocal;
import beans.NetworkManagmentLocal;
import exceptions.ConnectionException;
import exceptions.RegisterSlaveException;
import model.AgentCenter;
import model.AgentType;
import model.HandshakeMessage;
import util.PortTransformation;

@Singleton
@AccessTimeout(value = 100000)
public class HandshakeResponse implements HandshakeResponseLocal{	
	
	@EJB
	private AgencyRegistryLocal registry;
	
	@EJB
	private NetworkManagmentLocal nodesManagment;
	
	@EJB
	private HandshakeDealerLocal dealer;
	
	@EJB
	private HandshakeRequesterLocal requester;
	
	@Resource
	private TimerService timer;
	
	
	public HandshakeResponse() { }
	
	public void startTimer(){
		timer.createTimer(1000, "ZeroMQ");
	}
	
	@Timeout
	public void waitMessage(Timer timer){
		ObjectMapper mapper   = new ObjectMapper();
		ZMQ.Context context = ZMQ.context(1);
		ZMQ.Socket response = context.socket(ZMQ.REP);
		response.bind("tcp://"+PortTransformation.transform(registry.getThisCenter().getAddress(),0));
			while(!Thread.currentThread().isInterrupted()){
				try {
					String data = response.recvStr();
					if(data == null)
						continue;
					HandshakeMessage message = mapper.readValue(data, HandshakeMessage.class);
					switch(message.getType()){
					case REGISTER: { 
						try {
							sendRegisterResponse(message, response, mapper);
						} catch (RegisterSlaveException | ConnectionException e) {
							try {
								sendRegisterResponse(message, response, mapper);
							} catch (RegisterSlaveException | ConnectionException e1) {
								try {
									requester.sendMessage(nodesManagment.getMasterAddress(), message);
								} catch (ConnectionException e2) {
									//TODO: shutdown server
								}
							}
						}				 
					} 
					break;
					case GET_TYPES: {
						try {
							sendGetTypesResponse(message, response, mapper);
						} catch (ConnectionException | JsonProcessingException e) {
							try {
								sendGetTypesResponse(message, response, mapper);
							} catch (ConnectionException | JsonEOFException e1) {
								try {
									requester.sendMessage(nodesManagment.getMasterAddress(), message);
								} catch (ConnectionException e2) {
									// TODO: shutdown server
								}
							}
						}
					}
					break;
					case DELIVER_TYPES: addTypes(message, response); 
					break;
					case ROLLBACK: rollback(message, response, mapper); 
					break;
					default:
						break;
					
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			response.close();
			context.term();
}
		
	private void sendRegisterResponse(HandshakeMessage message, ZMQ.Socket response, ObjectMapper mapper) throws ConnectionException, RegisterSlaveException, JsonProcessingException{
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
	
	private void sendGetTypesResponse(HandshakeMessage message, ZMQ.Socket response, ObjectMapper mapper) throws ConnectionException, JsonProcessingException{
		Map<String, Set<AgentType>> types = dealer.registerAgentTypes(message);
		HandshakeMessage msg = new HandshakeMessage();
		msg.setOtherTypes(types);
		String m = mapper.writeValueAsString(msg);
		response.send(m);
	}
	
	private void addTypes(HandshakeMessage message, ZMQ.Socket response){
		dealer.addTypes(message);
		response.send("Added types to other nodes.");
	}
	
	private void rollback(HandshakeMessage message, ZMQ.Socket response, ObjectMapper mapper){
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

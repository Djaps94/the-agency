package handshake;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.Resource;
import javax.ejb.AccessTimeout;
import javax.ejb.EJB;
import javax.ejb.Singleton;
import javax.ejb.Timeout;
import javax.ejb.Timer;
import javax.ejb.TimerService;

import org.zeromq.ZMQ;

import com.fasterxml.jackson.databind.ObjectMapper;

import beans.CenterRegistryLocal;
import beans.NetworkManagmentLocal;
import exceptions.ConnectionException;
import exceptions.RegisterSlaveException;
import model.AgentCenter;
import model.HandshakeMessage;
import util.PortTransformation;

@Singleton
@AccessTimeout(value = 100000)
public class HandshakeResponse implements HandshakeResponseLocal{	
	
	@EJB
	private CenterRegistryLocal registry;
	
	@EJB
	private NetworkManagmentLocal nodesManagment;
	
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
		System.out.println("STIGAO SAM OVDE");
			while(!Thread.currentThread().isInterrupted()){
				try {
					String data = response.recvStr();
					if(data == null)
						continue;
					HandshakeMessage message = mapper.readValue(data, HandshakeMessage.class);
					switch(message.getType()){
					case REGISTER: { 
						try {
							List<AgentCenter> centers = registerCenter(message);
							if(centers == null)
								response.send("Register successful", 0);
							else{
								HandshakeMessage msg = new HandshakeMessage();
								msg.setCenters(centers);
								msg.setType(HandshakeMessage.handshakeType.GET_CENTERS);
								String m = mapper.writeValueAsString(msg);
								response.send(m);
							}
					} catch (RegisterSlaveException | ConnectionException e) {
						try {
							List<AgentCenter> centers = registerCenter(message);
							if(centers == null)
								response.send("Register successful", 0);
							else{
								HandshakeMessage msg = new HandshakeMessage();
								msg.setCenters(centers);
								msg.setType(HandshakeMessage.handshakeType.GET_CENTERS);
								String m = mapper.writeValueAsString(msg);
								response.send(m);
							}
						} catch (RegisterSlaveException | ConnectionException e1) {
							//TODO: rollback!
						}
					}
									 
								   } break;
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
	
	private List<AgentCenter> registerCenter(HandshakeMessage message) throws RegisterSlaveException, ConnectionException{
		if(nodesManagment.isMaster()){
			registry.addCenter(message.getCenter());
			
			for(AgentCenter center : registry.getCenters()){
				if(!center.getAddress().equals(message.getCenter().getAddress()))
						requester.sendMessage(center.getAddress(), message);
			}
			return registry.getCenters().stream()
										.filter(center -> !center.getAddress().equals(message.getCenter().getAddress()))
										.collect(Collectors.toList());
		}
		
		registry.addCenter(message.getCenter());
		return null;
	}
}

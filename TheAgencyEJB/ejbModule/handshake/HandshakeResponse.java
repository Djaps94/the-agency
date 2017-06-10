package handshake;

import java.io.IOException;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.Singleton;

import org.zeromq.ZMQ;

import com.fasterxml.jackson.databind.ObjectMapper;

import beans.CenterRegistryLocal;
import model.AgentCenter;
import model.HandshakeMessage;

@Singleton
public class HandshakeResponse implements HandshakeResponseLocal{

	private ZMQ.Context context;
	private ZMQ.Socket response;
	private ObjectMapper mapper;
	
	@EJB
	private CenterRegistryLocal registry;
	
	public HandshakeResponse() { }
	
	@PostConstruct
	private void initialise(){
		mapper   = new ObjectMapper();
		context  = ZMQ.context(1);
		response = context.socket(ZMQ.REP);
		response.bind("tcp://"+registry.getThisCenter().getAddress());
	}
	
	public void waitMessage(){
		new Thread() {
			@Override
			public void run() {
				while(true){
					try {
						String data = response.recvStr();
						if(data == null)
							continue;
						HandshakeMessage message = mapper.readValue(response.recvStr(), HandshakeMessage.class);
						switch(message.getType()){
						case REGISTER: { registerCenter(message.getCenter());
										 response.send("Register successful", 0);
									   }
										 break;
						default:
							break;
						
						}
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		};
	}
	
	private void registerCenter(AgentCenter center){
		registry.addCenter(center);
	}
	
	
}

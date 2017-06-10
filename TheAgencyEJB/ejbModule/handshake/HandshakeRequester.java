package handshake;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.Stateless;

import org.zeromq.*;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import model.HandshakeMessage;

@Stateless
public class HandshakeRequester implements HandshakeRequesterLocal {

	private ZMQ.Context context;
	private ZMQ.Socket request;
	
	public HandshakeRequester() { }
	
	@PostConstruct
	public void initialise(){
		context = ZMQ.context(1);
		request = context.socket(ZMQ.REQ);
	}
	
	public void sendMessage(String destination, HandshakeMessage message){
		request.connect("tcp://"+destination);
		ObjectMapper mapper = new ObjectMapper();
		try {
			request.send(mapper.writeValueAsString(message));
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
	}
	
	@PreDestroy
	public void destroy(){
		request.close();
		context.term();
	}
}

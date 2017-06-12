package handshake;

import java.io.IOException;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.Stateless;

import org.zeromq.*;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import exceptions.ConnectionException;
import model.HandshakeMessage;
import util.PortTransformation;

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
	
	public HandshakeMessage sendMessage(String destination, HandshakeMessage message) throws ConnectionException{
		request.connect("tcp://"+PortTransformation.transform(destination, 0));
		ObjectMapper mapper = new ObjectMapper();
		try {
			request.send(mapper.writeValueAsString(message));
		} catch (JsonProcessingException e) {
			throw new ConnectionException("Could not send message!");
		}
		String data = request.recvStr();
		HandshakeMessage msg = null;
		try {
			msg = mapper.readValue(data, HandshakeMessage.class);
		} catch (Exception e) {
			return msg;
		}
		return msg;
	}
	
	@PreDestroy
	public void destroy(){
		request.close();
		context.term();
	}
}

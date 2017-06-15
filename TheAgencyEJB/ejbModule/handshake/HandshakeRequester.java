package handshake;

import javax.annotation.PreDestroy;
import javax.ejb.Stateless;

import org.zeromq.ZMQ;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import exceptions.ConnectionException;
import model.HandshakeMessage;
import util.PortTransformation;

@Stateless
public class HandshakeRequester implements HandshakeRequesterLocal {
	
	public HandshakeRequester() { }
		
	public HandshakeMessage sendMessage(String destination, HandshakeMessage message) throws ConnectionException{
		ZMQ.Context context = ZMQ.context(1);
		ZMQ.Socket request = context.socket(ZMQ.REQ);
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
			request.close();
			context.term();
			return msg;
		}
		request.close();
		context.term();
		return msg;
	}
	
}

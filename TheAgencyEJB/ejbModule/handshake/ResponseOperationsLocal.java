package handshake;

import javax.ejb.Local;

import org.zeromq.ZMQ;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import exceptions.ConnectionException;
import exceptions.NodeExistsException;
import exceptions.RegisterSlaveException;
import model.HandshakeMessage;

@Local
public interface ResponseOperationsLocal {
	
	public void sendRegisterResponse(HandshakeMessage message, ZMQ.Socket response, ObjectMapper mapper) throws ConnectionException, RegisterSlaveException, JsonProcessingException, NodeExistsException;
	public void sendGetTypesResponse(HandshakeMessage message, ZMQ.Socket response, ObjectMapper mapper) throws ConnectionException, JsonProcessingException;
	public void sendGetRunningResponse(ZMQ.Socket response, ObjectMapper mapper) throws JsonProcessingException;
	public void addTypes(HandshakeMessage message, ZMQ.Socket response);
	public void rollback(HandshakeMessage message, ZMQ.Socket response, ObjectMapper mapper);


}

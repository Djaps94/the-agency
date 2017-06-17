package handshake;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

import javax.ejb.Local;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.client.AMQP.BasicProperties;
import com.rabbitmq.client.Channel;

import exceptions.ConnectionException;
import exceptions.NodeExistsException;
import exceptions.RegisterSlaveException;
import model.HandshakeMessage;

@Local
public interface ResponseOperationsLocal {
	
	public void sendRegisterResponse(HandshakeMessage message, Channel channel, ObjectMapper mapper, BasicProperties property) throws ConnectionException, RegisterSlaveException, NodeExistsException, IOException, TimeoutException, InterruptedException;
	public void sendGetTypesResponse(HandshakeMessage message, Channel channel, ObjectMapper mapper, BasicProperties property) throws ConnectionException, IOException, TimeoutException, InterruptedException;
	public void sendGetRunningResponse(Channel channel, ObjectMapper mapper, BasicProperties property) throws IOException;
	public void addTypes(HandshakeMessage message, Channel channel, BasicProperties property) throws IOException;
	public void rollback(HandshakeMessage message, Channel channel, ObjectMapper mapper, BasicProperties property) throws IOException;

}

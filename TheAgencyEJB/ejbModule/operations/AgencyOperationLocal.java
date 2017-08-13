package operations;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

import javax.ejb.Local;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.client.AMQP.BasicProperties;
import com.rabbitmq.client.Channel;

import exceptions.ConnectionException;
import exceptions.NodeExistsException;
import exceptions.RegisterSlaveException;
import model.ServiceMessage;

@Local
public interface AgencyOperationLocal {
	
	void sendRegisterResponse(ServiceMessage message, Channel channel, ObjectMapper mapper, BasicProperties property) throws ConnectionException, RegisterSlaveException, NodeExistsException, IOException, TimeoutException, InterruptedException;
	void sendGetTypesResponse(ServiceMessage message, Channel channel, ObjectMapper mapper, BasicProperties property) throws ConnectionException, IOException, TimeoutException, InterruptedException;
	void sendGetRunningResponse(Channel channel, ObjectMapper mapper, BasicProperties property) throws IOException;
	void addTypes(ServiceMessage message, Channel channel, BasicProperties property) throws IOException;
	void rollback(ServiceMessage message, Channel channel, ObjectMapper mapper, BasicProperties property) throws IOException;
	void deleteRunningAgent(ServiceMessage message, Channel channel, BasicProperties property) throws IOException;
	void addAgent(ServiceMessage message, Channel channel,BasicProperties property) throws IOException;
	void runAgent(ServiceMessage message, Channel channel, BasicProperties property) throws IOException;
	void stopAgent(ServiceMessage message, Channel channel, BasicProperties property) throws IOException;
	void streamMessage(ServiceMessage message, Channel channel, BasicProperties property) throws IOException;
}

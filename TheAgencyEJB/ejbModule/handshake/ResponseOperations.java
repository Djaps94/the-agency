package handshake;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeoutException;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.client.AMQP.BasicProperties;
import com.rabbitmq.client.Channel;

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

	public void sendRegisterResponse(HandshakeMessage message, Channel channel, ObjectMapper mapper, BasicProperties property) throws ConnectionException, RegisterSlaveException, NodeExistsException, IOException, TimeoutException, InterruptedException{
		List<AgentCenter> centers = dealer.registerCenter(message);
		if(centers.isEmpty())
			channel.basicPublish("", property.getReplyTo(), new BasicProperties().builder().build(), "Register successful".getBytes());
		else{
			HandshakeMessage msg = new HandshakeMessage();
			msg.setCenters(centers);
			msg.setType(HandshakeMessage.handshakeType.GET_CENTERS);
			String m = mapper.writeValueAsString(msg);
			channel.basicPublish("", property.getReplyTo(), new BasicProperties().builder().build(), m.getBytes());
		}
	}
	
	public void sendGetTypesResponse(HandshakeMessage message, Channel channel, ObjectMapper mapper, BasicProperties property) throws ConnectionException, IOException, TimeoutException, InterruptedException{
		Map<String, Set<AgentType>> types = dealer.registerAgentTypes(message);
		HandshakeMessage msg = new HandshakeMessage();
		msg.setOtherTypes(types);
		String m = mapper.writeValueAsString(msg);
		channel.basicPublish("", property.getReplyTo(), new BasicProperties().builder().build(), m.getBytes());
	}
	
	public void sendGetRunningResponse(Channel channel, ObjectMapper mapper, BasicProperties property) throws IOException{
		List<Agent> agents = dealer.getRunningAgents();
		HandshakeMessage msg = new HandshakeMessage();
		msg.setRunningAgents(agents);
		String data = mapper.writeValueAsString(msg);
		channel.basicPublish("", property.getReplyTo(), new BasicProperties().builder().build(), data.getBytes());
	}
	
	public void addTypes(HandshakeMessage message, Channel channel, BasicProperties property) throws IOException{
		dealer.addTypes(message);
		channel.basicPublish("", property.getReplyTo(), new BasicProperties().builder().build(), "Add types to other nodes".getBytes());
	}
	
	public void rollback(HandshakeMessage message, Channel channel, ObjectMapper mapper, BasicProperties property) throws IOException{
		try {
			dealer.rollback(message);
			HandshakeMessage msg = new HandshakeMessage();
			String data = mapper.writeValueAsString(msg);
			channel.basicPublish("", property.getReplyTo(), new BasicProperties().builder().build(), data.getBytes());
		} catch (ConnectionException | JsonProcessingException | TimeoutException | InterruptedException e) {
			channel.basicPublish("", property.getReplyTo(), new BasicProperties().builder().build(), "rollback failed".getBytes());
		}
		
	}

	@Override
	public void deleteRunningAgent(HandshakeMessage message, Channel channel, BasicProperties property) throws IOException {
		dealer.deleteAgent(message);
		channel.basicPublish("", property.getReplyTo(), new BasicProperties().builder().build(), "Agent deleted".getBytes());
	}

	@Override
	public void addAgent(HandshakeMessage message, Channel channel, BasicProperties property) throws IOException {
		dealer.addAgent(message);
		channel.basicPublish("", property.getReplyTo(), new BasicProperties().builder().build(), "Agent added".getBytes());
		
	}

	@Override
	public void runAgent(HandshakeMessage message, Channel channel, BasicProperties property) throws IOException {
		Agent agent = dealer.runAgent(message);
		HandshakeMessage msg = new HandshakeMessage();
		msg.setAgent(agent);
		ObjectMapper mapper = new ObjectMapper();
		String data = mapper.writeValueAsString(msg);
		channel.basicPublish("", property.getReplyTo(), new BasicProperties().builder().build(), data.getBytes());
		
	}
}	

package operations;

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
import model.AID;
import model.AgentCenter;
import model.AgentType;
import model.ServiceMessage;

@Stateless
public class AgencyOperation implements AgencyOperationLocal{
	
	@EJB
	private WorkerLocal dealer;

	public void sendRegisterResponse(ServiceMessage message, Channel channel, ObjectMapper mapper, BasicProperties property) throws ConnectionException, RegisterSlaveException, NodeExistsException, IOException, TimeoutException, InterruptedException{
		List<AgentCenter> centers = dealer.registerCenter(message);
		
		if(centers.isEmpty())
			channel.basicPublish("", property.getReplyTo(), new BasicProperties().builder().build(), "Register successful".getBytes());
		else{
			ServiceMessage msg = new ServiceMessage();
			msg.setCenters(centers);
			msg.setType(ServiceMessage.OperationType.GET_CENTERS);
			String m = mapper.writeValueAsString(msg);
			channel.basicPublish("", property.getReplyTo(), new BasicProperties().builder().build(), m.getBytes());
		}
	}
	
	public void sendGetTypesResponse(ServiceMessage message, Channel channel, ObjectMapper mapper, BasicProperties property) throws ConnectionException, IOException, TimeoutException, InterruptedException{
		Map<String, Set<AgentType>> types = dealer.registerAgentTypes(message);
		
		ServiceMessage msg = new ServiceMessage();
		msg.setOtherTypes(types);
		String m = mapper.writeValueAsString(msg);
		channel.basicPublish("", property.getReplyTo(), new BasicProperties().builder().build(), m.getBytes());
	}
	
	public void sendGetRunningResponse(Channel channel, ObjectMapper mapper, BasicProperties property) throws IOException{
		Map<String, List<AID>> agents = dealer.getRunningAgents();
		
		ServiceMessage msg = new ServiceMessage();
		msg.setOtherAgents(agents);
		String data = mapper.writeValueAsString(msg);
		channel.basicPublish("", property.getReplyTo(), new BasicProperties().builder().build(), data.getBytes());
	}
	
	public void addTypes(ServiceMessage message, Channel channel, BasicProperties property) throws IOException{
		dealer.addTypes(message);
		channel.basicPublish("", property.getReplyTo(), new BasicProperties().builder().build(), "Add types to other nodes".getBytes());
	}
	
	public void rollback(ServiceMessage message, Channel channel, ObjectMapper mapper, BasicProperties property) throws IOException{
		try {
			dealer.rollback(message);
			ServiceMessage msg = new ServiceMessage();
			String data = mapper.writeValueAsString(msg);
			channel.basicPublish("", property.getReplyTo(), new BasicProperties().builder().build(), data.getBytes());
		} catch (ConnectionException | JsonProcessingException | TimeoutException | InterruptedException e) {
			channel.basicPublish("", property.getReplyTo(), new BasicProperties().builder().build(), "rollback failed".getBytes());
		}
		
	}

	@Override
	public void deleteRunningAgent(ServiceMessage message, Channel channel, BasicProperties property) throws IOException {
		dealer.deleteAgent(message);
		channel.basicPublish("", property.getReplyTo(), new BasicProperties().builder().build(), "Agent deleted".getBytes());
	}

	@Override
	public void addAgent(ServiceMessage message, Channel channel, BasicProperties property) throws IOException {
		dealer.addAgent(message);
		
		ServiceMessage msg = new ServiceMessage();
		ObjectMapper mapper = new ObjectMapper();
		String data = mapper.writeValueAsString(msg);
		channel.basicPublish("", property.getReplyTo(), new BasicProperties().builder().build(), data.getBytes());
		
	}

	@Override
	public void runAgent(ServiceMessage message, Channel channel, BasicProperties property) throws IOException {
		AID agent = dealer.runAgent(message);
		
		ServiceMessage msg = new ServiceMessage();
		msg.setAid(agent);
		ObjectMapper mapper = new ObjectMapper();
		String data = mapper.writeValueAsString(msg);
		channel.basicPublish("", property.getReplyTo(), new BasicProperties().builder().build(), data.getBytes());
		
	}

	@Override
	public void stopAgent(ServiceMessage message, Channel channel, BasicProperties property) throws IOException {
		AID agent = dealer.stopAgent(message);
		
		ServiceMessage msg = new ServiceMessage();
		msg.setAid(agent);
		ObjectMapper mapper = new ObjectMapper();
		String data = mapper.writeValueAsString(agent);
		channel.basicPublish("", property.getReplyTo(), new BasicProperties().builder().build(), data.getBytes());
	}
	
	@Override
	public void streamMessage(ServiceMessage message, Channel channel, BasicProperties property) throws IOException {
		dealer.streamMessage(message);
		channel.basicPublish("", property.getReplyTo(), new BasicProperties().builder().build(), "Message streamed".getBytes());

	}
}	

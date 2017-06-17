package heartbeat;

import java.io.IOException;
import java.net.URISyntaxException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Singleton;
import javax.ejb.Timeout;
import javax.ejb.Timer;
import javax.ejb.TimerConfig;
import javax.ejb.TimerService;

import com.rabbitmq.client.AMQP.BasicProperties;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.DefaultSaslConfig;
import com.rabbitmq.client.Envelope;
import com.rabbitmq.client.JDKSaslConfig;
import com.rabbitmq.client.SaslConfig;

import beans.AgencyManagerLocal;
import beans.AgencyRegistryLocal;
import model.Agent;
import model.AgentCenter;
import util.HeartbeatConsumer;
import util.PortTransformation;


@Singleton
@LocalBean
public class HeartbeatRequest implements HeartbeatRequestLocal {

	@Resource
	private TimerService timer;
	
	@EJB
	private AgencyRegistryLocal registry;
	
	@EJB
	private AgencyManagerLocal manager;
	
	private ConnectionFactory factory;
	private Connection connection;
	private Channel channel;
	private String RequestQueueName;
	private String ReplyQueueName;
	private BlockingQueue<String> response;
	
    public HeartbeatRequest() {
        // TODO Auto-generated constructor stub
    }
    
    @PostConstruct
    private void initalise(){
    	
//    	factory.setHost("127.0.0.1");
//    	factory.setPort(5672);
//    	factory.setUsername("guest");
//    	factory.setPassword("guest");
//		factory.setVirtualHost("/");
    	try {
    		factory  = new ConnectionFactory();
        	response = new ArrayBlockingQueue<>(1);
        	factory.setHost("127.0.0.1");
        	factory.setPort(5672);
        	factory.setVirtualHost("/");
			connection = factory.newConnection();
			channel    = connection.createChannel();
			ReplyQueueName = channel.queueDeclare().getQueue();
		} catch (IOException | TimeoutException e) { }
		RequestQueueName = "Heartbeat";
    }

	@Override
	public void startTimer() {
		timer.createIntervalTimer(1000*15, 1000*15, new TimerConfig("Heartbeat", false));
	}
	
	@Timeout
	private void sendPulse(Timer timer){
		List<AgentCenter> deadList = new ArrayList<>();
		for(AgentCenter center : registry.getCenters()){
			String data = checkPulse(center);
			System.out.println(data);
			if(data == null){
				System.out.println("ONE MORE TIME");
				String temp = checkPulse(center);
				if(temp == null){
					deadList.add(center);
				}
			}
		}
			if(!deadList.isEmpty())
				removeDeadCenter(deadList);
		System.out.println("I am here :D");
	}
	
	private String checkPulse(AgentCenter center){
		try {
			BasicProperties properties = new BasicProperties()
											 .builder()
											 .replyTo(ReplyQueueName)
											 .correlationId(center.getAlias())
											 .build();
			
			channel.basicPublish("", RequestQueueName, properties, "Check pulse".getBytes());
			HeartbeatConsumer consumer = new HeartbeatConsumer(channel, center.getAlias(), response);
			channel.basicConsume(ReplyQueueName, true, consumer );
			return response.poll(5, TimeUnit.SECONDS);
		} catch (IOException | InterruptedException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	private void removeDeadCenter(List<AgentCenter> centers){
		for(AgentCenter center : centers){
			registry.deleteCenter(center);
			manager.deleteOtherTypes(center.getAlias());
			Optional<Agent> deleteAgent =  manager.getRunningAgents().stream()
									  						  .filter(agent -> agent.getId().getHost().getAddress().equals(center.getAddress()))
									  						  .findFirst();
			if(deleteAgent.isPresent())
				manager.getRunningAgents().remove(deleteAgent.get());
		}
	}
}

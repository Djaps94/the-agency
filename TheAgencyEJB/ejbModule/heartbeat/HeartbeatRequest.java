package heartbeat;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
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

import beans.AgencyManagerLocal;
import beans.AgencyRegistryLocal;
import beans.SocketSenderLocal;
import consumers.HeartbeatConsumer;
import model.AID;
import model.AgentCenter;
import model.AgentType;
import util.SocketMessage;
import util.SocketMessage.messageType;


@Singleton
@LocalBean
public class HeartbeatRequest implements HeartbeatRequestLocal {

	@Resource
	private TimerService timer;
	
	@EJB
	private AgencyRegistryLocal registry;
	
	@EJB
	private AgencyManagerLocal manager;
	
	@EJB
	private SocketSenderLocal socketSender;
	
	private ConnectionFactory factory;
	private Connection connection;
	private Channel channel;
	private String RequestQueueName;
	private String ReplyQueueName;
	private BlockingQueue<String> response;
	
    public HeartbeatRequest() {

    }
    
    @PostConstruct
    private void initalise(){
    	
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
		timer.createIntervalTimer(1000*30, 1000*30, new TimerConfig("Heartbeat", false));
	}
	
	@Timeout
	private void sendPulse(Timer timer){
		List<AgentCenter> deadList = new ArrayList<>();
		registry.getCenters().forEach(center -> {
			String data = checkPulse(center);
			if(data == null){
				String temp = checkPulse(center);
				if(temp == null){
					deadList.add(center);
				}
			}
		});
			System.out.println("Heart tick...");
			if(!deadList.isEmpty())
				removeDeadCenter(deadList);
	}
	
	private String checkPulse(AgentCenter center){
		try {
			BasicProperties properties = new BasicProperties()
											 .builder()
											 .replyTo(ReplyQueueName)
											 .correlationId(center.getAlias())
											 .build();
			
			channel.basicPublish("", RequestQueueName+center.getAlias(), properties, "Check pulse".getBytes());
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
			Set<AgentType> types = manager.getOtherSupportedTypes().get(center.getAlias());
			manager.deleteOtherTypes(center.getAlias());
			
			SocketMessage m = new SocketMessage();
			m.setMsgType(messageType.REMOVE_TYPES);
			m.setAgentTypes(types);
			socketSender.socketSend(m);
			
			if(!manager.getCenterAgents().isEmpty()){
				List<AID> list = manager.getCenterAgents().get(center.getAlias());
				manager.getCenterAgents().remove(center.getAlias());
				
				SocketMessage message = new SocketMessage();
				message.setMsgType(messageType.REMOVE_AGENTS);
				message.setRunningAgents(list.iterator());
				socketSender.socketSend(message);
			}
		}
	}
}

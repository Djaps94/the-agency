package intercommunication;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.Singleton;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.client.AMQP.BasicProperties;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import beans.AgencyRegistryLocal;
import model.ACLMessage;
import model.AID;

@Singleton
public class RabbitDispatcher implements RabbitDispatcherLocal{
	
	@EJB
	private AgencyRegistryLocal registry;

	private ConnectionFactory factory;
	private Connection connection;
	private Channel channel;
	private ObjectMapper mapper;
	
	private final String QUEUE_NAME = ":Agent";
	
	public RabbitDispatcher() { }
	
	@PostConstruct
	private void initialise(){
    	try {
    		mapper = new ObjectMapper();
    		factory  = new ConnectionFactory();
        	factory.setHost("127.0.0.1");
        	factory.setPort(5672);
        	factory.setVirtualHost("/");
			connection = factory.newConnection();
			channel    = connection.createChannel();
		} catch (IOException | TimeoutException e) { }
	}
	
	
	public void notifyCenter(ACLMessage message, AID aid, String alias){
		try {
			InterCenterMessage msg = new InterCenterMessage(message, aid);
			String data = mapper.writeValueAsString(msg);
			BasicProperties props = new BasicProperties().builder().replyTo(alias+QUEUE_NAME).build();
			channel.basicPublish("", alias+QUEUE_NAME, props, data.getBytes());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}

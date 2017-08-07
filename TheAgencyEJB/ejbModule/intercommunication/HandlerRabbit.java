package intercommunication;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.Singleton;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import beans.AgencyRegistryLocal;

@Singleton
public class HandlerRabbit implements HandlerRabbitLocal{

	@EJB
	private AgencyRegistryLocal registry;
	
	@EJB
	private MessageDispatcherLocal dispatcher;
	
	private ConnectionFactory factory;
	private Connection connection;
	private Channel channel;
	
	private final String QUEUE_NAME = ":Agent";
	
	public HandlerRabbit(){
		
	}
	
	@PostConstruct
	private void initialise(){
    	factory = new ConnectionFactory();
		try {
        	factory.setHost("127.0.0.1");
        	factory.setPort(5672);
        	factory.setVirtualHost("/");
			connection = factory.newConnection();
			channel    = connection.createChannel();
		} catch (IOException | TimeoutException e) {
			e.printStackTrace();
		}
	}
	
	public void recieveMessage(){
		try {
			channel.queueDeclare(registry.getThisCenter().getAlias()+QUEUE_NAME, false, false, false, null);
			channel.basicQos(1);
			channel.basicConsume(registry.getThisCenter().getAlias()+QUEUE_NAME, false, new HandlerConsumer(channel, dispatcher));
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
}

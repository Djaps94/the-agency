package heartbeat;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.EJB;
import javax.ejb.Singleton;

import com.rabbitmq.client.AMQP.BasicProperties;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;

import beans.AgencyRegistryLocal;
import beans.NetworkManagmentLocal;


@Singleton
public class HeartbeatResponse implements HeartBeatResponseLocal{

	@EJB
	private AgencyRegistryLocal registry;
	
	@EJB
	private NetworkManagmentLocal centerManagment;
	
	private ConnectionFactory factory;
	private Connection connection;
	private Channel channel;
	private final String QUEUE_NAME = ":Heartbeat";

    public HeartbeatResponse() {
    	
    }
	
    @PostConstruct
    private void initialise(){
		factory = new ConnectionFactory();
		try {
			factory.setHost("127.0.0.1");
			factory.setPort(5672);
			factory.setVirtualHost("/");
			connection = factory.newConnection();
			channel = connection.createChannel();
		} catch (IOException | TimeoutException e) {
			e.printStackTrace();
		}
	}

	public void pulseTick() {
		try {
			channel.queueDeclare(registry.getThisCenter().getAlias()+QUEUE_NAME, false, false, false, null);
			channel.basicQos(1);
			channel.basicConsume(registry.getThisCenter().getAlias()+QUEUE_NAME, false, new DefaultConsumer(channel) {
				@Override
				public void handleDelivery(String consumerTag, Envelope envelope, BasicProperties properties, byte[] body) throws IOException {
					BasicProperties replyProperty = new BasicProperties().builder().correlationId(properties.getCorrelationId()).build();
					channel.basicPublish("", properties.getReplyTo(), replyProperty, "Alive".getBytes());
					channel.basicAck(envelope.getDeliveryTag(), false);
				}
			});

		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	@PreDestroy
	private void destroy() {
		try {
			channel.close();
			connection.close();
		} catch (IOException | TimeoutException e) {
			e.printStackTrace();
		}
	}

}

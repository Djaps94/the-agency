package heartbeat;

import java.io.IOException;
import java.net.URISyntaxException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
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
import com.rabbitmq.client.DefaultSaslConfig;
import com.rabbitmq.client.Envelope;
import com.rabbitmq.client.JDKSaslConfig;
import com.rabbitmq.client.SaslConfig;

import beans.AgencyRegistryLocal;
import beans.NetworkManagmentLocal;
import util.PortTransformation;


@Singleton
public class HeartbeatResponse implements HeartBeatResponseLocal{

	@EJB
	private AgencyRegistryLocal registry;
	
	@EJB
	private NetworkManagmentLocal centerManagment;
	
	private ConnectionFactory factory;
	private Connection connection;
	private Channel channel;
	private final String queueName = "Heartbeat";

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
			channel    = connection.createChannel();
		} catch (IOException | TimeoutException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    
    public void pulseTick(){
    	try {
			channel.queueDeclare(queueName, false, false, false, null);
			channel.basicQos(1);
			channel.basicConsume(queueName, false, new DefaultConsumer(channel){
				@Override
				public void handleDelivery(String consumerTag, Envelope envelope, BasicProperties properties, byte[] body) throws IOException {
					BasicProperties replyProperty = new BasicProperties()
													  .builder()
													  .correlationId(properties.getCorrelationId())
													  .build();
					channel.basicPublish("", properties.getReplyTo(), replyProperty, "Alive".getBytes());
					channel.basicAck(envelope.getDeliveryTag(), false);
				}
			});
			
			
		} catch (IOException e) {
			e.printStackTrace();
		}
    	
    }
    
    @PreDestroy
    private void destroy(){
    	try {
			channel.close();
			connection.close();
		} catch (IOException | TimeoutException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }

}

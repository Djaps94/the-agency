package intercommunication;

import java.io.IOException;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.client.AMQP.BasicProperties;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;

public class MediatorConsumer extends DefaultConsumer{
	
	private DispatcherLocal dispatcher;
	private Channel channel;

	public MediatorConsumer(Channel channel, DispatcherLocal dispatcher) {
		super(channel);
		this.dispatcher = dispatcher;
		this.channel = channel;
	}
	
	@Override
	public void handleDelivery(String consumerTag, Envelope envelope, BasicProperties properties, byte[] body){
		String data = new String(body);
		ObjectMapper mapper = new ObjectMapper();
		try {
			channel.basicAck(envelope.getDeliveryTag(), false);
			InterAgencyMessage message = (InterAgencyMessage) mapper.readValue(data, InterAgencyMessage.class);
			dispatcher.sendMesssage(message.getMessage(), message.getAid());
			channel.basicPublish("", properties.getReplyTo(), new BasicProperties().builder().build(), "Message delivered".getBytes());
		} catch (IOException e) {
			return;
		};
	}

}

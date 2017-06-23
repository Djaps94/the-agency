package intercommunication;

import java.io.IOException;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.client.AMQP.BasicProperties;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;

public class HandlerConsumer extends DefaultConsumer{
	
	private MessageDispatcherLocal dispatcher;
	private Channel channel;

	public HandlerConsumer(Channel channel, MessageDispatcherLocal dispatcher) {
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
			InterCenterMessage message = (InterCenterMessage) mapper.readValue(data, InterCenterMessage.class);
			dispatcher.sendMesssage(message.getMessage(), message.getAid());
			channel.basicPublish("", properties.getReplyTo(), new BasicProperties().builder().build(), "Message delivered".getBytes());
		} catch (IOException e) {
			return;
		};
	}

}

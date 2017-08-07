package consumers;

import java.io.IOException;
import java.util.concurrent.BlockingQueue;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.client.AMQP.BasicProperties;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;

import model.ServiceMessage;

public class HandshakeConsumer extends DefaultConsumer{

	private ObjectMapper mapper;
	private BlockingQueue<ServiceMessage> response;
	
	public HandshakeConsumer(Channel channel, ObjectMapper mapper, BlockingQueue<ServiceMessage> response) {
		super(channel);
		this.mapper   = mapper;
		this.response = response;
	}
	
	@Override
	public void handleDelivery(String consumerTag, Envelope envelope, BasicProperties properties, byte[] body) {
		String data = new String(body);
		ServiceMessage msg = null;
		try {
			msg = mapper.readValue(data, ServiceMessage.class);
		} catch (IOException e) {
			msg = new ServiceMessage();
		}
		response.offer(msg);
	}

}

package util;

import java.io.IOException;
import java.util.concurrent.BlockingQueue;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.client.AMQP.BasicProperties;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;

import model.HandshakeMessage;

public class HandshakeConsumer extends DefaultConsumer{

	private ObjectMapper mapper;
	private BlockingQueue<HandshakeMessage> response;
	
	public HandshakeConsumer(Channel channel, ObjectMapper mapper, BlockingQueue<HandshakeMessage> response) {
		super(channel);
		this.mapper   = mapper;
		this.response = response;
	}
	
	@Override
	public void handleDelivery(String consumerTag, Envelope envelope, BasicProperties properties, byte[] body) throws IOException {
		String data = new String(body);
		HandshakeMessage msg = null;
		msg = mapper.readValue(data, HandshakeMessage.class);
		response.offer(msg);
	}

}

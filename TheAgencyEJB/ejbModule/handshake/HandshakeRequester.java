package handshake;

import java.io.IOException;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import javax.ejb.Stateless;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.client.AMQP.BasicProperties;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import exceptions.ConnectionException;
import model.ServiceMessage;
import util.HandshakeConsumer;

@Stateless
public class HandshakeRequester implements HandshakeRequesterLocal {
	
	public HandshakeRequester() { }
		
	public ServiceMessage sendMessage(String destination, ServiceMessage message) throws ConnectionException, IOException, TimeoutException, InterruptedException{
		ConnectionFactory factory = new ConnectionFactory();
	  	factory.setHost("127.0.0.1");
	  	factory.setPort(5672);
	  	factory.setVirtualHost("/");
		Connection connection = factory.newConnection();
		Channel channel = connection.createChannel();
		String ReplyQueueName = channel.queueDeclare().getQueue();
		BlockingQueue<ServiceMessage> response = new ArrayBlockingQueue<>(1);
		ObjectMapper mapper = new ObjectMapper();
		String msg = mapper.writeValueAsString(message);
		BasicProperties props = new BasicProperties().builder().replyTo(ReplyQueueName).build();
	
		channel.basicPublish("", destination, props, msg.getBytes());
		channel.basicConsume(ReplyQueueName, true, new HandshakeConsumer(channel, mapper, response));
		return response.poll(5, TimeUnit.SECONDS);
	}
	
}

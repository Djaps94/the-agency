package service;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.Singleton;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import beans.AgencyRegistryLocal;
import beans.NetworkManagmentLocal;
import consumers.HandshakeConsumer;
import handshake.ResponseOperationsLocal;

@Singleton
public class MessageResponse implements MessageResponseLocal{	
	
	@EJB
	private AgencyRegistryLocal registry;
	
	@EJB
	private NetworkManagmentLocal nodesManagment;
	
	@EJB
	private MessageRequestLocal requester;
	
	@EJB
	private ResponseOperationsLocal operations;
	
	private ConnectionFactory factory;
	private Connection connection;
	private Channel channel;
	
	public MessageResponse() { }
	
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
	
	public void waitMessage() throws IOException{
		try {
			channel.queueDeclare(registry.getThisCenter().getAddress(), false, false, false, null);
			channel.basicQos(1);
		} catch (IOException e) {
			e.printStackTrace();
		}
		ObjectMapper mapper = new ObjectMapper();
		channel.basicConsume(registry.getThisCenter().getAddress(), new HandshakeConsumer(channel, mapper, nodesManagment, requester, operations));

	}
}
			

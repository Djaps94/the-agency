package consumers;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.io.JsonEOFException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.client.AMQP.BasicProperties;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;

import beans.NetworkManagmentLocal;
import exceptions.ConnectionException;
import exceptions.NodeExistsException;
import exceptions.RegisterSlaveException;
import handshake.ResponseOperationsLocal;
import model.ServiceMessage;
import model.ServiceMessage.handshakeType;
import service.MessageRequestLocal;

public class HandshakeResponseConsumer extends DefaultConsumer{

	private ObjectMapper mapper;
	private NetworkManagmentLocal nodesManagment;
	private MessageRequestLocal requester;
	private ResponseOperationsLocal operations;
	private Channel channel;
	
	public HandshakeResponseConsumer(Channel channel, ObjectMapper mapper, NetworkManagmentLocal nodesManagment, MessageRequestLocal requester, ResponseOperationsLocal operations) {
		super(channel);
		this.channel 		= channel;
		this.mapper 		= mapper;
		this.requester 		= requester;
		this.operations 	= operations;
		this.nodesManagment = nodesManagment;
	}
	
	@Override
	public void handleDelivery(String consumerTag, Envelope envelope, BasicProperties properties, byte[] body) throws IOException {
		channel.basicAck(envelope.getDeliveryTag(), false);
		String data = new String(body);
		ServiceMessage message = mapper.readValue(data, ServiceMessage.class);
		switch(message.getType()){
		case REGISTER: { 
			try {
				operations.sendRegisterResponse(message, channel, mapper, properties);
			} catch (RegisterSlaveException | ConnectionException | NodeExistsException | TimeoutException | InterruptedException e) {
				try {
					operations.sendRegisterResponse(message, channel, mapper, properties);
				} catch (RegisterSlaveException | ConnectionException | NodeExistsException | TimeoutException | InterruptedException e1) {
					try {
						message.setType(handshakeType.ROLLBACK);
						requester.sendMessage(nodesManagment.getMasterAddress(), message);
					} catch (ConnectionException | TimeoutException | InterruptedException e2) {
						//TODO: shutdown server
					}
				}
			}				 
		} 
		break;
		case GET_TYPES: {
			try {
				operations.sendGetTypesResponse(message, channel, mapper, properties);
			} catch (ConnectionException | JsonProcessingException | TimeoutException | InterruptedException e) {
				try {
					operations.sendGetTypesResponse(message, channel, mapper, properties);
				} catch (ConnectionException | JsonEOFException | TimeoutException | InterruptedException e1) {
					try {
						message.setType(handshakeType.ROLLBACK);
						requester.sendMessage(nodesManagment.getMasterAddress(), message);
					} catch (ConnectionException | TimeoutException | InterruptedException e2) {
						// TODO: shutdown server
					}
				}
			}
		}
		break;
		case DELIVER_TYPES: operations.addTypes(message, channel, properties); 
		break;
		case GET_RUNNING: {
			try {
				operations.sendGetRunningResponse(channel, mapper, properties);
			} catch(JsonProcessingException e){
				try {
					operations.sendGetRunningResponse(channel, mapper, properties);
				} catch(JsonProcessingException e1){
					try {
						message.setType(handshakeType.ROLLBACK);
						requester.sendMessage(nodesManagment.getMasterAddress(), message);
					} catch (ConnectionException | TimeoutException | InterruptedException e2) {
						// TODO shutdown server
					}
				}
			}
		}
		break;
		case 	 ROLLBACK: operations.rollback(message, channel, mapper, properties); 
		break;
		case DELETE_AGENT: operations.deleteRunningAgent(message, channel, properties);
		break;
		case 	ADD_AGENT: operations.addAgent(message, channel, properties);
		break;
		case 	RUN_AGENT: operations.runAgent(message, channel, properties); 
		break; 
		case   STOP_AGENT: operations.stopAgent(message, channel, properties);
		break;
		default:
		break;
		
		}
	}

}

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
import model.ServiceMessage;
import model.ServiceMessage.handshakeType;
import operations.AgencyOperationLocal;
import service.MessageRequestLocal;

public class AgencyOperationConsumer extends DefaultConsumer{

	private ObjectMapper mapper;
	private NetworkManagmentLocal nodesManagment;
	private MessageRequestLocal request;
	private AgencyOperationLocal operation;
	private Channel channel;
	
	public AgencyOperationConsumer(Channel channel, ObjectMapper mapper, NetworkManagmentLocal nodesManagment, MessageRequestLocal requester, AgencyOperationLocal operations) {
		super(channel);
		this.channel 		= channel;
		this.mapper 		= mapper;
		this.request 		= requester;
		this.operation 		= operations;
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
				operation.sendRegisterResponse(message, channel, mapper, properties);
			} catch (RegisterSlaveException | ConnectionException | NodeExistsException | TimeoutException | InterruptedException e) {
				try {
					operation.sendRegisterResponse(message, channel, mapper, properties);
				} catch (RegisterSlaveException | ConnectionException | NodeExistsException | TimeoutException | InterruptedException e1) {
					try {
						message.setType(handshakeType.ROLLBACK);
						request.sendMessage(nodesManagment.getMasterAddress(), message);
					} catch (ConnectionException | TimeoutException | InterruptedException e2) {
						//TODO: shutdown server
					}
				}
			}				 
		} 
		break;
		case GET_TYPES: {
			try {
				operation.sendGetTypesResponse(message, channel, mapper, properties);
			} catch (ConnectionException | JsonProcessingException | TimeoutException | InterruptedException e) {
				try {
					operation.sendGetTypesResponse(message, channel, mapper, properties);
				} catch (ConnectionException | JsonEOFException | TimeoutException | InterruptedException e1) {
					try {
						message.setType(handshakeType.ROLLBACK);
						request.sendMessage(nodesManagment.getMasterAddress(), message);
					} catch (ConnectionException | TimeoutException | InterruptedException e2) {
						// TODO: shutdown server
					}
				}
			}
		}
		break;
		case DELIVER_TYPES: operation.addTypes(message, channel, properties); 
		break;
		case GET_RUNNING: {
			try {
				operation.sendGetRunningResponse(channel, mapper, properties);
			} catch(JsonProcessingException e){
				try {
					operation.sendGetRunningResponse(channel, mapper, properties);
				} catch(JsonProcessingException e1){
					try {
						message.setType(handshakeType.ROLLBACK);
						request.sendMessage(nodesManagment.getMasterAddress(), message);
					} catch (ConnectionException | TimeoutException | InterruptedException e2) {
						// TODO shutdown server
					}
				}
			}
		}
		break;
		case 	 ROLLBACK: operation.rollback(message, channel, mapper, properties); 
		break;
		case DELETE_AGENT: operation.deleteRunningAgent(message, channel, properties);
		break;
		case 	ADD_AGENT: operation.addAgent(message, channel, properties);
		break;
		case 	RUN_AGENT: operation.runAgent(message, channel, properties); 
		break; 
		case   STOP_AGENT: operation.stopAgent(message, channel, properties);
		break;
		default:
		break;
		
		}
	}

}

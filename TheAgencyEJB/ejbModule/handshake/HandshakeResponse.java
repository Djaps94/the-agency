package handshake;

import java.io.IOException;

import javax.ejb.ActivationConfigProperty;
import javax.ejb.Asynchronous;
import javax.ejb.EJB;
import javax.ejb.MessageDriven;
import javax.jms.Message;
import javax.jms.MessageListener;

import org.zeromq.ZMQ;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.io.JsonEOFException;
import com.fasterxml.jackson.databind.ObjectMapper;

import beans.AgencyRegistryLocal;
import beans.NetworkManagmentLocal;
import exceptions.ConnectionException;
import exceptions.RegisterSlaveException;
import model.HandshakeMessage;
import model.HandshakeMessage.handshakeType;
import util.PortTransformation;

@MessageDriven(activationConfig ={
		@ActivationConfigProperty(propertyName = "destinationType", propertyValue = "javax.jms.Queue"),
		@ActivationConfigProperty(propertyName = "destination", propertyValue = "java:/jms/queue/ZeroMQ"),
		@ActivationConfigProperty(propertyName = "transactionTimeout", propertyValue = "300000")
})
public class HandshakeResponse implements MessageListener{	
	
	@EJB
	private AgencyRegistryLocal registry;
	
	@EJB
	private NetworkManagmentLocal nodesManagment;
	
	@EJB
	private HandshakeRequesterLocal requester;
	
	@EJB
	private ResponseOperationsLocal operations;
		
	
	public HandshakeResponse() { }
	
	@Override
	public void onMessage(Message message) {
		if(!nodesManagment.isRecieverRunning())
			waitMessage();
	}
	
	@Asynchronous
	public void waitMessage(){
		nodesManagment.setRecieverRunning(true);
		ObjectMapper mapper = new ObjectMapper();
		ZMQ.Context context = ZMQ.context(1);
		ZMQ.Socket response = context.socket(ZMQ.REP);
		response.bind("tcp://"+PortTransformation.transform(registry.getThisCenter().getAddress(),0));
		
			while(!Thread.currentThread().isInterrupted()){
				try {
					String data = response.recvStr();
					HandshakeMessage message = mapper.readValue(data, HandshakeMessage.class);
					switch(message.getType()){
					case REGISTER: { 
						try {
							operations.sendRegisterResponse(message, response, mapper);
						} catch (RegisterSlaveException | ConnectionException e) {
							try {
								operations.sendRegisterResponse(message, response, mapper);
							} catch (RegisterSlaveException | ConnectionException e1) {
								try {
									message.setType(handshakeType.ROLLBACK);
									requester.sendMessage(nodesManagment.getMasterAddress(), message);
								} catch (ConnectionException e2) {
									//TODO: shutdown server
								}
							}
						}				 
					} 
					break;
					case GET_TYPES: {
						try {
							operations.sendGetTypesResponse(message, response, mapper);
						} catch (ConnectionException | JsonProcessingException e) {
							try {
								operations.sendGetTypesResponse(message, response, mapper);
							} catch (ConnectionException | JsonEOFException e1) {
								try {
									message.setType(handshakeType.ROLLBACK);
									requester.sendMessage(nodesManagment.getMasterAddress(), message);
								} catch (ConnectionException e2) {
									// TODO: shutdown server
								}
							}
						}
					}
					break;
					case DELIVER_TYPES: operations.addTypes(message, response); 
					break;
					case GET_RUNNING: {
						try {
							operations.sendGetRunningResponse(response, mapper);
						} catch(JsonProcessingException e){
							try {
								operations.sendGetRunningResponse(response, mapper);
							} catch(JsonProcessingException e1){
								try {
									message.setType(handshakeType.ROLLBACK);
									requester.sendMessage(nodesManagment.getMasterAddress(), message);
								} catch (ConnectionException e2) {
									// TODO shutdown server
								}
							}
						}
					}
					break;
					case ROLLBACK: operations.rollback(message, response, mapper); 
					break;
					default:
						break;
					
					}
				} catch (IOException e) {
					continue;
				}
			}
			response.close();
			context.term();
	}
}
			

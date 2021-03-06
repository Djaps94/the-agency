package beans;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.concurrent.TimeoutException;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.Local;
import javax.ejb.Singleton;
import javax.ejb.Startup;

import exceptions.ConnectionException;
import exceptions.NodeExistsException;
import heartbeat.HeartBeatResponseLocal;
import heartbeat.HeartbeatRequestLocal;
import intercommunication.MediatorLocal;
import model.AgentCenter;
import model.ServiceMessage;
import model.ServiceMessage.OperationType;
import service.MessageRequestLocal;
import service.MessageResponseLocal;
import util.AgencyUtil;

@Startup
@Singleton
@Local(NetworkManagmentLocal.class)
public class NetworkManagment implements NetworkManagmentLocal{
	
	private final int PORT = 8080;
	private final String MASTER = "master";
	private final String ALIAS  = "alias";
	private final String OFFSET = "jboss.socket.binding.port-offset";
	private final String TYPES  = "types";
	private final String LOCAL  = "local";
	
	private boolean master;
	private String masterIpAddress;																																																																																																																																																																																
	
	@EJB
	private AgencyRegistryLocal registry;
	
	@EJB
	private MessageRequestLocal request;
		
	@EJB 
	private AgencyManagerLocal agency;
	
	@EJB
	private HeartbeatRequestLocal beatRequest;
	
	@EJB
	private HeartBeatResponseLocal beatResponse;
	
	@EJB
	private MessageResponseLocal messageResponse;
	
	@EJB
	private MediatorLocal rabbitHandler;
	
	@EJB
	private AgentRegistry agentRegistry;
	
	@PostConstruct
	public void initialise(){		
		if(System.getProperty(MASTER) == null){
			master = true;
			try {
				registry.setThisCenter(createCenter());
				beatRequest.startTimer();
				beatResponse.pulseTick();
				messageResponse.waitMessage();
				rabbitHandler.recieveAgentMessage();
				System.out.println("MASTER NODE UP");
			} catch (IOException e) {
				//TODO: shutdown script
			}
			return;
		}
		masterIpAddress   = System.getProperty(MASTER);
		AgentCenter slave = null;
		try {
			slave = createCenter();
			System.out.println("SLAVE NODE UP "+slave.getAlias());
			registry.setThisCenter(slave);
			messageResponse.waitMessage();
			rabbitHandler.recieveAgentMessage();
			master = false;
			try {
				ServiceMessage message = sendMessageToMaster(masterIpAddress, slave);
				if(message != null){
					for(AgentCenter center : message.getCenters())
						registry.addCenter(center);
				}
			} catch (ConnectionException | NodeExistsException | IOException | TimeoutException | InterruptedException e) {
				try {
					ServiceMessage message = sendMessageToMaster(masterIpAddress, slave);
					if(message != null) {
						for(AgentCenter center : message.getCenters())
							registry.addCenter(center);
					}
				} catch (ConnectionException | NodeExistsException | IOException | TimeoutException | InterruptedException e1) {
					try {
						ServiceMessage message = rollback(masterIpAddress, slave);
						if(message != null) shutdownServer();
					} catch (ConnectionException | IOException | TimeoutException | InterruptedException e2) {
						System.out.println("Handshake failed. Rollback failed. Shuting down server...");
						shutdownServer();
					}
				}
			}
			
			try {
				ServiceMessage message = getAllAgentTypes(masterIpAddress, slave);
				if(message != null) message.getOtherTypes().entrySet().stream().forEach(entry -> agency.addOtherTypes(entry.getKey(), entry.getValue()));
			} catch (ConnectionException | IOException | TimeoutException | InterruptedException e) {
				try {
					ServiceMessage message = getAllAgentTypes(masterIpAddress, slave);
					if(message != null) message.getOtherTypes().entrySet().stream().forEach(entry -> agency.addOtherTypes(entry.getKey(), entry.getValue()));
				} catch (ConnectionException | IOException | TimeoutException | InterruptedException e1) {
					try {
						ServiceMessage message = rollback(masterIpAddress, slave);
						if(message != null) shutdownServer();
					} catch (ConnectionException | IOException | TimeoutException | InterruptedException e2) {
						System.out.println("Handshake failed. Rollback failed. Shuting down server...");
						shutdownServer();
					}
				}
			}
			try {
				ServiceMessage message = getAllRunningAgents(masterIpAddress, slave);
				if(message != null) agency.addCenterAgents(message.getOtherAgents().entrySet().iterator());
			} catch (ConnectionException | IOException | TimeoutException | InterruptedException e) {
				try {
					ServiceMessage message = getAllRunningAgents(masterIpAddress, slave);
					if(message != null) agency.addCenterAgents(message.getOtherAgents().entrySet().iterator());									
				} catch (ConnectionException | IOException | TimeoutException | InterruptedException e1) {
					try {
						ServiceMessage message = rollback(masterIpAddress, slave);
						if(message != null) shutdownServer();
					} catch (ConnectionException | IOException | TimeoutException | InterruptedException e2) {
						System.out.println("Handshake failed. Rollback failed. Shuting down server...");
						shutdownServer();
					}
				}
			}
			beatRequest.startTimer();
			beatResponse.pulseTick();
		} catch (IOException e) {
			shutdownServer();
		}
		
		
		
	}
	
	private AgentCenter createCenter() throws UnknownHostException{
		String ipAddress = System.getProperty(LOCAL) == null ? "127.0.0.1" : System.getProperty(LOCAL);
		String alias     = System.getProperty(ALIAS) == null ? InetAddress.getLocalHost().getHostName() : System.getProperty(ALIAS);
		int offset 		 = System.getProperty(OFFSET) == null ? 0 : Integer.parseInt(System.getProperty(OFFSET));
		String filename  = System.getProperty(TYPES) == null ? "/default" : System.getProperty(TYPES);
		AgentCenter center = new AgentCenter(alias, ipAddress+":"+(PORT+offset));
		agency.setSupportedTypes(AgencyUtil.agentTypes(filename));
		return center;
	}
		
	private ServiceMessage sendMessageToMaster(String masterIpAddress, AgentCenter slave) throws ConnectionException, IOException, TimeoutException, InterruptedException{
		return request.sendMessage(masterIpAddress, new ServiceMessage(slave, OperationType.REGISTER));
	}
	
	private ServiceMessage getAllAgentTypes(String masterIpAddress, AgentCenter slave) throws ConnectionException, IOException, TimeoutException, InterruptedException{
		return request.sendMessage(masterIpAddress, createMessage(slave, OperationType.GET_TYPES));
	}
	
	private ServiceMessage getAllRunningAgents(String masterIpAddress, AgentCenter slave) throws ConnectionException, IOException, TimeoutException, InterruptedException{
		return request.sendMessage(masterIpAddress, createMessage(slave, OperationType.GET_RUNNING));
	}

	private ServiceMessage rollback(String masterIpAddress, AgentCenter slave) throws ConnectionException, IOException, TimeoutException, InterruptedException{
		return request.sendMessage(masterIpAddress, createMessage(slave, OperationType.ROLLBACK));
	}
	
	private ServiceMessage createMessage(AgentCenter slave, OperationType type){
		ServiceMessage message = new ServiceMessage();
		message.setType(type);
		message.setTypesViaIter(agency.getSupportedTypes());
		message.setCenter(slave);
		message.setAIDsViaIter(agentRegistry.getRunningAID());
		return message;
	}
		
	private void shutdownServer(){
		//TODO: Load script and exec it
	}
		
	public boolean isMaster(){
		return master;
	}
	
	public String getMasterAddress(){
		return masterIpAddress;
	}
}

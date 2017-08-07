package beans;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.concurrent.TimeoutException;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.EJB;
import javax.ejb.Local;
import javax.ejb.Singleton;
import javax.ejb.Startup;

import exceptions.ConnectionException;
import exceptions.NodeExistsException;
import heartbeat.HeartBeatResponseLocal;
import heartbeat.HeartbeatRequestLocal;
import intercommunication.HandlerRabbitLocal;
import model.AgentCenter;
import model.ServiceMessage;
import model.ServiceMessage.handshakeType;
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
	private AgencyRegistryLocal registryBean;
	
	@EJB
	private MessageRequestLocal requester;
		
	@EJB 
	private AgencyManagerLocal agency;
	
	@EJB
	private HeartbeatRequestLocal beatRequester;
	
	@EJB
	private HeartBeatResponseLocal beatResponse;
	
	@EJB
	private MessageResponseLocal handshakeResponse;
	
	@EJB
	private HandlerRabbitLocal rabbitHandler;
	
	@PostConstruct
	public void initialise(){		
		if(System.getProperty(MASTER) == null){
			master = true;
			try {
				registryBean.setThisCenter(createCenter());
				System.out.println("MASTER NODE UP");
				beatRequester.startTimer();
				beatResponse.pulseTick();
				handshakeResponse.waitMessage();
				rabbitHandler.recieveMessage();
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
			registryBean.setThisCenter(slave);
			master = false;
			handshakeResponse.waitMessage();
			rabbitHandler.recieveMessage();
			try {
				ServiceMessage message = sendMessageToMaster(masterIpAddress, slave);
				if(message != null){
					for(AgentCenter center : message.getCenters())
						registryBean.addCenter(center);
				}
			} catch (ConnectionException | NodeExistsException | IOException | TimeoutException | InterruptedException e) {
				try {
					ServiceMessage message = sendMessageToMaster(masterIpAddress, slave);
					if(message != null) {
						for(AgentCenter center : message.getCenters())
							registryBean.addCenter(center);
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
				if(message != null) message.getOtherTypes().entrySet()
														   .stream()
														   .forEach(entry -> agency.addOtherTypes(entry.getKey(), entry.getValue()));
			} catch (ConnectionException | IOException | TimeoutException | InterruptedException e) {
				try {
					ServiceMessage message = getAllAgentTypes(masterIpAddress, slave);
					if(message != null) message.getOtherTypes().entrySet()
															   .stream()
															   .forEach(entry -> agency.addOtherTypes(entry.getKey(), entry.getValue()));
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
				if(message != null) agency.getCenterAgents().putAll(message.getOtherAgents());
			} catch (ConnectionException | IOException | TimeoutException | InterruptedException e) {
				try {
					ServiceMessage message = getAllRunningAgents(masterIpAddress, slave);
					if(message != null) agency.getCenterAgents().putAll(message.getOtherAgents());									
				} catch (ConnectionException | IOException | TimeoutException | InterruptedException e1) {
					try {
						ServiceMessage message = rollback(masterIpAddress, slave);
						if(message != null) shutdownServer();
					} catch (ConnectionException | IOException | TimeoutException | InterruptedException e2) {
						System.out.println("Handshake failed. Rollback failed. Shuting down serve...");
						shutdownServer();
					}
				}
			}
			beatRequester.startTimer();
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
		return requester.sendMessage(masterIpAddress, new ServiceMessage(slave, handshakeType.REGISTER));
	}
	
	private ServiceMessage getAllAgentTypes(String masterIpAddress, AgentCenter slave) throws ConnectionException, IOException, TimeoutException, InterruptedException{
		return requester.sendMessage(masterIpAddress, createMessage(slave, handshakeType.GET_TYPES));
	}
	
	private ServiceMessage getAllRunningAgents(String masterIpAddress, AgentCenter slave) throws ConnectionException, IOException, TimeoutException, InterruptedException{
		return requester.sendMessage(masterIpAddress, createMessage(slave, handshakeType.GET_RUNNING));
	}

	private ServiceMessage rollback(String masterIpAddress, AgentCenter slave) throws ConnectionException, IOException, TimeoutException, InterruptedException{
		return requester.sendMessage(masterIpAddress, createMessage(slave, handshakeType.ROLLBACK));
	}
	
	private ServiceMessage createMessage(AgentCenter slave, handshakeType type){
		ServiceMessage message = new ServiceMessage();
		message.setType(type);
		message.setAgentTypes(agency.getSupportedTypes());
		message.setCenter(slave);
		message.setRunningAgents(agency.getRunningAgents());
		return message;
	}
	
	@PreDestroy
	private void destroy(){
		
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

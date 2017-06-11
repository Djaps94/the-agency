package beans;

import java.net.InetAddress;
import java.net.UnknownHostException;

import javax.annotation.PostConstruct;
import javax.ejb.ConcurrencyManagement;
import javax.ejb.ConcurrencyManagementType;
import javax.ejb.EJB;
import javax.ejb.Local;
import javax.ejb.Singleton;
import javax.ejb.Startup;

import exceptions.ConnectionException;
import handshake.HandshakeRequesterLocal;
import handshake.HandshakeResponseLocal;
import model.AgentCenter;
import model.HandshakeMessage;
import model.HandshakeMessage.handshakeType;

@Startup
@Singleton
@ConcurrencyManagement(ConcurrencyManagementType.CONTAINER)
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
	private CenterRegistryLocal registryBean;
	
	@EJB
	private HandshakeRequesterLocal requester;
	
	@EJB
	private HandshakeResponseLocal timer;
	
	@PostConstruct
	public void initialise(){		
		if(System.getProperty(MASTER) == null){
			master = true;
			try {
				registryBean.setThisCenter(createCenter());
				System.out.println("MASTER NODE UP");
				timer.startTimer();
			} catch (UnknownHostException e) {
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
			timer.startTimer();
			try {
				HandshakeMessage message = sendMessageToMaster(masterIpAddress, slave);
				if(message != null)
					message.getCenters().forEach(center -> registryBean.addCenter(center));
			} catch (ConnectionException e) {
				try {
					HandshakeMessage message = sendMessageToMaster(masterIpAddress, slave);
					if(message != null)
						message.getCenters().forEach(center -> registryBean.addCenter(center));
				} catch (ConnectionException e1) {
					// TODO: rollback!
				}
			}
		} catch (UnknownHostException e) {
			//TODO: shutdown script
		}
		
		
		
	}
	
	private AgentCenter createCenter() throws UnknownHostException{
		String ipAddress = System.getProperty(LOCAL) == null ? "127.0.0.1" : System.getProperty(LOCAL);
		String alias     = System.getProperty(ALIAS) == null ? InetAddress.getLocalHost().getHostName() : System.getProperty(ALIAS);
		int offset 		 = System.getProperty(OFFSET) == null ? 0 : Integer.parseInt(System.getProperty(OFFSET));
		String filename  = System.getProperty(TYPES) == null ? "" : System.getProperty(TYPES);
		
		AgentCenter center = new AgentCenter(alias, ipAddress+":"+(PORT+offset));
		//TODO: Set AgentTypes for agent center
		return center;
	}
		
	private HandshakeMessage sendMessageToMaster(String masterIpAddress, AgentCenter slave) throws ConnectionException{
		return requester.sendMessage(masterIpAddress, new HandshakeMessage(slave, handshakeType.REGISTER));
	}
	
	public boolean isMaster(){
		return master;
	}
}

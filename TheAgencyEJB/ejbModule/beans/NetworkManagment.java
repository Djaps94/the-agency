package beans;

import javax.annotation.PostConstruct;
import javax.ejb.ConcurrencyManagement;
import javax.ejb.ConcurrencyManagementType;
import javax.ejb.Local;
import javax.ejb.Singleton;
import javax.ejb.Startup;

@Startup
@Singleton
@ConcurrencyManagement(ConcurrencyManagementType.CONTAINER)
@Local(NetworkManagmentLocal.class)
public class NetworkManagment implements NetworkManagmentLocal{
	
	private final int PORT = 8080;
	private final String MASTER = "master";
	private final String ALIAS  = "alias";
	private final String OFFSET = "jboss.socket.binding.port-offset";
	
	private boolean master;
	private String masterIpAddress;
	
	@PostConstruct
	public void initialise(){
		
	}
}

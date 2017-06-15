package beans;

import java.util.Map;
import java.util.Set;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import model.AgentCenter;
import model.AgentType;

@Stateless
@Path("/test")
public class Test {

	@EJB
	private AgencyRegistryLocal registry;
	
	@EJB
	private AgencyManagerLocal manager;
	
	@Path("/centers")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Map<String, Set<AgentType>> test(){
		return manager.getOtherSupportedTypes();
	}
}

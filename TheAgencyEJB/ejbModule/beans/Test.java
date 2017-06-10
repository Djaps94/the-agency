package beans;

import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import model.AgentCenter;

@Stateless
@Path("/test")
public class Test {

	@EJB
	private CenterRegistryLocal registry;
	
	@Path("/centers")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public List<AgentCenter> test(){
		return registry.getCenters();
	}
}

package beans;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.Singleton;

import exceptions.NodeExistsException;
import model.AgentCenter;

@Singleton
public class AgencyRegistry implements AgencyRegistryLocal{

	private AgentCenter thisCenter;
	private List<AgentCenter> registeredCenters;
	
	public AgencyRegistry() { }
	
	@PostConstruct
	private void initialise(){
		this.registeredCenters = new ArrayList<AgentCenter>();
	}
	 
	@Override
	public void addCenter(AgentCenter center) throws NodeExistsException {
		if(registeredCenters.stream().anyMatch(c -> c.getAlias().equals(center.getAlias())))
			throw new NodeExistsException("Center already running");
		
		registeredCenters.add(center);		
	}
	
	@Override
	public void deleteCenter(AgentCenter center) {
		registeredCenters.remove(center);
		
	}
	
	@Override
	public List<AgentCenter> getCenters() {
		return registeredCenters;
	}

	@Override
	public void setThisCenter(AgentCenter center) {
		thisCenter = center;
	}

	@Override
	public AgentCenter getThisCenter() {
		return thisCenter;
	}
}

package beans;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;
import javax.ejb.AccessTimeout;
import javax.ejb.Singleton;

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
	@AccessTimeout(value = 1, unit = TimeUnit.MINUTES)
	public void addCenter(AgentCenter center) {
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

package beans;

import java.util.List;

import javax.ejb.Singleton;

import model.AgentCenter;

@Singleton
public class CenterRegistry implements CenterRegistryLocal{

	private AgentCenter thisCenter;
	private List<AgentCenter> registeredCenters;
	
	public CenterRegistry() { }
	 
	@Override
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

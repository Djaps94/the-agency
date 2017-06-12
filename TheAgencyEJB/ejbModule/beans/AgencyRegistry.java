package beans;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.ConcurrencyManagement;
import javax.ejb.ConcurrencyManagementType;
import javax.ejb.Lock;
import javax.ejb.LockType;
import javax.ejb.Singleton;

import model.AgentCenter;

@Singleton
@ConcurrencyManagement(ConcurrencyManagementType.CONTAINER)
public class AgencyRegistry implements AgencyRegistryLocal{

	private AgentCenter thisCenter;
	private List<AgentCenter> registeredCenters;
	
	public AgencyRegistry() { }
	
	@PostConstruct
	private void initialise(){
		this.registeredCenters = new ArrayList<AgentCenter>();
	}
	 
	@Override
	@Lock(LockType.WRITE)
	public void addCenter(AgentCenter center) {
		registeredCenters.add(center);		
	}
	
	@Override
	@Lock(LockType.WRITE)
	public void deleteCenter(AgentCenter center) {
		registeredCenters.remove(center);
		
	}
	
	@Override
	@Lock(LockType.READ)
	public List<AgentCenter> getCenters() {
		return registeredCenters;
	}

	@Override
	@Lock(LockType.WRITE)
	public void setThisCenter(AgentCenter center) {
		thisCenter = center;
	}

	@Override
	@Lock(LockType.READ)
	public AgentCenter getThisCenter() {
		return thisCenter;
	}
}

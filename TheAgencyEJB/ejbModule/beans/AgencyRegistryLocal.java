package beans;

import java.util.List;

import javax.ejb.Local;

import model.AgentCenter;

@Local
public interface AgencyRegistryLocal {
	
	public void addCenter(AgentCenter center);
	public void deleteCenter(AgentCenter center);
	public List<AgentCenter> getCenters();
	public void setThisCenter(AgentCenter center);
	public AgentCenter getThisCenter();

}

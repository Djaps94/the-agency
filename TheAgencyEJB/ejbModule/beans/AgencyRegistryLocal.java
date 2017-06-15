package beans;

import java.util.List;

import javax.ejb.Local;

import exceptions.NodeExistsException;
import model.AgentCenter;

@Local
public interface AgencyRegistryLocal {
	
	public void addCenter(AgentCenter center) throws NodeExistsException;
	public void deleteCenter(AgentCenter center);
	public List<AgentCenter> getCenters();
	public void setThisCenter(AgentCenter center);
	public AgentCenter getThisCenter();

}

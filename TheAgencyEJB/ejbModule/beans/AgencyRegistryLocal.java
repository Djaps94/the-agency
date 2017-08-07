package beans;

import java.util.Iterator;
import java.util.stream.Stream;

import javax.ejb.Local;

import exceptions.NodeExistsException;
import model.AgentCenter;

@Local
public interface AgencyRegistryLocal {
	
	public void addCenter(AgentCenter center) throws NodeExistsException;
	public void deleteCenter(AgentCenter center);
	public Stream<AgentCenter> getCenters();
	public Iterator<AgentCenter> getCentersIterator();
	public void setThisCenter(AgentCenter center);
	public AgentCenter getThisCenter();

}

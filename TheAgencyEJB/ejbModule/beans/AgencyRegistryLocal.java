package beans;

import java.util.Iterator;
import java.util.stream.Stream;

import javax.ejb.Local;

import exceptions.NodeExistsException;
import model.AgentCenter;

@Local
public interface AgencyRegistryLocal {
	
	void addCenter(AgentCenter center) throws NodeExistsException;
	void deleteCenter(AgentCenter center);
	Stream<AgentCenter> getCenters();
	Iterator<AgentCenter> getCentersIterator();
	void setThisCenter(AgentCenter center);
	AgentCenter getThisCenter();

}

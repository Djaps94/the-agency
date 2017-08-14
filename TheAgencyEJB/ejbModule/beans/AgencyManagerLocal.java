package beans;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Stream;

import javax.ejb.Local;

import model.AID;
import model.AgentType;

@Local
public interface AgencyManagerLocal {

	Iterator<AgentType> getSupportedTypes();
	Stream<AgentType> getSupportedTypesStream();
	void setSupportedTypes(Set<AgentType> supportedTypes);
	Iterator<Entry<String, Set<AgentType>>> getOtherSupportedTypes();
	Set<AgentType> getOtherAgentTypes(String key);
	Stream<Entry<String, Set<AgentType>>> getOtherSupportedTypesStream();
	boolean isSupportedContained(AgentType t);
	void deleteOtherTypes(String typeName);
	void addOtherTypes(String typeName, Set<AgentType> types);
	boolean isContained(String typeName);
	Iterator<Entry<String, List<AID>>> getCenterAgents();
	void addCenterAgents(Iterator<Entry<String, List<AID>>> iter);
	List<AID> getCenterAgent(String key);
	boolean isAgentContained(String key);
	void removeAgent(String key);
	void addCenterAgent(String key, List<AID> aids);
	void setCenterAgents(Map<String, List<AID>> centerAgents);

}

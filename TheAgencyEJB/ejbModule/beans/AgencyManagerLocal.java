package beans;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.stream.Stream;

import javax.ejb.Local;

import model.ACLMessage.Performative;
import model.AID;
import model.Agent;
import model.AgentType;

@Local
public interface AgencyManagerLocal {

	public Iterator<AgentType> getSupportedTypes();
	public Stream<AgentType> getSupportedTypesStream();
	public void setSupportedTypes(Set<AgentType> supportedTypes);
	public Iterator<Entry<String, Set<AgentType>>> getOtherSupportedTypes();
	public Set<AgentType> getOtherAgentTypes(String key);
	public Stream<Entry<String, Set<AgentType>>> getOtherSupportedTypesStream();
	public boolean isSupportedContained(AgentType t);
	public void deleteOtherTypes(String typeName);
	public void addOtherTypes(String typeName, Set<AgentType> types);
	public boolean isContained(String typeName);
	public Iterator<Entry<String, List<AID>>> getCenterAgents();
	public void addCenterAgents(Iterator<Entry<String, List<AID>>> iter);
	public List<AID> getCenterAgent(String key);
	public boolean isAgentContained(String key);
	public void removeAgent(String key);
	public void addCenterAgent(String key, List<AID> aids);
	public void setCenterAgents(Map<String, List<AID>> centerAgents);

}

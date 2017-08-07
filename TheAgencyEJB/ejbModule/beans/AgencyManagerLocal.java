package beans;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
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
	public Iterator<AID> getRunningAgents();
	public Stream<AID> getRunningAgentsStream();
	public void addRunningAgent(AID aid);
	public void removeRunningAgent(AID aid);
	public void setRunningAgents(List<AID> runningAgents);
	public void removeAllRunningAgents(Iterator<AID> agents);
	public Map<String, Set<AgentType>> getOtherSupportedTypes();
	public void setOtherSupportedTypes(Map<String, Set<AgentType>> otherSupportedTypes);
	public boolean isSupportedContained(AgentType t);
	public void deleteOtherTypes(String typeName);
	public void addOtherTypes(String typeName, Set<AgentType> types);
	public boolean isContained(String typeName);
	public Performative getPerformative();
	public Map<String, List<AID>> getCenterAgents();
	public void setCenterAgents(Map<String, List<AID>> centerAgents);
	public Iterator<Agent> getStartedAgents();
	public void addStartedAgent(Agent agent);
	public void removeStartedAgent(Agent agent);
}

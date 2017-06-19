package beans;

import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.ejb.Local;

import model.ACLMessage.Performative;
import model.AID;
import model.AgentType;

@Local
public interface AgencyManagerLocal {

	public Set<AgentType> getSupportedTypes();
	public void setSupportedTypes(Set<AgentType> supportedTypes);
	public List<AID> getRunningAgents();
	public void setRunningAgents(List<AID> runningAgents);
	public Map<String, Set<AgentType>> getOtherSupportedTypes();
	public void setOtherSupportedTypes(Map<String, Set<AgentType>> otherSupportedTypes);
	public void deleteOtherTypes(String typeName);
	public void addOtherTypes(String typeName, Set<AgentType> types);
	public boolean isContained(String typeName);
	public Performative getPerformative();
	public Map<String, List<AID>> getCenterAgents();
	public void setCenterAgents(Map<String, List<AID>> centerAgents);
}

package beans;

import java.util.List;
import java.util.Set;

import javax.ejb.Local;

import model.Agent;
import model.AgentType;

@Local
public interface AgencyManagerLocal {

	public Set<AgentType> getSupportedTypes();
	public void setSupportedTypes(Set<AgentType> supportedTypes);
	public List<Agent> getRunningAgents();
	public void setRunningAgents(List<Agent> runningAgents);
}

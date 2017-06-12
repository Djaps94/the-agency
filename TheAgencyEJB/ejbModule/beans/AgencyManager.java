package beans;

import java.util.List;
import java.util.Set;

import javax.ejb.Singleton;

import model.Agent;
import model.AgentType;

@Singleton
public class AgencyManager implements AgencyManagerLocal {

	private List<Agent> runningAgents;
	private Set<AgentType> supportedTypes; 
	private Set<AgentType> otherSupportedTypes;
	
	public AgencyManager() { }
	
	public Set<AgentType> getSupportedTypes() {
		return supportedTypes;
	}

	public void setSupportedTypes(Set<AgentType> supportedTypes) {
		this.supportedTypes = supportedTypes;
	}

	public List<Agent> getRunningAgents() {
		return runningAgents;
	}

	public void setRunningAgents(List<Agent> runningAgents) {
		this.runningAgents = runningAgents;
	}

	public Set<AgentType> getOtherSupportedTypes() {
		return otherSupportedTypes;
	}

	public void setOtherSupportedTypes(Set<AgentType> otherSupportedTypes) {
		this.otherSupportedTypes = otherSupportedTypes;
	}
}

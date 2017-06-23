package beans;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.ejb.Singleton;

import model.ACLMessage.Performative;
import model.AID;
import model.Agent;
import model.AgentType;

@Singleton
public class AgencyManager implements AgencyManagerLocal {

	private List<AID> runningAgents;
	private Map<String, List<AID>> centerAgents;
	private Set<AgentType> supportedTypes; 
	private Map<String,Set<AgentType>> otherSupportedTypes;
	private List<Agent> startedAgents;
	private Performative performative;
	
	public AgencyManager() { }
	
	@PostConstruct
	public void initialise(){
		this.runningAgents  = new ArrayList<AID>();
		this.supportedTypes = new HashSet<AgentType>();
		this.otherSupportedTypes = new HashMap<String, Set<AgentType>>();
		this.centerAgents = new HashMap<String, List<AID>>();
		startedAgents = new ArrayList<Agent>();
	}
	
	public Performative getPerformative(){
		return performative;
	}
	
	public Set<AgentType> getSupportedTypes() {
		return supportedTypes;
	}

	public void setSupportedTypes(Set<AgentType> supportedTypes) {
		this.supportedTypes = supportedTypes;
	}

	public List<AID> getRunningAgents() {
		return runningAgents;
	}

	public void setRunningAgents(List<AID> runningAgents) {
		this.runningAgents = runningAgents;
	}

	public Map<String, Set<AgentType>> getOtherSupportedTypes() {
		return otherSupportedTypes;
	}

	public void setOtherSupportedTypes(Map<String, Set<AgentType>> otherSupportedTypes) {
		this.otherSupportedTypes = otherSupportedTypes;
	}
	
	public boolean isContained(String alias){
		return otherSupportedTypes.containsKey(alias);
	}
	
	public void addOtherTypes(String alias, Set<AgentType> types){
		if(!isContained(alias))
			otherSupportedTypes.put(alias, types);
		else
			otherSupportedTypes.get(alias).addAll(types);
	}
	
	public void deleteOtherTypes(String alias){
		otherSupportedTypes.remove(alias);
	}

	public Map<String, List<AID>> getCenterAgents() {
		return centerAgents;
	}

	public void setCenterAgents(Map<String, List<AID>> centerAgents) {
		this.centerAgents = centerAgents;
	}

	public List<Agent> getStartedAgents() {
		return startedAgents;
	}

	public void setStartedAgents(List<Agent> startedAgents) {
		this.startedAgents = startedAgents;
	}
	
	
}

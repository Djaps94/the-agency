package beans;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

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
	
	public Iterator<AgentType> getSupportedTypes() {
		return supportedTypes.iterator();
	}
	
	public Stream<AgentType> getSupportedTypesStream(){
		return supportedTypes.stream();
	}
	
	public boolean isSupportedContained(AgentType t){
		return supportedTypes.contains(t);
	}

	public void setSupportedTypes(Set<AgentType> supportedTypes) {
		this.supportedTypes = supportedTypes;
	}

	public Iterator<AID> getRunningAgents() {
		return runningAgents.iterator();
	}
	
	public Stream<AID> getRunningAgentsStream(){
		return runningAgents.stream();
	}
	
	public void addRunningAgent(AID aid){
		runningAgents.add(aid);
	}
	
	public void removeRunningAgent(AID aid){
		runningAgents.remove(aid);
	}
	
	public void removeAllRunningAgents(Iterator<AID> agents){
		while(agents.hasNext())
			runningAgents.remove(agents.next());
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

	public Iterator<Agent> getStartedAgents() {
		return startedAgents.iterator();
	}
	
	public void addStartedAgent(Agent agent){
		this.startedAgents.add(agent);
	}
	
	public void removeStartedAgent(Agent agent){
		this.startedAgents.remove(agent);
	}

	public void setStartedAgents(List<Agent> startedAgents) {
		this.startedAgents = startedAgents;
	}
	
	
}

package beans;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Stream;

import javax.annotation.PostConstruct;
import javax.ejb.Singleton;

import model.ACLMessage.Performative;
import model.AID;
import model.AgentType;

@Singleton
public class AgencyManager implements AgencyManagerLocal {


	private Map<String, List<AID>> centerAgents;
	private Set<AgentType> supportedTypes; 
	private Map<String,Set<AgentType>> otherSupportedTypes;
	public static Performative performative;
	
	public AgencyManager() { }
	
	@PostConstruct
	public void initialise(){
		this.supportedTypes = new HashSet<AgentType>();
		this.otherSupportedTypes = new HashMap<String, Set<AgentType>>();
		this.centerAgents = new HashMap<String, List<AID>>();
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

	public Iterator<Entry<String, Set<AgentType>>> getOtherSupportedTypes() {
		return otherSupportedTypes.entrySet().iterator();
	}
	
	public Set<AgentType> getOtherAgentTypes(String key){
		return otherSupportedTypes.get(key);
	}
	
	public Stream<Entry<String, Set<AgentType>>> getOtherSupportedTypesStream(){
		return otherSupportedTypes.entrySet().stream();
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

	public Iterator<Entry<String, List<AID>>> getCenterAgents() {
		return centerAgents.entrySet().iterator();
	}
	
	public List<AID> getCenterAgent(String key) {
		return centerAgents.get(key);
	}
	
	public boolean isAgentContained(String key) {
		return centerAgents.containsKey(key);
	}
	
	public void removeAgent(String key) {
		centerAgents.remove(key);
	}
	
	public void addCenterAgent(String key, List<AID> aids) {
		centerAgents.put(key, aids);
	}
	
	public void addCenterAgents(Iterator<Entry<String, List<AID>>> iter) {
		while(iter.hasNext()){
			Entry<String, List<AID>> entry = iter.next();
			if(centerAgents.containsKey(entry.getKey()))
				centerAgents.get(entry.getKey()).addAll(entry.getValue());
			else
				centerAgents.put(entry.getKey(), entry.getValue());
		}
	}

	public void setCenterAgents(Map<String, List<AID>> centerAgents) {
		this.centerAgents = centerAgents;
	}
}

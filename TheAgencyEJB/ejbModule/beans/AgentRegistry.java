package beans;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Stream;

import javax.annotation.PostConstruct;
import javax.ejb.LocalBean;
import javax.ejb.Singleton;

import model.AID;
import model.Agent;

@Singleton
@LocalBean
public class AgentRegistry implements AgentRegistryLocal {

	private List<AID> runningAID;
	private List<Agent> runningAgents;

	public AgentRegistry() {

	}

	@PostConstruct
	private void initialise() {
		this.runningAID = new ArrayList<AID>();
		this.runningAgents = new ArrayList<Agent>();
	}

	public Iterator<AID> getRunningAID() {
		return runningAID.iterator();
	}

	public Stream<AID> getRunningAIDStream() {
		return runningAID.stream();
	}

	public void addRunningAID(AID aid) {
		runningAID.add(aid);
	}

	public void removeRunningAID(AID aid) {
		for(int i = 0; i < runningAID.size(); i++){
			if(runningAID.get(i).getName().equals(aid.getName())){
				runningAID.remove(i);
				break;
			}
		}
	}

	public void removeAllRunningAIDs(Iterator<AID> agents) {
		while (agents.hasNext()){
			for(int i = 0; i < runningAID.size(); i++){
				if(runningAID.get(i).getName().equals(agents.next().getName())){
					runningAID.remove(i);
					break;
				}
			}
		}
	}

	public void setRunningAID(List<AID> runningAgents) {
		this.runningAID = runningAgents;
	}

	public Iterator<Agent> getRunningAgents() {
		return runningAgents.iterator();
	}

	public void addRunningAgent(Agent agent) {
		this.runningAgents.add(agent);
	}

	public void removeRunningAgent(AID agent) {
		for (Agent a : runningAgents) {
			if (a.getId().equals(agent))
				this.runningAgents.remove(a);
		}
	}

	public void setRunnnigAgents(List<Agent> startedAgents) {
		this.runningAgents = startedAgents;
	}

}

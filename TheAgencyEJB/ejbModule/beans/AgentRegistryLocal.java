package beans;

import java.util.Iterator;
import java.util.List;
import java.util.stream.Stream;

import javax.ejb.Local;

import model.AID;
import model.Agent;

@Local
public interface AgentRegistryLocal {
	
	Iterator<AID> getRunningAID();
	Stream<AID> getRunningAIDStream();
	void addRunningAID(AID aid);
	void removeRunningAID(AID aid);
	void setRunningAID(List<AID> runningAgents);
	void removeAllRunningAIDs(Iterator<AID> agents);
	Iterator<Agent> getRunningAgents();
	void addRunningAgent(Agent agent);
	void removeRunningAgent(AID agent);
	void setRunnnigAgents(List<Agent> startedAgents);

}

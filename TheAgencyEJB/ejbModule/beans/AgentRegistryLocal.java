package beans;

import java.util.Iterator;
import java.util.List;
import java.util.stream.Stream;

import javax.ejb.Local;

import model.AID;
import model.Agent;

@Local
public interface AgentRegistryLocal {
	
	public Iterator<AID> getRunningAID();
	public Stream<AID> getRunningAIDStream();
	public void addRunningAID(AID aid);
	public void removeRunningAID(AID aid);
	public void setRunningAID(List<AID> runningAgents);
	public void removeAllRunningAIDs(Iterator<AID> agents);
	public Iterator<Agent> getRunningAgents();
	public void addRunningAgent(Agent agent);
	public void removeRunningAgent(AID agent);
	public void setRunnnigAgents(List<Agent> startedAgents);

}

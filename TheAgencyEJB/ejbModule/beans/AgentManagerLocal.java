package beans;

import javax.ejb.Local;

import model.Agent;
import model.AgentType;

@Local
public interface AgentManagerLocal {
	
	public Agent startAgent(Agent agent, String[] typesPart, AgentType t);
	public void stopAgent(Agent agent);

}

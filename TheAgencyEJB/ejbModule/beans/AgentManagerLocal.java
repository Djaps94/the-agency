package beans;

import javax.ejb.Local;

import model.AID;
import model.AgentType;

@Local
public interface AgentManagerLocal {
	
	public AID startAgent(AID agent);
	public AID stopAgent(AID agent);

}

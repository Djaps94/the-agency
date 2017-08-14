package beans;

import javax.ejb.Local;

import model.AID;

@Local
public interface AgentManagerLocal {
	
	AID startAgent(AID agent);
	AID stopAgent(AID agent);

}

package operations;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeoutException;

import javax.ejb.Local;

import exceptions.ConnectionException;
import exceptions.NodeExistsException;
import exceptions.RegisterSlaveException;
import model.AID;
import model.AgentCenter;
import model.AgentType;
import model.ServiceMessage;

@Local
public interface WorkerLocal {

	List<AgentCenter> registerCenter(ServiceMessage message) throws RegisterSlaveException, ConnectionException, NodeExistsException, IOException, TimeoutException, InterruptedException;
	Map<String, Set<AgentType>> registerAgentTypes(ServiceMessage message) throws ConnectionException, IOException, TimeoutException, InterruptedException;
	void addTypes(ServiceMessage message);
	void rollback(ServiceMessage message) throws ConnectionException, IOException, TimeoutException, InterruptedException;
	Map<String,List<AID>> getRunningAgents();
	void deleteAgent(ServiceMessage message);
	void addAgent(ServiceMessage message);
	AID runAgent(ServiceMessage message);
	AID stopAgent(ServiceMessage message);
	void streamMessage(ServiceMessage message); 
}

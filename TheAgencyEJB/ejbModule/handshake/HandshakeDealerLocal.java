package handshake;

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
import model.Agent;
import model.AgentCenter;
import model.AgentType;
import model.ServiceMessage;

@Local
public interface HandshakeDealerLocal {

	public List<AgentCenter> registerCenter(ServiceMessage message) throws RegisterSlaveException, ConnectionException, NodeExistsException, IOException, TimeoutException, InterruptedException;
	public Map<String, Set<AgentType>> registerAgentTypes(ServiceMessage message) throws ConnectionException, IOException, TimeoutException, InterruptedException;
	public void addTypes(ServiceMessage message);
	public void rollback(ServiceMessage message) throws ConnectionException, IOException, TimeoutException, InterruptedException;
	public Map<String,List<AID>> getRunningAgents();
	public void deleteAgent(ServiceMessage message);
	public void addAgent(ServiceMessage message);
	public AID runAgent(ServiceMessage message);
	public AID stopAgent(ServiceMessage message);
}

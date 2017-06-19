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
import model.HandshakeMessage;

@Local
public interface HandshakeDealerLocal {

	public List<AgentCenter> registerCenter(HandshakeMessage message) throws RegisterSlaveException, ConnectionException, NodeExistsException, IOException, TimeoutException, InterruptedException;
	public Map<String, Set<AgentType>> registerAgentTypes(HandshakeMessage message) throws ConnectionException, IOException, TimeoutException, InterruptedException;
	public void addTypes(HandshakeMessage message);
	public void rollback(HandshakeMessage message) throws ConnectionException, IOException, TimeoutException, InterruptedException;
	public Map<String,List<AID>> getRunningAgents();
	public void deleteAgent(HandshakeMessage message);
	public void addAgent(HandshakeMessage message);
	public AID runAgent(HandshakeMessage message);
	public AID stopAgent(HandshakeMessage message);
}

package handshake;

import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.ejb.Local;

import exceptions.ConnectionException;
import exceptions.RegisterSlaveException;
import model.AgentCenter;
import model.AgentType;
import model.HandshakeMessage;

@Local
public interface HandshakeDealerLocal {

	public List<AgentCenter> registerCenter(HandshakeMessage message) throws RegisterSlaveException, ConnectionException;
	public Map<String, Set<AgentType>> registerAgentTypes(HandshakeMessage message) throws ConnectionException;
	public void addTypes(HandshakeMessage message);
	public void rollback(HandshakeMessage message) throws ConnectionException;
}

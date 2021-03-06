package operations;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import beans.AgencyManagerLocal;
import beans.AgencyRegistryLocal;
import beans.AgentManagerLocal;
import beans.AgentRegistryLocal;
import beans.NetworkManagmentLocal;
import beans.SocketSenderLocal;
import exceptions.ConnectionException;
import exceptions.NodeExistsException;
import exceptions.RegisterSlaveException;
import model.AID;
import model.AgentCenter;
import model.AgentType;
import model.ServiceMessage;
import model.ServiceMessage.OperationType;
import service.MessageRequestLocal;
import util.SocketMessage;
import util.SocketMessage.messageType;

@Stateless
public class Worker implements WorkerLocal {

	@EJB
	private AgencyRegistryLocal registry;

	@EJB
	private NetworkManagmentLocal nodesManagment;

	@EJB
	private MessageRequestLocal requester;

	@EJB
	private AgencyManagerLocal manager;

	@EJB
	private AgentManagerLocal agentManager;

	@EJB
	private SocketSenderLocal socketSender;

	@EJB
	private AgentRegistryLocal agentRegistry;

	public Worker() {
	}

	public List<AgentCenter> registerCenter(ServiceMessage message) throws RegisterSlaveException, ConnectionException,
			NodeExistsException, IOException, TimeoutException, InterruptedException {
		if (nodesManagment.isMaster()) {

			Iterator<AgentCenter> centers = registry.getCentersIterator();
			
			while (centers.hasNext()) {
				requester.sendMessage(centers.next().getAddress(), message);
			}

			registry.addCenter(message.getCenter());
			List<AgentCenter> list = registry.getCenters()
					.filter(center -> !center.getAlias().equals(message.getCenter().getAlias()))
					.collect(Collectors.toList());

			list.add(registry.getThisCenter());
			return list;
		}

		registry.addCenter(message.getCenter());
		return new ArrayList<AgentCenter>();
	}

	public Map<String, Set<AgentType>> registerAgentTypes(ServiceMessage message) throws ConnectionException, IOException, TimeoutException, InterruptedException {
		Map<String, Set<AgentType>> types = new HashMap<String, Set<AgentType>>();

		if (nodesManagment.isMaster()) {
			Set<AgentType> temp = new HashSet<AgentType>();

			Iterator<AgentType> typeIterator = manager.getSupportedTypes();
			
			while (typeIterator.hasNext())
				temp.add(typeIterator.next());
			
			types.put(registry.getThisCenter().getAlias(), temp);
			 
			
			if (manager.getOtherSupportedTypes().hasNext())
				manager.getOtherSupportedTypesStream().forEach(entry -> types.put(entry.getKey(), entry.getValue()));

			manager.addOtherTypes(message.getCenter().getAlias(), message.getAgentTypes());
			message.setType(OperationType.DELIVER_TYPES);

			Iterator<AgentCenter> centerIterator = registry.getCentersIterator();
			
			while (centerIterator.hasNext()) {
				AgentCenter center = centerIterator.next();
				if (!center.getAlias().equals(message.getCenter().getAlias()))
					requester.sendMessage(center.getAddress(), message);
			}

			SocketMessage msg = new SocketMessage();
			msg.setMsgType(messageType.ADD_TYPE);
			msg.setAgentTypes(message.getAgentTypes());
			socketSender.socketSend(msg);
			return types;
		}
		return types;
	}

	public void addTypes(ServiceMessage message) {
		manager.addOtherTypes(message.getCenter().getAlias(), message.getAgentTypes());
	}

	public void rollback(ServiceMessage message) throws ConnectionException, IOException, TimeoutException, InterruptedException {
		if (nodesManagment.isMaster()) {
			registry.deleteCenter(message.getCenter());
			Set<AgentType> type = manager.getOtherAgentTypes(message.getCenter().getAlias());
			manager.deleteOtherTypes(message.getCenter().getAlias());
			agentRegistry.removeAllRunningAIDs(message.getRunningAgents().iterator());

			SocketMessage msg = new SocketMessage();
			msg.setMsgType(messageType.REMOVE_AGENTS);
			msg.setRunningAgents(message.getRunningAgents());
			socketSender.socketSend(msg);

			SocketMessage m = new SocketMessage();
			msg.setMsgType(messageType.REMOVE_TYPES);
			msg.setAgentTypes(type);
			socketSender.socketSend(m);

			Iterator<AgentCenter> centers = registry.getCentersIterator();
			
			while (centers.hasNext())
				requester.sendMessage(centers.next().getAddress(), message);

			return;
		}

		registry.deleteCenter(message.getCenter());
		Set<AgentType> types = manager.getOtherAgentTypes(message.getCenter().getAlias());
		manager.deleteOtherTypes(message.getCenter().getAlias());
		agentRegistry.removeAllRunningAIDs(message.getRunningAgents().iterator());

		SocketMessage msg = new SocketMessage();
		msg.setMsgType(messageType.REMOVE_AGENTS);
		msg.setRunningAgents(message.getRunningAgents());
		socketSender.socketSend(msg);

		SocketMessage m = new SocketMessage();
		msg.setMsgType(messageType.REMOVE_TYPES);
		msg.setAgentTypes(types);
		socketSender.socketSend(m);
	}

	public Map<String, List<AID>> getRunningAgents() {
		Map<String, List<AID>> agents = new HashMap<String, List<AID>>();
		List<AID> tempAgents = new ArrayList<>();

		Iterator<AID> aids = agentRegistry.getRunningAID();
		
		while (aids.hasNext())
			tempAgents.add(aids.next());

		agents.put(registry.getThisCenter().getAlias(), tempAgents);
		return agents;
	}

	@Override
	public void deleteAgent(ServiceMessage message) {
		manager.getCenterAgent(message.getCenter().getAlias()).remove(message.getAid());

		SocketMessage msg = new SocketMessage();
		msg.setMsgType(messageType.STOP_AGENT);
		msg.setAid(message.getAid());
		socketSender.socketSend(msg);
	}

	@Override
	public void addAgent(ServiceMessage message) {
		if (manager.isAgentContained(message.getCenter().getAlias())) {
			manager.getCenterAgent(message.getCenter().getAlias()).add(message.getAid());
		} else {
			List<AID> list = new ArrayList<>();
			list.add(message.getAid());
			manager.addCenterAgent(message.getCenter().getAlias(), list);
		}

		SocketMessage msg = new SocketMessage();
		msg.setMsgType(messageType.START_AGENT);
		msg.setAid(message.getAid());
		socketSender.socketSend(msg);
	}

	@Override
	public AID runAgent(ServiceMessage message) {
		return agentManager.startAgent(message.getAid());
	}

	public AID stopAgent(ServiceMessage message) {
		return agentManager.stopAgent(message.getAid());
	}
	
	public void streamMessage(ServiceMessage message) {
		SocketMessage msg = new SocketMessage();
		msg.setMsgType(messageType.STREAM_MESSAGE);
		msg.setInfoStream(message.getMessageInfo());
		socketSender.socketSend(msg);
	}

}

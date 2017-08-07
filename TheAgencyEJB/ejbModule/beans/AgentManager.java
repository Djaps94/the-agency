package beans;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import exceptions.ConnectionException;
import handshake.HandshakeRequesterLocal;
import model.AID;
import model.Agent;
import model.HandshakeMessage;
import model.HandshakeMessage.handshakeType;
import util.SocketMessage;
import util.SocketMessage.messageType;

@Stateless
@LocalBean
public class AgentManager implements AgentManagerLocal {

	@EJB
	private AgencyManagerLocal manager;
	
	@EJB
	private AgencyRegistryLocal registry;
	
	@EJB
	private HandshakeRequesterLocal requester;
	
	@EJB
	private SocketSenderLocal socketSender;

    public AgentManager() {
    
    }

	@Override
	public AID startAgent(AID aid) {
		aid.setHost(registry.getThisCenter());
		try {
			InitialContext context = new InitialContext();
			Agent agent = (Agent)context.lookup("java:module/"+aid.getType().getName());
			agent.setId(aid);
			manager.getStartedAgents().add(agent);
		} catch (NamingException e1) {
			e1.printStackTrace();
		}
		manager.getRunningAgents().add(aid);
		HandshakeMessage message = new HandshakeMessage(handshakeType.ADD_AGENT);
		message.setAid(aid);
		message.setCenter(registry.getThisCenter());
		registry.getCenters().forEach(center -> {
			try {
				requester.sendMessage(center.getAddress(), message);
			} catch (ConnectionException | IOException | TimeoutException | InterruptedException e) {
				System.out.println("Can't send agent to other centers");
			}
		});
		SocketMessage msg = new SocketMessage();
		msg.setMsgType(messageType.START_AGENT);
		msg.setAid(aid);
		socketSender.socketSend(msg);
		return aid;
	}

	@Override
	public AID stopAgent(AID agent) {
		manager.getRunningAgents().remove(agent);
		HandshakeMessage message = new HandshakeMessage(handshakeType.DELETE_AGENT);
		message.setAid(agent);
		message.setCenter(registry.getThisCenter());
		registry.getCenters().forEach(center -> {
			try {
				requester.sendMessage(center.getAddress(), message);
			} catch (ConnectionException | IOException | TimeoutException | InterruptedException e) {
			}
		});
		SocketMessage msg = new SocketMessage();
		msg.setMsgType(messageType.STOP_AGENT);
		msg.setAid(agent);
		socketSender.socketSend(msg);
		return agent;
	}

}

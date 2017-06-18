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
import model.AgentType;
import model.HandshakeMessage;
import model.HandshakeMessage.handshakeType;

@Stateless
@LocalBean
public class AgentManager implements AgentManagerLocal {

	@EJB
	private AgencyManagerLocal manager;
	
	@EJB
	private AgencyRegistryLocal registry;
	
	@EJB
	private HandshakeRequesterLocal requester;

    public AgentManager() {
    
    }

	@Override
	public Agent startAgent(Agent agent, String[] typesPart, AgentType t, String name) {
		try {
			InitialContext context = new InitialContext();
			agent = (Agent)context.lookup("ejb:/TheAgency/"+typesPart[1].trim()+"!"+Class.forName(typesPart[1].trim()).getName()+"?stateful");
			AID aid = new AID();
			aid.setType(t);
			aid.setName(name);
			aid.setHost(registry.getThisCenter());
		} catch (NamingException | ClassNotFoundException e) { 
			System.out.println("Agent not found!");
			return null;
		}
		manager.getRunningAgents().add(agent);
		HandshakeMessage message = new HandshakeMessage(handshakeType.ADD_AGENT);
		message.setAgent(agent);
		message.setCenter(registry.getThisCenter());
		registry.getCenters().stream().forEach(center -> {
			try {
				requester.sendMessage(center.getAddress(), message);
			} catch (ConnectionException | IOException | TimeoutException | InterruptedException e) {
				System.out.println("Can't send agent to other centers");
			}
		});
		
		return agent;
	}

	@Override
	public Agent stopAgent(Agent agent) {
		manager.getRunningAgents().remove(agent);
		HandshakeMessage message = new HandshakeMessage(handshakeType.DELETE_AGENT);
		message.setAgent(agent);
		message.setCenter(registry.getThisCenter());
		registry.getCenters().stream().forEach(center -> {
			try {
				requester.sendMessage(center.getAddress(), message);
			} catch (ConnectionException | IOException | TimeoutException | InterruptedException e) {
			}
		});
		return agent;
	}

}

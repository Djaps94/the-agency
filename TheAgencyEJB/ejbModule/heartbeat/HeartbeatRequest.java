package heartbeat;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.annotation.PreDestroy;
import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Singleton;
import javax.ejb.Timeout;
import javax.ejb.Timer;
import javax.ejb.TimerConfig;
import javax.ejb.TimerService;

import org.zeromq.ZMQ;

import beans.AgencyManagerLocal;
import beans.AgencyRegistryLocal;
import model.Agent;
import model.AgentCenter;
import util.PortTransformation;


@Singleton
@LocalBean
public class HeartbeatRequest implements HeartbeatRequestLocal {

	@Resource
	private TimerService timer;
	
	@EJB
	private AgencyRegistryLocal registry;
	
	@EJB
	private AgencyManagerLocal manager;
	
    public HeartbeatRequest() {
        // TODO Auto-generated constructor stub
    }


	@Override
	public void startTimer() {
		timer.createIntervalTimer(1000*15, 1000*15, new TimerConfig("Heartbeat", false));
	}
	
	@Timeout
	private void sendPulse(Timer timer){
		ZMQ.Context context = ZMQ.context(1);
		List<AgentCenter> deadList = new ArrayList<>();
		for(AgentCenter center : registry.getCenters()){
			ZMQ.Socket socket = createSocket(context);
			socket.connect("tcp://"+PortTransformation.transform(center.getAddress(), 5));
			socket.send("Check pulse", 0);
			String data = socket.recvStr();
			System.out.println("Its "+data);
			if(data == null){
				socket.close();
				socket = createSocket(context);
				System.out.println("ONE MORE TIME");
				socket.send("Check pulse", 0);
				String temp = socket.recvStr();
				if(temp == null){
					socket.close();
					deadList.add(center);
				}
			}
			socket.close();
		}
			if(!deadList.isEmpty())
				removeDeadCenter(deadList);
			System.out.println("I was here :D");
			context.term();
	}
	
	private void removeDeadCenter(List<AgentCenter> centers){
		for(AgentCenter center : centers){
			registry.deleteCenter(center);
			manager.deleteOtherTypes(center.getAlias());
			Optional<Agent> deleteAgent =  manager.getRunningAgents().stream()
									  						  .filter(agent -> agent.getId().getHost().getAddress().equals(center.getAddress()))
									  						  .findFirst();
			if(deleteAgent.isPresent())
				manager.getRunningAgents().remove(deleteAgent.get());
		}
	}
	
	private ZMQ.Socket createSocket(ZMQ.Context context){
		ZMQ.Socket socket = context.socket(ZMQ.REQ);
		socket.setReceiveTimeOut(2000);
		return socket;
	}

}

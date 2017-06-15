package heartbeat;

import javax.ejb.ActivationConfigProperty;
import javax.ejb.Asynchronous;
import javax.ejb.EJB;
import javax.ejb.MessageDriven;
import javax.jms.Message;
import javax.jms.MessageListener;

import org.zeromq.ZMQ;

import beans.AgencyRegistryLocal;
import beans.NetworkManagmentLocal;
import util.PortTransformation;


@MessageDriven(
		activationConfig = { @ActivationConfigProperty(
				propertyName = "destinationType", propertyValue = "javax.jms.Queue"),
							 @ActivationConfigProperty(
				propertyName = "destination", propertyValue = "java:/jms/queue/Heartbeat"
									 ),
							 @ActivationConfigProperty(
				propertyName = "transactionTimeout", propertyValue = "300000")
		})
public class HeartbeatResponse implements MessageListener {

	@EJB
	private AgencyRegistryLocal registry;
	
	@EJB
	private NetworkManagmentLocal centerManagment;

    public HeartbeatResponse() {

    }
	
    public void onMessage(Message message) {
    	if(!centerManagment.isHeartbeatFlag())
    		response();
    }
    
    @Asynchronous
    private void response(){
    	centerManagment.setHeartbeatFlag(true);
    	ZMQ.Context context = ZMQ.context(1);
    	ZMQ.Socket socket = context.socket(ZMQ.REP);
    	socket.bind("tcp://"+PortTransformation.transform(registry.getThisCenter().getAddress(), 5));
    	while(!Thread.currentThread().isInterrupted()){
    		String data = socket.recvStr();
    		if(data == null)
    			continue;
    		
    		socket.send("Alive!");
    	}
    	
    	socket.close();
    	context.term();
    }

}

package agents;

import javax.ejb.Stateless;

import model.ACLMessage;
import model.Agent;

@Stateless
public class WeatherSlave extends Agent {
    
	private static final long serialVersionUID = -7183133844763416517L;

	public WeatherSlave() {
        
    }

	@Override
	public void handleMessage(ACLMessage message) {
		
		
	}

}

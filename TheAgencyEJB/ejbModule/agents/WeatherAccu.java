package agents;

import javax.ejb.Stateful;

import model.ACLMessage;
import model.Agent;

@Stateful
public class WeatherAccu extends Agent {
    
	private static final long serialVersionUID = -7183133844763416517L;

	public WeatherAccu() {
        
    }

	@Override
	public void handleMessage(ACLMessage message) {
		
		
	}

}

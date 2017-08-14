package agents;

import javax.ejb.Stateless;

import model.ACLMessage;
import model.Agent;


@Stateless
public class WeatherMaster extends Agent{

	private static final long serialVersionUID = -7609277672062916060L;

	public WeatherMaster() {
       
    }

	@Override
	public void handleMessage(ACLMessage message) {
		
	}

}

package heartbeat;

import javax.ejb.Local;

@Local
public interface HeartbeatRequestLocal {

	public void startTimer();
	
}

package heartbeat;

import javax.ejb.Local;

@Local
public interface HeartBeatResponseLocal {
	
	void pulseTick();

}

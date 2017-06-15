package beans;

import javax.ejb.Local;

@Local
public interface NetworkManagmentLocal {

	public boolean isMaster();
	public String getMasterAddress();
	public boolean isRecieverRunning();
	public void setRecieverRunning(boolean flag);
}

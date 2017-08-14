package beans;

import javax.ejb.Local;

@Local
public interface NetworkManagmentLocal {

	boolean isMaster();
	String getMasterAddress();
}

package beans;

import javax.ejb.Local;

@Local
public interface MessageStreamLocal {

	public void streamMessage(String content, String address);
}

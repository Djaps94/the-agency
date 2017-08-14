package beans;

import javax.ejb.Local;

@Local
public interface MessageStreamLocal {

	void streamMessage(String content, String address);
}

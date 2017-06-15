package handshake;

import javax.ejb.Local;

@Local
public interface HandshakeResponseLocal {

	public void waitMessage();
}

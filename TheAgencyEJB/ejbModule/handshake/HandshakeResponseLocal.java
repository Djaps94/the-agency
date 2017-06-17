package handshake;

import java.io.IOException;

import javax.ejb.Local;

@Local
public interface HandshakeResponseLocal {

	public void waitMessage() throws IOException;
}

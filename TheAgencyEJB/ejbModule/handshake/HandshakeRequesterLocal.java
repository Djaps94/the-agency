package handshake;

import javax.ejb.Local;

import model.HandshakeMessage;

@Local
public interface HandshakeRequesterLocal {
	
	public void sendMessage(String destination, HandshakeMessage message);

}

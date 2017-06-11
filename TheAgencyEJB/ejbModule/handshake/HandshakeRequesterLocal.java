package handshake;

import javax.ejb.Local;

import exceptions.ConnectionException;
import model.HandshakeMessage;

@Local
public interface HandshakeRequesterLocal {
	
	public HandshakeMessage sendMessage(String destination, HandshakeMessage message) throws ConnectionException;

}

package handshake;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

import javax.ejb.Local;

import exceptions.ConnectionException;
import model.ServiceMessage;

@Local
public interface HandshakeRequesterLocal {
	
	public ServiceMessage sendMessage(String destination, ServiceMessage message) throws ConnectionException, IOException, TimeoutException, InterruptedException;

}

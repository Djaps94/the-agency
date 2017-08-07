package service;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

import javax.ejb.Local;

import exceptions.ConnectionException;
import model.ServiceMessage;

@Local
public interface MessageRequestLocal {
	
	public ServiceMessage sendMessage(String destination, ServiceMessage message) throws ConnectionException, IOException, TimeoutException, InterruptedException;

}

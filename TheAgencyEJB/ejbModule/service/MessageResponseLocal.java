package service;

import java.io.IOException;

import javax.ejb.Local;

@Local
public interface MessageResponseLocal {

	void waitMessage() throws IOException;
}

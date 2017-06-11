package model;

import java.util.List;

public class HandshakeMessage {

	public enum handshakeType { REGISTER,
								GET_CENTERS
							  };
	
	private AgentCenter center;
	private handshakeType type;
	private List<AgentCenter> centers;
	
	public HandshakeMessage() { }
	
	public HandshakeMessage(AgentCenter center, handshakeType type){
		this.center = center;
		this.type   = type;
	}

	public AgentCenter getCenter() {
		return center;
	}

	public void setCenter(AgentCenter center) {
		this.center = center;
	}

	public handshakeType getType() {
		return type;
	}

	public void setType(handshakeType type) {
		this.type = type;
	}

	public List<AgentCenter> getCenters() {
		return centers;
	}

	public void setCenters(List<AgentCenter> centers) {
		this.centers = centers;
	}
	
	
}

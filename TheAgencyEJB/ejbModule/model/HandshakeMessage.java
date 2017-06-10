package model;

public class HandshakeMessage {

	public enum handshakeType { REGISTER };
	
	private AgentCenter center;
	private handshakeType type;
	
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
	
	
}

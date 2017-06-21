package model;

import java.io.Serializable;

public class AID implements Serializable{

	private static final long serialVersionUID = 1L;
	private String name;
	private AgentCenter host;
	private AgentType type;
	
	public AID() { }

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public AgentCenter getHost() {
		return host;
	}

	public void setHost(AgentCenter host) {
		this.host = host;
	}

	public AgentType getType() {
		return type;
	}

	public void setType(AgentType type) {
		this.type = type;
	}
}

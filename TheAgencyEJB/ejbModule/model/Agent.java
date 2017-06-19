package model;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import agents.*;

@SuppressWarnings("serial")
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.PROPERTY, property = "type")
@JsonSubTypes({
	@JsonSubTypes.Type(value = PingAgent.class, name = "Ping"),
	@JsonSubTypes.Type(value = PongAgent.class, name = "Pong")
})
public abstract class Agent implements Serializable{
	
	protected AID id;
	
	public abstract void handleMessage(ACLMessage message);
	
	public AID getId() {
		return id;
	}
	
	public void setId(AID id) {
		this.id = id;
	}

}

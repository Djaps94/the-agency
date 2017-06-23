package model;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import agents.*;

@SuppressWarnings("serial")
public abstract class Agent implements Serializable{
	
	protected AID id;
	
	public abstract void handleMessage(ACLMessage message);
	
	public void notSupportedMethod(){
		System.out.println("This agent does not support this kind of a message");
	}
	
	public AID getId() {
		return id;
	}
	
	public void setId(AID id) {
		this.id = id;
	}

}

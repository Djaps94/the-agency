package model;

import java.io.Serializable;
import java.util.Set;

public class AgentCenter implements Serializable{
	
	private static final long serialVersionUID = 3617071009441644990L;
	
	private String alias;
	private String address;
	private Set<AgentType> supportedTypes; 
	
	public AgentCenter(){ }
	
	public AgentCenter(String alias, String address){
		this.alias   = alias;
		this.address = address;
	}
	
	public String getAlias() {
		return alias;
	}
	public void setAlias(String alias) {
		this.alias = alias;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}

	public Set<AgentType> getSupportedTypes() {
		return supportedTypes;
	}

	public void setSupportedTypes(Set<AgentType> supportedTypes) {
		this.supportedTypes = supportedTypes;
	}

}

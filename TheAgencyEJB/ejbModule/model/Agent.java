package model;

import java.io.Serializable;

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

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Agent other = (Agent) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

}

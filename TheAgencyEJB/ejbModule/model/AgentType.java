package model;

import java.io.Serializable;

public class AgentType implements Serializable {
	
	private static final long serialVersionUID = 6355526464588438905L;
	
	private String name;
	private String module;
	
	public AgentType() { }
	
	public AgentType(String name, String module){
		this.name 	= name;
		this.module = module;
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getModule() {
		return module;
	}
	public void setModule(String module) {
		this.module = module;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((module == null) ? 0 : module.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
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
		AgentType other = (AgentType) obj;
		if (module == null) {
			if (other.module != null)
				return false;
		} else if (!module.equals(other.module))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}
	
	

}

package net.nansore.prolog;

import java.io.Serializable;

public class Variable implements Serializable {
	/**
     * 
     */
    private static final long serialVersionUID = 1L;
    private static int genNumber = 0;
	private String name;
	private Object boundTo = null;

	public Variable() {
		this("Var" + generateNumber());
	}

	public Variable(String string) {
		name = string;
	}

	private static synchronized int generateNumber() {
		return genNumber++;
	}
	
	public String name() {
		return name;
	}
	
	public boolean equals(Object other) {
		if(!(other instanceof Variable))
			return false;
		return ((Variable)other).name().equals(name);
	}
	
	public int hashCode() {
		return name.hashCode();
	}

	public void bind(Object term) {
		boundTo  = term;
	}
	
	public boolean isBound() {
		return boundTo() != null;
	}
	
	public Object boundTo() {
		while(boundTo != null && boundTo instanceof Variable)
			boundTo = ((Variable)boundTo).boundTo();
		return boundTo;
	}
	
	public String toString()
	{
		return name;
	}
}

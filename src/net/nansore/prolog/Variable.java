package net.nansore.prolog;

import java.io.Serializable;

/**
 * An Variable object represents a logic variable.
 * Logic variables start their lives unbound, and can, at some point, be bound to a value.
 * They can also be bound to other variables.  In such cases, their ultimate bound value
 * is the value bound to the last variable in the chain, making note that a variable can be bound to itself.
 * We consider a variable to be bound if it is bound to a non-variable.
 * A bound variable cannot change its value, except by backtracking.  This class, however, does not enforce this.
 */
public class Variable implements Serializable {
    private static final long serialVersionUID = 1L;
    private static int genNumber = 0;
	private String name;
	private Object boundTo = this;

	/**
	 * Construct a variable named Var# (# to be replaced with an ordinal number
	 */
	public Variable() {
		this("Var" + generateNumber());
	}

	/**
	 * Create a variable
	 * @param string the name of the variable
	 */
	public Variable(String string) {
		name = string;
	}

	private static synchronized int generateNumber() {
		return genNumber++;
	}
	
	/**
	 * @return the variable name
	 */
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

	/**
	 * Bind a value to this variable. This operation respects backtracking.
	 * @param term the new term to be bound
	 */
	public void bind(Object term) {
		BacktrackingStack.instance().push(this, boundTo);
		setValue(term);
	}
	
	/**
	 * @return true if this variable is bound, that is, if the value it is bound to is not a variable.
	 */
	public boolean isBound() {
		return !(boundTo() instanceof Variable);
	}
	
	/**
	 * @return the object this variable is bound to.  If this variable is bound to a variable and the variable is bound to a value, that value is returned.
	 */
	public Object boundTo() {
		if(boundTo != this && boundTo instanceof Variable)
			bind(((Variable)boundTo).boundTo());
		return boundTo;
	}
	
	public String toString()
	{
		return name;
	}

	/**
	 * Changes the value to which this variable is bound to.  This operation does not respect backtracking.
	 * @param val the new value to set
	 */
	public void setValue(Object val) {
		boundTo  = val;
	}
}

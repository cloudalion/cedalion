package net.nansore.prolog;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

/**
 * A compound term.  Such a term has a name, and zero or more arguments, which are any kind of objects.
 */
public class Compound implements Serializable {
	/**
     * Serialization version
     */
    private static final long serialVersionUID = 1L;
    private List<Object> args = new ArrayList<Object>();
	private String name;
	
	/**
	 * Create a new compound with the given name and arguments
	 * @param name the name of this compound
	 * @param args zero or more arguments, which can be Compound, Variable or any other Object
	 */
	public Compound(String name, Object... args) {
		this.name = name;
		this.args = Arrays.asList(args);
	}
	
	/**
	 * @return the number of arguments
	 */
	public int arity() {
		return args.size();
	}
	
	/**
	 * @return the name of this compound
	 */
	public String name() { 
		return name;
	}
	
	/**
	 * Returns the n'th argument.  If the argument is a Variable, the value this Variable is bound to is returned.
	 * @param n the 1-based index of the argument
	 * @return the argument value
	 */
	public Object arg(int n) {
		Object arg = args.get(n - 1);
		if(arg instanceof Variable)
			arg = ((Variable) arg).boundTo();
		return arg;
	}
	
	/**
	 * @return the arguments of this compound.  Variables are returned as is.
	 */
	public List<Object> args() {
		return args;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((args == null) ? 0 : args.hashCode());
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
		Compound other = (Compound) obj;
		if (args == null) {
			if (other.args != null)
				return false;
		} else if (!args.equals(other.args))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}
	
	@Override
	public String toString() {
		if(name.equals(".") && arity() == 2)
			return listToString();
		StringBuffer buff = new StringBuffer();
		buff.append(name);
		if(args.size() > 0) {
			buff.append("(");
			for(Iterator<Object> i = args.iterator(); i.hasNext(); ) {
				buff.append(i.next());
				if(i.hasNext())
					buff.append(",");
			}
			buff.append(")");
		}
		return buff.toString();
	}
	private String listToString() {
		StringBuffer buff = new StringBuffer();
		buff.append("[");
		Compound i = this;
		while(i.name().equals(".") && i.arity() == 2) {
			if(i != this)
				buff.append(", ");
			buff.append(i.arg(1));
			Object next = i.arg(2);
			if(!(next instanceof Compound))
				break;
			i = (Compound)next;
		}
		buff.append("]");
		return buff.toString();
	}
	/**
	 * Perform term unification with another object.
	 * @param other the other object to unify with.
	 * @return true if this term matches other.
	 */
	public boolean unify(Object other) {
		if(other instanceof Variable) {
			Variable otherVar = (Variable)other;
			if(otherVar.isBound()) {
				return unify(otherVar.boundTo());
			} else {
				otherVar.bind(this);
				return true;
			}
		} else if(other instanceof Compound) {
			Compound otherCompound = (Compound)other;
			if(!name.equals(otherCompound.name()) || arity() != otherCompound.arity()) {
				return false;
			}
			for(int i = 1; i <= arity(); i++) {
				if(!unify(arg(i), otherCompound.arg(i)))
					return false;
			}
			return true;
		} else {
			return false;
		}
	}
	/**
	 * Unify two terms
	 * @param term1 the first term
	 * @param term2 the second term
	 * @return true if the two terms match
	 */
	public static boolean unify(Object term1, Object term2) {
		if(term1 instanceof Compound) {
			return ((Compound)term1).unify(term2);
		} else if(term1 instanceof Variable) {
			if(((Variable)term1).isBound()) {
				return unify(((Variable)term1).boundTo(), term2);
			} else {
				((Variable)term1).bind(term2);
				return true;
			}
		} else if(term2 instanceof Variable) {
			return unify(term2, term1);
		} else {
			return term1.equals(term2);
		}
	}
	/**
	 * @return true is this compound does not contain (recursively) any Variables.
	 */
	public boolean isGround() {
		for(Object arg : args) {
			if(arg instanceof Variable)
				arg = ((Variable)arg).boundTo();
			if(arg instanceof Variable)
				return false;
			else if(arg instanceof Compound && !((Compound)arg).isGround())
				return false;
		}
		return true;
	}
	/**
	 * @return a list of all variables in this compound (recursively) 
	 */
	public List<Variable> variables() {
		List<Variable> result = new ArrayList<Variable>();
		for(Object arg : args) {
			if(arg instanceof Variable)
				arg = ((Variable)arg).boundTo();
			
			if(arg instanceof Compound) {
				result.addAll(((Compound)arg).variables());
			} else if(arg instanceof Variable) {
				result.add((Variable)arg);
			}
		}
		return result;
	}

	public static Compound createCompound(String name, Object... args) {
		return new Compound(name, args);
	}
}

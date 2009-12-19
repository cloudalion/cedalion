package net.nansore.prolog;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

public class Compound implements Serializable {
	/**
     * 
     */
    private static final long serialVersionUID = 1L;
    private List<Object> args = new ArrayList<Object>();
	private String name;
	private PrologProxy prolog;
	
	public Compound(PrologProxy prolog, String name) {
		this.name = name;
		this.prolog = prolog;
	}
	public Compound(PrologProxy prolog, String name, Object[] args) {
		this(prolog, name);
		this.args = Arrays.asList(args);
	}
	public Compound(PrologProxy prolog, String name, Object arg1) {
		this(prolog, name, new Object[] {arg1});
	}
	public Compound(PrologProxy prolog, String name, Object arg1, Object arg2) {
		this(prolog, name, new Object[] {arg1, arg2});
	}
	public Compound(PrologProxy prolog, String name, Object arg1, Object arg2, Object arg3) {
		this(prolog, name, new Object[] {arg1, arg2, arg3});
	}
	public Compound(PrologProxy prolog, String name, Object arg1, Object arg2, Object arg3, Object arg4) {
		this(prolog, name, new Object[] {arg1, arg2, arg3, arg4});
	}
	
	public Compound(PrologProxy prolog, String name, Object arg1, Object arg2, Object arg3, Object arg4, Object arg5) {
		this(prolog, name, new Object[] {arg1, arg2, arg3, arg4, arg5});
	}
	
	public Compound(PrologProxy prolog, String name, Object arg1, Object arg2, Object arg3, Object arg4, Object arg5, Object arg6) {
		this(prolog, name, new Object[] {arg1, arg2, arg3, arg4, arg5, arg6});
	}
	
	public int arity() {
		return args.size();
	}
	
	public String name() { 
		return name;
	}
	
	public Object arg(int n) {
		return args.get(n - 1);
	}
	
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
	public String toString() {
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
	public PrologProxy getProlog() {
		return prolog;
	}
}

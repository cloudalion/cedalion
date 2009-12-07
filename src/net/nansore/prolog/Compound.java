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
	
	public Compound(String name) {
		this.name = name;
	}
	public Compound(String name, Object[] args) {
		this(name);
		this.args = Arrays.asList(args);
	}
	public Compound(String name, Object arg1) {
		this(name, new Object[] {arg1});
	}
	public Compound(String name, Object arg1, Object arg2) {
		this(name, new Object[] {arg1, arg2});
	}
	public Compound(String name, Object arg1, Object arg2, Object arg3) {
		this(name, new Object[] {arg1, arg2, arg3});
	}
	public Compound(String name, Object arg1, Object arg2, Object arg3, Object arg4) {
		this(name, new Object[] {arg1, arg2, arg3, arg4});
	}
	
	public Compound(String name, Object arg1, Object arg2, Object arg3, Object arg4, Object arg5) {
		this(name, new Object[] {arg1, arg2, arg3, arg4, arg5});
	}
	
	public Compound(String name, Object arg1, Object arg2, Object arg3, Object arg4, Object arg5, Object arg6) {
		this(name, new Object[] {arg1, arg2, arg3, arg4, arg5, arg6});
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
	public String localName() {
		return PrologClient.toLocalName(name());
	}
}

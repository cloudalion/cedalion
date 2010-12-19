package net.nansore.parser;

public class Operator {
	public enum Assoc {
		LEFT,
		RIGHT,
		UNARY
	}
	
	public Assoc assoc;
	public boolean prefix;
	public int prec;
	public String name;
	
	public Operator(String name, Assoc assoc, boolean prefix, int prec) {
		this.name = name;
		this.assoc = assoc;
		this.prefix = prefix;
		this.prec = prec;
	}
}

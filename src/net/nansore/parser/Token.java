package net.nansore.parser;

public class Token {

	private Type type;
	private String value;

	public Token(Type type, String value) {
		this.type = type;
		this.value = value;
	}

	public enum Type {
		EOF,
		ID,
		STRING,
		NUMBER,
		SPECIAL
	}

}

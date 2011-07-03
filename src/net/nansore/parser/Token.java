package net.nansore.parser;

public class Token {

	private Type type;
	private String value;

	public Token(Type type, String value) {
		this.setType(type);
		this.setValue(value);
	}

	public void setType(Type type) {
		this.type = type;
	}

	public Type getType() {
		return type;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getValue() {
		return value;
	}

	public enum Type {
		EOF,
		ID,
		STRING,
		NUMBER,
		SPECIAL
	}

}

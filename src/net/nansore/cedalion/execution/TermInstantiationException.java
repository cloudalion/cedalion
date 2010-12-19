package net.nansore.cedalion.execution;

public class TermInstantiationException extends Exception {

	private static final long serialVersionUID = 1L;

	public TermInstantiationException(Exception e) {
		super(e);
	}

	public TermInstantiationException(String string) {
		super(string);
	}

}

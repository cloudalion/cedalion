package net.nansore.cedalion.execution;

/**
 * An exception thrown when the instantiation of a term has failed.
 */
public class TermInstantiationException extends Exception {

	private static final long serialVersionUID = 1L;

	public TermInstantiationException(Exception e) {
		super(e);
	}

	public TermInstantiationException(String string) {
		super(string);
	}

}

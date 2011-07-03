package net.nansore.prolog;

/**
 * This exception is thrown by PrologProxy.getSolution(), if no solution is found.
 */
public class NoSolutionsException extends PrologException {

	public NoSolutionsException(String string) {
		super(string);
	}

	private static final long serialVersionUID = 1L;

}

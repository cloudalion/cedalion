package net.nansore.cedalion.execution;


/**
 * Generic exception thrown on a command execution error.
 */
public class ExecutionContextException extends Exception {

	public ExecutionContextException(String string) {
		super(string);
	}

	public ExecutionContextException(Exception e) {
		super(e);
	}

	private static final long serialVersionUID = 1L;

}

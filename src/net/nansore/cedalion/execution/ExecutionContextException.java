package net.nansore.cedalion.execution;

import org.eclipse.ui.PartInitException;

public class ExecutionContextException extends Exception {

	public ExecutionContextException(String string) {
		super(string);
	}

	public ExecutionContextException(PartInitException e) {
		super(e);
	}

	private static final long serialVersionUID = 1L;

}

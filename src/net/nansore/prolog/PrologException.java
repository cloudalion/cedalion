package net.nansore.prolog;

/**
 * A generic exception originating from the logic engine
 */
public class PrologException extends Exception {

	private static final long serialVersionUID = 1L;

	public PrologException() {
		super();
	}

	public PrologException(String arg0, Throwable arg1) {
		super(arg0, arg1);
	}

	public PrologException(String arg0) {
		super(arg0);
	}

	public PrologException(Throwable arg0) {
		super(arg0);
	}

    public PrologException(Object exception) {
        this(exception.toString());
    }

	
	
}

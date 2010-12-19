package net.nansore.cedalion.cmd;

import net.nansore.cedalion.execution.ExecutionContext;
import net.nansore.cedalion.execution.ExecutionContextException;
import net.nansore.cedalion.execution.ICommand;
import net.nansore.cedalion.execution.TermInstantiationException;
import net.nansore.prolog.Compound;
import net.nansore.prolog.PrologException;

public class Assign implements ICommand {
	
	private Object ref;
	private Compound expr;
	private Object type;

	public Assign(Compound term) {
		ref = term.arg(1);
		expr = (Compound) term.arg(2);
		type = term.arg(3);
	}

	public void run(ExecutionContext executionContext) throws PrologException,
			TermInstantiationException, ExecutionContextException {
		executionContext.runFunction(expr, ref, type);
	}

}

package net.nansore.cedalion.cmd;

import net.nansore.cedalion.execution.ExecutionContext;
import net.nansore.cedalion.execution.ExecutionContextException;
import net.nansore.cedalion.execution.ICommand;
import net.nansore.cedalion.execution.TermInstantiationException;
import net.nansore.prolog.Compound;
import net.nansore.prolog.PrologException;
import net.nansore.prolog.Variable;

/**
 * Takes two imperative expressions as arguments, and a target reference.  Calculates the values, 
 * then adds them.  Stores the result in the given target reference.
 */
public class Plus implements ICommand {
	private Object arg1;
	private Object arg2;
	private Object resultTarget;

	public Plus(Compound term) {
		arg1 = term.arg(1);
		arg2 = term.arg(2);
		resultTarget = term.arg(3);
	}

	public void run(ExecutionContext executionContext) throws PrologException,
			TermInstantiationException, ExecutionContextException {
		Variable type = new Variable();
		double v1 = ((Number)executionContext.evaluate(arg1, type)).doubleValue();
		double v2 = ((Number)executionContext.evaluate(arg2, type)).doubleValue();
		executionContext.storeValue(resultTarget, v1+v2);
	}

}

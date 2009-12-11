package net.nansore.cedalion.cmd;

import net.nansore.cedalion.execution.ExecutionContext;
import net.nansore.cedalion.execution.ExecutionContextException;
import net.nansore.cedalion.execution.ICommand;
import net.nansore.cedalion.execution.TermInstantiationException;
import net.nansore.prolog.Compound;
import net.nansore.prolog.PrologException;
import net.nansore.prolog.Variable;

public class AssignFinal implements ICommand {
	private Variable target;
	private Compound expression;
	private Object type;

	public AssignFinal(Compound term) {
		target = (Variable) term.arg(1);
		expression = (Compound) term.arg(2);
		type = term.arg(3);
	}

	public void run(ExecutionContext executionContext) throws PrologException,
			TermInstantiationException, ExecutionContextException {
		target.bind(executionContext.evaluate(expression, type));
	}
	
	
}

package net.nansore.cedalion.cmd;

import net.nansore.cedalion.execution.ExecutionContext;
import net.nansore.cedalion.execution.ExecutionContextException;
import net.nansore.cedalion.execution.ICommand;
import net.nansore.cedalion.execution.TermInstantiationException;
import net.nansore.prolog.Compound;
import net.nansore.prolog.PrologException;

/**
 * Runs a procedure.  This is done by turning to the logic engine to convert the procedure to a command, and then
 * executing the command.
 * Takes one argument: the procedure term to be executed.
 */
public class DoProc implements ICommand {
	private Compound proc;

	public DoProc(Compound term) {
		proc = (Compound)term.arg(1);
	}

	public void run(ExecutionContext executionContext) throws PrologException, TermInstantiationException, ExecutionContextException {
		executionContext.runProcedure(proc);
	}
}

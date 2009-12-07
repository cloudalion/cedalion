package net.nansore.cedalion.cmd;

import net.nansore.cedalion.execution.ExecutionContext;
import net.nansore.cedalion.execution.ICommand;
import net.nansore.cedalion.execution.TermInstantiationException;
import net.nansore.prolog.Compound;
import net.nansore.prolog.PrologException;

public class DoProc implements ICommand {
	private Compound proc;

	public DoProc(Compound term) {
		proc = (Compound)term.arg(1);
	}

	public void run(ExecutionContext executionContext) throws PrologException, TermInstantiationException {
		executionContext.runProcedure(proc);
	}
}

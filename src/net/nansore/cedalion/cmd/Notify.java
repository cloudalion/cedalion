package net.nansore.cedalion.cmd;

import net.nansore.cedalion.execution.ExecutionContext;
import net.nansore.cedalion.execution.ExecutionContextException;
import net.nansore.cedalion.execution.ICommand;
import net.nansore.cedalion.execution.Notifier;
import net.nansore.cedalion.execution.TermInstantiationException;
import net.nansore.prolog.Compound;
import net.nansore.prolog.PrologException;

public class Notify implements ICommand {
	
	private Compound notifyTerm;

	public Notify(Compound term) {
		notifyTerm = (Compound)term.arg(1);
	}

	@Override
	public void run(ExecutionContext executionContext) throws PrologException,
			TermInstantiationException, ExecutionContextException {
		Notifier.instance().notify(notifyTerm);
	}

}

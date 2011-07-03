package net.nansore.cedalion.cmd;

import net.nansore.cedalion.execution.ExecutionContext;
import net.nansore.cedalion.execution.ICommand;
import net.nansore.cedalion.execution.TermInstantiationException;
import net.nansore.prolog.Compound;
import net.nansore.prolog.PrologException;
import net.nansore.prolog.PrologProxy;

/**
 * Removes a statement from the logic engine's database.
 * Accepts one argument: the statement to be removed.
 */
public class DBRemove implements ICommand {
	private Compound statement;

	public DBRemove(Compound term) {
		statement = (Compound)term.arg(1);
	}

	public void run(ExecutionContext executionContext) throws PrologException,
			TermInstantiationException {
		PrologProxy.instance().getSolution(Compound.createCompound("remove", statement));
	}

}

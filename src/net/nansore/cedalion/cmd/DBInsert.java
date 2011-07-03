package net.nansore.cedalion.cmd;

import net.nansore.cedalion.execution.ExecutionContext;
import net.nansore.cedalion.execution.ICommand;
import net.nansore.cedalion.execution.TermInstantiationException;
import net.nansore.prolog.Compound;
import net.nansore.prolog.PrologException;
import net.nansore.prolog.PrologProxy;

/**
 * Inserts the given statement into the logic engine's database
 * Takes one argument: the statement to be inserted.
 */
public class DBInsert implements ICommand {
	private Compound statement;

	public DBInsert(Compound term) {
		statement = (Compound)term.arg(1);
	}

	public void run(ExecutionContext executionContext) throws PrologException,
			TermInstantiationException {
		PrologProxy.instance().getSolution(Compound.createCompound("insert", statement));
	}

}

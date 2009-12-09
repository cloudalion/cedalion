package net.nansore.cedalion.cmd;

import net.nansore.cedalion.execution.ExecutionContext;
import net.nansore.cedalion.execution.ICommand;
import net.nansore.cedalion.execution.TermInstantiationException;
import net.nansore.prolog.Compound;
import net.nansore.prolog.PrologException;
import net.nansore.prolog.PrologProxy;

public class DBInsert implements ICommand {
	private Compound statement;

	public DBInsert(Compound term) {
		statement = (Compound)term.arg(1);
	}

	public void run(ExecutionContext executionContext) throws PrologException,
			TermInstantiationException {
		PrologProxy p = executionContext.prolog();
		p.getSolution(p.createCompound("insert", statement));
	}

}

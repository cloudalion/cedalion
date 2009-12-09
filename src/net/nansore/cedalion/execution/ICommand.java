package net.nansore.cedalion.execution;

import net.nansore.prolog.PrologException;

public interface ICommand {

	void run(ExecutionContext executionContext) throws PrologException, TermInstantiationException, ExecutionContextException;

}

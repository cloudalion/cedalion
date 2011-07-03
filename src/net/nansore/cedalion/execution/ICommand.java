package net.nansore.cedalion.execution;

import net.nansore.prolog.PrologException;

/**
 * An implementation of this interface is a command in the context of procedural programming in Cedalion.
 * A command is an object that performs some action.  It is usually the interpretation of a procedure,
 * which is a Cedalion term.  The Cedalion program translates procedures into command terms, which
 * in turn are instantiated into objects (implementations of this interface), and get executed.
 */
public interface ICommand {

	/**
	 * Performs the command action
	 * @param executionContext the execution context holding whatever imperative variables are used by this command.
	 * @throws PrologException if some query to the Cedalion logic engine raised an exception
	 * @throws TermInstantiationException if an underlying command could not be instantiated
	 * @throws ExecutionContextException if something else went wrong
	 */
	void run(ExecutionContext executionContext) throws PrologException, TermInstantiationException, ExecutionContextException;

}

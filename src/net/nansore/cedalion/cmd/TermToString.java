package net.nansore.cedalion.cmd;

import java.util.Map;

import net.nansore.cedalion.execution.ExecutionContext;
import net.nansore.cedalion.execution.ExecutionContextException;
import net.nansore.cedalion.execution.ICommand;
import net.nansore.cedalion.execution.TermInstantiationException;
import net.nansore.prolog.Compound;
import net.nansore.prolog.PrologException;
import net.nansore.prolog.PrologProxy;
import net.nansore.prolog.Variable;

/**
 * Converts a (typed) term to a string.
 * Takes five arguments:
 * (1) The typed term to be converted
 * (2) the variable name bindings to be considered
 * (3) the depth above which we wish to trim the term, and replace the trimmed parts by numbered references
 * (4) the list of namespace aliases to consider
 * (5) a variable to which we wish to bind the string.
 */
public class TermToString implements ICommand {
	private Compound tterm;
	private Object varNames;
	private Object depth;
	private Object nsList;
	private Object resultRef;
	
	public TermToString(Compound term) {
		tterm = (Compound)term.arg(1);
		varNames = term.arg(2);
		depth = term.arg(3);
		nsList = term.arg(4);
		resultRef = term.arg(5);
	}
	public void run(ExecutionContext executionContext) throws PrologException,
			TermInstantiationException, ExecutionContextException {
		PrologProxy p = PrologProxy.instance();
		Variable resultVar = new Variable();
		if(!tterm.name().equals("::"))
			throw new ExecutionContextException("First argument to a TermToString command must be a typed term");
		Object term = tterm.arg(1);
		Map<Variable, Object> result = p.getSolution(Compound.createCompound("termToString", term, varNames, executionContext.evaluate(depth, new Variable()) , nsList, resultVar));
		executionContext.storeValue(resultRef, result.get(resultVar));
	}

}

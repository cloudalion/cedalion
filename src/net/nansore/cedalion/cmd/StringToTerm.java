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
 * Converts a string to a term.
 * Takes four arguments:
 * (1) the string to be converted.
 * (2) the list of namespace aliases to be considered when converting the string
 * (3) the typed term, a place-holder for the result.
 * (4) a variable to be bound to the list of variable name bindings.
 */
public class StringToTerm implements ICommand {
	
	private Object str;
	private Object nsList;
	private Variable tterm;
	private Variable varNames;

	public StringToTerm(Compound term) {
		str = term.arg(1);
		nsList = term.arg(2);
		tterm = (Variable) term.arg(3);
		varNames = (Variable) term.arg(4);
	}

	public void run(ExecutionContext executionContext) throws PrologException,
			TermInstantiationException, ExecutionContextException {
		Variable term = new Variable();
		Map<Variable, Object> solution = PrologProxy.instance().getSolution(Compound.createCompound("stringToTerm", executionContext.evaluate(str, new Variable()), nsList, term, varNames));
		tterm.bind(Compound.createCompound("::", solution.get(term), new Variable()));
		varNames.bind(solution.get(varNames));
	}
}

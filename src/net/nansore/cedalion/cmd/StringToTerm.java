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

public class StringToTerm implements ICommand {
	
	private Object str;
	private Object nsList;
	private Variable tterm;
	private Variable varNames;
	private PrologProxy prolog;

	public StringToTerm(Compound term) {
		str = term.arg(1);
		nsList = term.arg(2);
		tterm = (Variable) term.arg(3);
		varNames = (Variable) term.arg(4);
		prolog = term.getProlog();
	}

	public void run(ExecutionContext executionContext) throws PrologException,
			TermInstantiationException, ExecutionContextException {
		Variable term = new Variable();
		Map<Variable, Object> solution = prolog.getSolution(prolog.createCompound("stringToTerm", executionContext.evaluate(str, new Variable()), nsList, term, varNames));
		tterm.bind(prolog.createCompound("::", solution.get(term), new Variable()));
		varNames.bind(solution.get(varNames));
	}
}

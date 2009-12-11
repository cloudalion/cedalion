package net.nansore.cedalion.cmd;

import java.util.Map;

import sun.net.www.content.text.plain;
import net.nansore.cedalion.execution.ExecutionContext;
import net.nansore.cedalion.execution.ExecutionContextException;
import net.nansore.cedalion.execution.ICommand;
import net.nansore.cedalion.execution.TermInstantiationException;
import net.nansore.prolog.Compound;
import net.nansore.prolog.PrologException;
import net.nansore.prolog.PrologProxy;
import net.nansore.prolog.Variable;

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
		PrologProxy p = tterm.getProlog();
		Variable resultVar = new Variable();
		if(!tterm.name().equals("::"))
			throw new ExecutionContextException("First argument to a TermToString command must be a typed term");
		Object term = tterm.arg(1);
		Map<Variable, Object> result = p.getSolution(p.createCompound("termToString", term, varNames, executionContext.evaluate(depth, new Variable()) , nsList, resultVar));
		executionContext.storeValue(resultRef, result.get(resultVar));
	}

}

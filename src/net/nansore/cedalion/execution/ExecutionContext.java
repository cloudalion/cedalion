package net.nansore.cedalion.execution;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import net.nansore.prolog.Compound;
import net.nansore.prolog.NoSolutionsException;
import net.nansore.prolog.PrologException;
import net.nansore.prolog.PrologProxy;
import net.nansore.prolog.Variable;

public class ExecutionContext {

	private PrologProxy prolog;
	private List<Object> variables = new ArrayList<Object>();

	public ExecutionContext(PrologProxy p) {
		prolog = p;
	}

	public void runProcedure(Compound proc) throws PrologException, TermInstantiationException, ExecutionContextException {
		Variable command = new Variable("Command");
		Map<Variable, Object> result;
		try {
			result = prolog.getSolution(new Compound(prolog, "cpi#procedureCommand", proc, command));
		} catch (NoSolutionsException e) {
			throw new PrologException("Undefined procedure: " + proc);
		}
		try {
			runCommand((Compound)result.get(command));
		} catch (ClassCastException e) {
			throw new PrologException("Command returned from procedure " + proc + " is not a compound term");
		}
	}

	private void runCommand(Compound commandTerm) throws PrologException, TermInstantiationException, ExecutionContextException {
		ICommand command;
		try {
			command = (ICommand)  TermInstantiator.instance().instantiate(commandTerm);
		} catch (ClassCastException e) {
			throw new PrologException("Not a command: " + commandTerm);
		}
		command.run(this);
		
	}

	public void runFunction(Compound expression, Object result, Object type) throws PrologException, TermInstantiationException, ExecutionContextException {
		// Allocate a variable entry
		if(result instanceof Variable && !((Variable)result).isBound()) {
			int ref = variables.size();
			variables.add(prolog.createCompound("nil"));
			((Variable)result).bind(prolog.createCompound("ref", ref));
		}
		// Handle a const expression
		if(expression.name().equals("cpi#constExpr")) {
			storeValue(result, expression.arg(1));
		} else {
			// Execute the function
			runProcedure(prolog.createCompound("cpi#func", expression, result, type));
		}
	}

	public void storeValue(Object resultTarget, Object value) throws ExecutionContextException {
		int ref = getRef(resultTarget);
		variables.set(ref, value);
	}

	private int getRef(Object result) throws ExecutionContextException {
		if(result instanceof Variable) {
			result = ((Variable)result).boundTo();
		}
		if(!(result instanceof Compound)) {
			throw new ExecutionContextException("Bad reference " + result);
		}

		try {
			return (Integer) ((Compound)result).arg(1);
		} catch (ClassCastException e) {
			throw new ExecutionContextException("Bad reference " + result);
		}
	}

	public Object getValue(Object term) {
		Integer ref = (Integer) ((Compound)term).arg(1);
		return variables .get(ref);
	}

	public PrologProxy prolog() {
		return prolog;
	}

	public Object evaluate(Object expr, Object type) throws PrologException, TermInstantiationException, ExecutionContextException {
		Variable result = new Variable();
		if(expr instanceof Variable)
			expr = ((Variable)expr).boundTo();
		runFunction((Compound)expr, result, type);
		return getValue(result.boundTo());
	}

	public boolean isProcDefined(Compound procedure) throws PrologException {
		return prolog.hasSolution(prolog.createCompound("cpi#procedureCommand", procedure, new Variable()));
	}

}

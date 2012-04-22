package net.nansore.cedalion.execution;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import net.nansore.prolog.Compound;
import net.nansore.prolog.NoSolutionsException;
import net.nansore.prolog.PrologException;
import net.nansore.prolog.PrologProxy;
import net.nansore.prolog.Variable;


/**
 * An instance of this class provides the context for the execution of commands (see ICommand). 
 * It provides methods for running procedures, i.e., querying the Cedalion program for what command needs to run,
 * receive a command and then execute it.  For state, it holds the program's "memory", as a list of objects.
 * Each object has an index, and references provide access to memory cells.
 * This provides support for imperative variables, i.e., variables that change their values with time. 
 * @see ICommand
 */
public class ExecutionContext {

	private List<Object> variables = new ArrayList<Object>();

	/**
	 * Run a Cedalion procedure.
	 * @param proc the procedure term
	 * @throws PrologException if an exception was thrown when evaluating the procedure to a command
	 * @throws TermInstantiationException if the command could not be instantiated
	 * @throws ExecutionContextException if the execution of the command raised an exception.
	 */
	public void runProcedure(Compound proc) throws PrologException, TermInstantiationException, ExecutionContextException {
		Variable command = new Variable("Command");
		Map<Variable, Object> result;
		Object cmd = null;
		try {
			result = PrologProxy.instance().getSolution(new Compound("cpi#procedureCommand", proc, command));
		} catch (NoSolutionsException e) {
			throw new PrologException("Undefined procedure: " + proc);
		}
		cmd = result.get(command);
		if(!(cmd instanceof Compound)) {
			throw new PrologException("Command returned from procedure " + proc + " is not a compound term");
		}
		runCommand((Compound)cmd);
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

	/**
	 * Runs an imperative function, and stores the result to an imperative variable.
	 * @param expression the function to be evaluated
	 * @param result the reference to which the result should be stored
	 * @param type the type of the result
	 * @throws PrologException if an exception was thrown when evaluating the function to a command
	 * @throws TermInstantiationException if the command could not be instantiated
	 * @throws ExecutionContextException if the execution of the command raised an exception.
	 */
	public void runFunction(Compound expression, Object result, Object type) throws PrologException, TermInstantiationException, ExecutionContextException {
		// Allocate a variable entry
		if(result instanceof Variable && !((Variable)result).isBound()) {
			int ref = variables.size();
			variables.add(Compound.createCompound("nil"));
			((Variable)result).bind(Compound.createCompound("ref", ref));
		}
		// Handle a const expression
		if(expression.name().equals("cpi#constExpr")) {
			storeValue(result, expression.arg(1));
		} else {
			// Execute the function
			runProcedure(Compound.createCompound("cpi#func", expression, result, type));
		}
	}

	/**
	 * Store a value in an imperative variable
	 * @param resultTarget a reference to the variable to be modified
	 * @param value the value to store
	 * @throws ExecutionContextException if the reference is not valid
	 */
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

	/**
	 * Retrieves the value associated with a reference.
	 * @param term a reference term
	 * @return the associated value
	 */
	public Object getValue(Object term) {
		Integer ref = (Integer) ((Compound)term).arg(1);
		return variables .get(ref);
	}

	/**
	 * Evaluates an imperative expression and returns the returned value
	 * @param expr the expression to be evaluated
	 * @param type the expression type
	 * @return the returned value
	 * @throws PrologException if an exception was thrown when evaluating the function to a command
	 * @throws TermInstantiationException if the command could not be instantiated
	 * @throws ExecutionContextException if the execution of the command raised an exception.
	 */
	public Object evaluate(Object expr, Object type) throws PrologException, TermInstantiationException, ExecutionContextException {
		Variable result = new Variable();
		if(expr instanceof Variable)
			expr = ((Variable)expr).boundTo();
		runFunction((Compound)expr, result, type);
		return getValue(result.boundTo());
	}

	/**
	 * Returns whether a give procedure is defined
	 * @param procedure the procedure term to be checked
	 * @return true if defined
	 * @throws PrologException if an exception was raised when querying
	 */
	public boolean isProcDefined(Compound procedure) throws PrologException {
		return PrologProxy.instance().hasSolution(Compound.createCompound("cpi#procedureCommand", procedure, new Variable()));
	}

}

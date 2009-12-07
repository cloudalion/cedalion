package net.nansore.cedalion.execution;

import java.util.Map;

import net.nansore.prolog.Compound;
import net.nansore.prolog.NoSolutionsException;
import net.nansore.prolog.PrologException;
import net.nansore.prolog.PrologProxy;
import net.nansore.prolog.Variable;

public class ExecutionContext {

	private PrologProxy prolog;

	public ExecutionContext(PrologProxy p) {
		prolog = p;
	}

	public void runProcedure(Compound proc) throws PrologException, TermInstantiationException {
		Variable command = new Variable("Command");
		Map<Variable, Object> result;
		try {
			result = prolog.getSolution(new Compound(prolog, "cedalion#procedureCommand", proc, command));
		} catch (NoSolutionsException e) {
			throw new PrologException("Undefined procedure: " + proc);
		}
		try {
			runCommand((Compound)result.get(command));
		} catch (ClassCastException e) {
			throw new PrologException("Command returned from procedure " + proc + " is not a compound term");
		}
	}

	private void runCommand(Compound commandTerm) throws PrologException, TermInstantiationException {
		ICommand command;
		try {
			command = (ICommand)  TermInstantiator.instance().instantiate(commandTerm);
		} catch (ClassCastException e) {
			throw new PrologException("Not a command: " + commandTerm);
		}
		command.run(this);
		
	}

}

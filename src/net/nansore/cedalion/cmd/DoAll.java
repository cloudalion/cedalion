package net.nansore.cedalion.cmd;

import java.util.ArrayList;
import java.util.List;

import net.nansore.cedalion.execution.ExecutionContext;
import net.nansore.cedalion.execution.ExecutionContextException;
import net.nansore.cedalion.execution.ICommand;
import net.nansore.cedalion.execution.TermInstantiationException;
import net.nansore.cedalion.execution.TermInstantiator;
import net.nansore.prolog.Compound;
import net.nansore.prolog.PrologException;

/**
 * Performs all commands in the given list, sequentially.
 * Takes one argument: the list of commands to be executed.
 */
public class DoAll implements ICommand {
	private List<ICommand> commands = new ArrayList<ICommand>();
	public DoAll(Compound term) throws TermInstantiationException, PrologException {
		for(Compound list = (Compound)term.arg(1); !list.name().equals("[]"); list = (Compound)list.arg(2)) {
			commands.add((ICommand) TermInstantiator.instance().instantiate((Compound) list.arg(1)));
		}
	}

	public void run(ExecutionContext executionContext) throws PrologException, TermInstantiationException, ExecutionContextException {
		for(ICommand cmd : commands) {
			cmd.run(executionContext);
		}
	}

}

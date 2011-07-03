package net.nansore.cedalion.cmd;

import net.nansore.cedalion.execution.ExecutionContext;
import net.nansore.cedalion.execution.ExecutionContextException;
import net.nansore.cedalion.execution.ICommand;
import net.nansore.cedalion.execution.TermInstantiationException;
import net.nansore.prolog.Compound;
import net.nansore.prolog.PrologException;
import net.nansore.prolog.PrologProxy;

/**
 * Write a Cedalion file to the disk.
 * Takes two arguments: the name of the file, and its content, as provided by ReadFile.
 */
public class WriteFile implements ICommand {
	
	private String fileName;
	private Compound content;

	public WriteFile(Compound term) {
		fileName = (String) term.arg(1);
		content = (Compound) term.arg(2);
	}

	public void run(ExecutionContext executionContext) throws PrologException,
			TermInstantiationException, ExecutionContextException {
		PrologProxy p = PrologProxy.instance();
		p.getSolution(Compound.createCompound("writeFile", fileName, content));
	}

}

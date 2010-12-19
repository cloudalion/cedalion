package net.nansore.cedalion.cmd;

import net.nansore.cedalion.execution.ExecutionContext;
import net.nansore.cedalion.execution.ExecutionContextException;
import net.nansore.cedalion.execution.ICommand;
import net.nansore.cedalion.execution.TermInstantiationException;
import net.nansore.prolog.Compound;
import net.nansore.prolog.PrologException;
import net.nansore.prolog.PrologProxy;

public class WriteFile implements ICommand {
	
	private String fileName;
	private Compound content;

	public WriteFile(Compound term) {
		fileName = (String) term.arg(1);
		content = (Compound) term.arg(2);
	}

	public void run(ExecutionContext executionContext) throws PrologException,
			TermInstantiationException, ExecutionContextException {
		PrologProxy p = content.getProlog();
		p.getSolution(p.createCompound("writeFile", fileName, content));
	}

}

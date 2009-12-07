package net.nansore.cedalion.cmd;

import java.util.Map;

import net.nansore.cedalion.execution.ExecutionContext;
import net.nansore.cedalion.execution.ICommand;
import net.nansore.cedalion.execution.TermInstantiationException;
import net.nansore.prolog.Compound;
import net.nansore.prolog.PrologException;
import net.nansore.prolog.PrologProxy;
import net.nansore.prolog.Variable;

public class ReadFile implements ICommand {
	private String fileName;
	private String namespace;
	private Variable result;
	private PrologProxy prolog;

	public ReadFile(Compound term) {
		fileName = (String) term.arg(1);
		namespace = (String) term.arg(2);
		result = (Variable) term.arg(3);
		prolog = term.getProlog();
	}

	public void run(ExecutionContext executionContext) throws PrologException,
			TermInstantiationException {
		Variable fileContent = new Variable("FileContent");
		Map<Variable, Object> solution = prolog.getSolution(prolog.createCompound("readFile", fileName, namespace, fileContent));
		result.bind(solution.get(fileContent));
	}
}

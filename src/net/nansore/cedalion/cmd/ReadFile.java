package net.nansore.cedalion.cmd;

import java.util.Map;

import net.nansore.cedalion.execution.ExecutionContext;
import net.nansore.cedalion.execution.ICommand;
import net.nansore.cedalion.execution.TermInstantiationException;
import net.nansore.prolog.Compound;
import net.nansore.prolog.PrologException;
import net.nansore.prolog.PrologProxy;
import net.nansore.prolog.Variable;

/**
 * Reads a Cedalion file, and binds its content into a logic variable.
 * Takes three arguments: The file name, the namespace associated with the file, and the variable where the read
 * file content is to be bound.
 * The result of reading the file is a bultin#fileContent/2 structure, containing the list of statements in the file,
 * and the list of namespaces used in the file.
 * Each statement entry is of the builtin#statement/2 structure, containing the statement itself, and a list
 * of variable name bindings.
 * Variable name bindings use the builtin#varName/2 structure, containing the variable and the name.
 */
public class ReadFile implements ICommand {
	private String fileName;
	private String namespace;
	private Variable result;

	public ReadFile(Compound term) {
		fileName = (String) term.arg(1);
		namespace = (String) term.arg(2);
		result = (Variable) term.arg(3);
	}

	public void run(ExecutionContext executionContext) throws PrologException,
			TermInstantiationException {
		Variable fileContent = new Variable("FileContent");
		Map<Variable, Object> solution = PrologProxy.instance().getSolution(Compound.createCompound("readFile", fileName, namespace, fileContent));
		result.bind(solution.get(fileContent));
	}
}

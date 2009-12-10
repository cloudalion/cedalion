import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.Map;

import net.nansore.cedalion.execution.ExecutionContext;
import net.nansore.cedalion.execution.ExecutionContextException;
import net.nansore.cedalion.execution.TermInstantiationException;
import net.nansore.prolog.Compound;
import net.nansore.prolog.PrologException;
import net.nansore.prolog.PrologProxy;
import net.nansore.prolog.Variable;


public class Test {
	public static void main(String[] args) throws IOException, PrologException, TermInstantiationException, ExecutionContextException {
		PrologProxy p = new PrologProxy(new File("service.pl"));
		
		p.getSolution(new Compound(p, "loadFile", "procedure.ced", "cedalion"));
		
		ExecutionContext exe = new ExecutionContext(p);
		// Open file
		exe.runProcedure(new Compound(p, "cpi#openFile", "grammer-example.ced", "grammar", "gram"));
		
		// Print model
		Variable model = new Variable("Model");
		Variable x = new Variable("X");
		Variable y = new Variable("Y");
		Variable z = new Variable("Z");
		Iterator<Map<Variable, Object>> results = p.getSolutions(p.createCompound("cedalion#loadedFile", "grammar", z, model));
		while(results.hasNext()) {
			Map<Variable, Object> result = results.next();
			System.out.println(result.get(model));
		}

		// Print the third term
		Compound list = p.createCompound("[]");
		list = p.createCompound(".", 2, list);
		list = p.createCompound(".", 2, list);
		list = p.createCompound(".", 1, list);
		Compound path = p.createCompound("cpi#path", "grammar", list);
		Map<Variable, Object> result = p.getSolution(p.createCompound("cpi#termAtPath", path, x, y));
		System.out.println(result.get(x));
		System.out.println(result.get(y));

		// Modify the third statement
		exe.runProcedure(p.createCompound("cpi#setAtPath", path, p.createCompound("::", p.createCompound("[]"), new Variable()), p.createCompound("[]")));
		
		// Save file
		exe.runProcedure(p.createCompound("cpi#saveFile", "grammar", "g2.ced"));
		
		p.terminate();
	}
}

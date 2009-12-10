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
		exe.runProcedure(new Compound(p, "cbi#openFile", "grammer-example.ced", "grammar", "gram"));
		
		Variable model = new Variable("Model");
		Variable x = new Variable("X");
		Variable y = new Variable("Y");
		Variable z = new Variable("Z");
		Iterator<Map<Variable, Object>> results = p.getSolutions(p.createCompound("cedalion#loadedFile", "grammar", z, model, x, y));
		while(results.hasNext()) {
			Map<Variable, Object> result = results.next();
			System.out.println(result.get(model));
		}

		exe.runProcedure(p.createCompound("cbi#saveFile", "grammar", "g2.ced"));
		
		p.terminate();
	}
}

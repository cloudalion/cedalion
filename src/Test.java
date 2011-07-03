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
		PrologProxy.initialize("pl", new File("service.pl"));
		PrologProxy p = PrologProxy.instance();
		
		p.getSolution(new Compound("loadFile", "procedure.ced", "cedalion"));
		
		ExecutionContext exe = new ExecutionContext();
		// Open file
		exe.runProcedure(new Compound("cpi#openFile", "grammer-example.ced", "grammar", "gram"));
		
		// Print model
		Variable model = new Variable("Model");
		Variable x = new Variable("X");
		Variable y = new Variable("Y");
		Variable z = new Variable("Z");
		Iterator<Map<Variable, Object>> results = p.getSolutions(Compound.createCompound("cedalion#loadedFile", "grammar", z, model));
		while(results.hasNext()) {
			Map<Variable, Object> result = results.next();
			System.out.println(result.get(model));
		}

		// Print the third term
		Compound list = Compound.createCompound("[]");
		list = Compound.createCompound(".", 1, list);
		list = Compound.createCompound(".", 2, list);
		list = Compound.createCompound(".", 2, list);
		Compound path = Compound.createCompound("cpi#path", "grammar", list);
		Map<Variable, Object> result = p.getSolution(Compound.createCompound("cpi#termAtPath", path, x, y));
		System.out.println(result.get(x));
		System.out.println(result.get(y));

		// Save file
		exe.runProcedure(Compound.createCompound("cpi#saveFile", "grammar", "g1.ced"));

		// Modify the third statement and save
		exe.runProcedure(Compound.createCompound("cpi#edit", path, Compound.createCompound("::", Compound.createCompound("[]"), new Variable()), Compound.createCompound("[]")));
		exe.runProcedure(Compound.createCompound("cpi#saveFile", "grammar", "g2.ced"));
		
		// Undo the change and save
		exe.runProcedure(Compound.createCompound("cpi#undo", "grammar"));
		exe.runProcedure(Compound.createCompound("cpi#saveFile", "grammar", "g3.ced"));

		Variable vis = new Variable();
		result = p.getSolution(Compound.createCompound("cpi#visualizeDescriptor", Compound.createCompound("cpi#descriptor", path, Compound.createCompound("[]")), vis));
		System.out.println(result.get(vis));
		
		// Print third statement as a string
		String oldContent = (String)exe.evaluate(Compound.createCompound("cpi#termAsString", path, Compound.createCompound("cpi#constExpr", 3)), new Variable());
		System.out.println(oldContent);

		// Redo the change and save
		exe.runProcedure(Compound.createCompound("cpi#redo", "grammar"));
		exe.runProcedure(Compound.createCompound("cpi#saveFile", "grammar", "g4.ced"));
		
		// Restore the old content from the string we saved earlier, and save
		exe.runProcedure(Compound.createCompound("cpi#editFromString", path, Compound.createCompound("cpi#constExpr", "4")));
		exe.runProcedure(Compound.createCompound("cpi#saveFile", "grammar", "g5.ced"));
		
		// Test visualization
		result = p.getSolution(Compound.createCompound("cpi#visualizeDescriptor", Compound.createCompound("cpi#descriptor", path, Compound.createCompound("[]")), vis));
		System.out.println(result.get(vis));
		
		p.terminate();
	}
}

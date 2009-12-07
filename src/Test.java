import java.io.File;
import java.io.IOException;

import net.nansore.cedalion.execution.ExecutionContext;
import net.nansore.cedalion.execution.TermInstantiationException;
import net.nansore.prolog.Compound;
import net.nansore.prolog.PrologException;
import net.nansore.prolog.PrologProxy;


public class Test {
	public static void main(String[] args) throws IOException, PrologException, TermInstantiationException {
		PrologProxy p = new PrologProxy(new File("service.pl"));
		
		p.getSolution(new Compound(p, "loadFile", "procedure.ced", "cedalion"));
		
		ExecutionContext exe = new ExecutionContext(p);
		exe.runProcedure(new Compound(p, "cedalion#openFile", "grammer-example.pl", "grammar.ced", "gram"));
		
	}
}

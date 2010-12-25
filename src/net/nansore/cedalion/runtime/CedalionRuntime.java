package net.nansore.cedalion.runtime;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import net.nansore.cedalion.execution.ExecutionContext;
import net.nansore.cedalion.execution.ExecutionContextException;
import net.nansore.cedalion.execution.TermInstantiationException;
import net.nansore.prolog.Compound;
import net.nansore.prolog.PrologException;
import net.nansore.prolog.PrologProxy;

public class CedalionRuntime {
	private ExecutionContext exe;
	private PrologProxy prolog;

	public CedalionRuntime(String swiProlog, String programFile) throws IOException, PrologException {
		URL url =  ClassLoader.getSystemResource("service.pl");
		prolog = new PrologProxy(swiProlog, new File(url.getFile()));
		
		prolog.getSolution(new Compound(prolog, "loadFile", programFile, "<no namespace>"));
		
		exe = new ExecutionContext(prolog);

	}
	
	public void runProcedure(Compound proc) throws PrologException, TermInstantiationException, ExecutionContextException {
		exe.runProcedure(proc);
	}
	
	public static void main(String[] args) throws IOException, PrologException {
		CedalionRuntime runtime = new CedalionRuntime("/usr/bin/swipl", "/home/boaz/export.car");
	}
}

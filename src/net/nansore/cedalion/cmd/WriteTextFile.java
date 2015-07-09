package net.nansore.cedalion.cmd;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;

import net.nansore.cedalion.execution.ExecutionContext;
import net.nansore.cedalion.execution.ExecutionContextException;
import net.nansore.cedalion.execution.ICommand;
import net.nansore.cedalion.execution.TermInstantiationException;
import net.nansore.prolog.Compound;
import net.nansore.prolog.PrologException;

public class WriteTextFile implements ICommand{
	
	private String fileName;
	private Compound content;

	public WriteTextFile(Compound term) {
		fileName = (String) term.arg(1);
		content = (Compound) term.arg(2);
	}
	
	@Override
	public void run(ExecutionContext executionContext) throws PrologException,
			TermInstantiationException, ExecutionContextException {
		try {
			FileOutputStream output = new FileOutputStream(fileName);
			Writer writer = new OutputStreamWriter(output);
			Compound l = content;
			while(l.name().equals(".")) {
				writer.write((String) l.arg(1) + "\n");
				l = (Compound)l.arg(2);
			}
			writer.close();
			output.close();
		} catch (IOException e) {
			throw new ExecutionContextException(e);
		}
		
	}

}

package net.nansore.cedalion.cmd;

import net.nansore.cedalion.eclipse.Activator;
import net.nansore.cedalion.eclipse.CedalionView;
import net.nansore.cedalion.execution.ExecutionContext;
import net.nansore.cedalion.execution.ExecutionContextException;
import net.nansore.cedalion.execution.ICommand;
import net.nansore.cedalion.execution.TermInstantiationException;
import net.nansore.prolog.Compound;
import net.nansore.prolog.PrologException;

import org.eclipse.ui.PartInitException;

public class ShowView implements ICommand {
	private Compound visualization;

	public ShowView(Compound term) {
		visualization = (Compound)term.arg(1);
	}

	@Override
	public void run(ExecutionContext executionContext) throws PrologException,
			TermInstantiationException, ExecutionContextException {
		try {
			CedalionView view = Activator.getDefault().openView();
			view.setVisualization(visualization);
		} catch (PartInitException e) {
			throw new ExecutionContextException(e);
		}
	}

}

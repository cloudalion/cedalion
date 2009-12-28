package net.nansore.cedalion.figures;

import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.MouseEvent;
import org.eclipse.draw2d.MouseListener;

import net.nansore.cedalion.eclipse.TermContext;
import net.nansore.cedalion.eclipse.TermVisualizationException;
import net.nansore.cedalion.execution.ExecutionContext;
import net.nansore.cedalion.execution.ExecutionContextException;
import net.nansore.cedalion.execution.TermInstantiationException;
import net.nansore.cedalion.execution.TermInstantiator;
import net.nansore.prolog.Compound;
import net.nansore.prolog.PrologException;

public class ActionFigure extends TermContextProxy {

	private Compound proc;

	public ActionFigure(Compound term, TermContext parent) throws TermVisualizationException, TermInstantiationException, PrologException {
		super(parent);
		try {
			TermFigure child = (TermFigure)TermInstantiator.instance().instantiate((Compound)term.arg(1), this);
			add(child);
		} catch (ClassCastException e) {
			throw new TermVisualizationException(e);
		}
		proc = (Compound)term.arg(2);
	}

	@Override
	public void dispose() {
		// Nothing to do...
	}

	@Override
	public void bindFigure(TermFigure figure) {
		super.bindFigure(figure);
		figure.addMouseListener(new MouseListener() {
			
			@Override
			public void mouseReleased(MouseEvent me) {
			}
			
			@Override
			public void mousePressed(MouseEvent me) {
			}
			
			@Override
			public void mouseDoubleClicked(MouseEvent me) {
				ExecutionContext exe = new ExecutionContext(proc.getProlog());
				try {
					exe.runProcedure(proc);
				} catch (PrologException e) {
					e.printStackTrace();
				} catch (TermInstantiationException e) {
					e.printStackTrace();
				} catch (ExecutionContextException e) {
					e.printStackTrace();
				}
			}
		});
	}

}

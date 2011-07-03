package net.nansore.cedalion.figures;

import net.nansore.cedalion.eclipse.TermContext;
import net.nansore.cedalion.eclipse.TermVisualizationException;
import net.nansore.cedalion.execution.ExecutionContext;
import net.nansore.cedalion.execution.ExecutionContextException;
import net.nansore.cedalion.execution.TermInstantiationException;
import net.nansore.cedalion.execution.TermInstantiator;
import net.nansore.prolog.Compound;
import net.nansore.prolog.PrologException;
import net.nansore.prolog.PrologProxy;

import org.eclipse.draw2d.MouseEvent;
import org.eclipse.draw2d.MouseListener;

/**
 * A figure, that when double-clicked, performs a procedure associated with it.  The term is expected to have 2 or 3 arguments:
 * 1. The child figure to display.
 * 2. The procedure to perform when the action occurs
 * 3. (optional) A goal that decides if the action should be performed automatically, as the visuals are displayed.
 */
public class ActionFigure extends TermContextProxy {

	private Compound proc;
	private TermFigure child;
	private boolean automatic = false;

	public ActionFigure(Compound term, TermContext parent) throws TermVisualizationException, TermInstantiationException, PrologException {
		super(parent);
		try {
			child = (TermFigure)TermInstantiator.instance().instantiate((Compound)term.arg(1), this);
			add(child);
		} catch (ClassCastException e) {
			throw new TermVisualizationException(e);
		}
		proc = (Compound)term.arg(2);
		if(term.arity() >= 3) {
			automatic  = PrologProxy.instance().hasSolution((Compound)term.arg(3));
		}
	}

	@Override
	public void validate() {
		super.validate();
		if(automatic) {
			getCanvas().getDisplay().asyncExec(new Runnable() {
				
				@Override
				public void run() {
					performDefaultAction();
				}
			});			
		}
	}

	@Override
	public void dispose() {
		child.dispose();
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
				performDefaultAction();
			}
		});
	}

	@Override
	public void performDefaultAction() {
		ExecutionContext exe = new ExecutionContext();
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
	

}

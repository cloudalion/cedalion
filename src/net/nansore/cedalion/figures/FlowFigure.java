package net.nansore.cedalion.figures;

import java.util.ArrayList;
import java.util.List;

import net.nansore.cedalion.eclipse.TermContext;
import net.nansore.cedalion.eclipse.TermVisualizationException;
import net.nansore.cedalion.execution.TermInstantiationException;
import net.nansore.cedalion.execution.TermInstantiator;
import net.nansore.prolog.Compound;
import net.nansore.prolog.PrologException;

import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.Panel;

/**
 * This is a base class for "container" figures.
 * Derived classes need to implement setLayout(), to set the layout manager as needed.
 * Expects at least one argument: a list of figures to be owned.
 */
public abstract class FlowFigure extends Panel implements TermFigure {

	protected List<TermFigure> childFigures = new ArrayList<TermFigure>();

	protected abstract void setLayout(Compound term);

	public FlowFigure(Compound term, final TermContext context) throws TermInstantiationException, PrologException {
		init(term, context);
	}

	protected void init(Compound term, final TermContext context)
			throws TermInstantiationException, PrologException {			    
			    context.bindFigure(this);
			    Object list = term.arg(1);
			    while(list instanceof Compound && ((Compound)list).arity() == 2) {
			        Compound compound = ((Compound)list);
			        Object figure = TermInstantiator.instance().instantiate((Compound)compound.arg(1), context);
			        childFigures.add((TermFigure)figure);
			        list = compound.arg(2);
			    }
			    
				setLayout(term);
				for(TermFigure figure : childFigures)
					add((IFigure) figure);
			}

	public void updateFigure() throws TermVisualizationException {
		// Nothing to do
	}

	public void dispose() {
		for(TermFigure figure : childFigures)
			figure.dispose();
	}

}
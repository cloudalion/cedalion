package net.nansore.visualterm.figures;

import net.nansore.cedalion.eclipse.TermContext;
import net.nansore.cedalion.eclipse.TermVisualizationException;
import net.nansore.cedalion.execution.TermInstantiationException;
import net.nansore.cedalion.execution.TermInstantiator;
import net.nansore.prolog.Compound;
import net.nansore.prolog.PrologException;

import org.eclipse.draw2d.FlowLayout;
import org.eclipse.draw2d.IFigure;

public class tooltip extends TermContextProxy {

	public tooltip(Compound term, TermContext parent) throws TermVisualizationException, TermInstantiationException, PrologException {
		super(parent);
        setLayoutManager(new FlowLayout());
        add((IFigure) TermInstantiator.instance().instantiate((Compound)term.arg(1), this));
        setToolTip((IFigure) TermInstantiator.instance().instantiate((Compound)term.arg(2), this));
	}

	public void dispose() {
		// TODO Auto-generated method stub

	}

}

package net.nansore.visualterm.figures;

import net.nansore.cedalion.eclipse.TermContext;
import net.nansore.cedalion.eclipse.TermVisualizationException;
import net.nansore.cedalion.execution.TermInstantiationException;
import net.nansore.cedalion.execution.TermInstantiator;
import net.nansore.prolog.Compound;
import net.nansore.prolog.PrologException;

import org.eclipse.draw2d.FlowLayout;
import org.eclipse.draw2d.IFigure;

public class background extends TermContextProxy {

	public background(Compound term, TermContext parent) throws TermVisualizationException, TermInstantiationException, PrologException {
		super(parent);
        setLayoutManager(new FlowLayout());
        setBackgroundColor(color.createColor(term.arg(2), this));
        add((IFigure) TermInstantiator.instance().instantiate(term.arg(1), this));
	}

	public void dispose() {
		// TODO Auto-generated method stub

	}

}

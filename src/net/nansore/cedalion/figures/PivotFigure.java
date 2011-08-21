package net.nansore.cedalion.figures;

import net.nansore.cedalion.eclipse.TermContext;
import net.nansore.cedalion.execution.TermInstantiationException;
import net.nansore.cedalion.execution.TermInstantiator;
import net.nansore.cedalion.helpers.Pivot;
import net.nansore.prolog.Compound;
import net.nansore.prolog.PrologException;

public class PivotFigure extends TermContextProxy implements Pivot {

	private TermFigure figure;

	public PivotFigure(Compound term, TermContext parent) throws TermInstantiationException, PrologException {
		super(parent);
        figure = (TermFigure) TermInstantiator.instance().instantiate(term.arg(1), this);
        add(figure);
	}

	@Override
	public void dispose() {
		figure.dispose();
	}

}

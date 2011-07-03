package net.nansore.cedalion.figures;

import net.nansore.cedalion.eclipse.TermContext;
import net.nansore.cedalion.eclipse.TermVisualizationException;
import net.nansore.cedalion.execution.TermInstantiationException;
import net.nansore.prolog.Compound;
import net.nansore.prolog.PrologException;

import org.eclipse.draw2d.Border;
import org.eclipse.draw2d.SimpleLoweredBorder;

/**
 * A BorderFigure that displays a lowered border around the contents.
 * Takes no additional arguments.
 */
public class Lowered extends BorderFigure {

	public Lowered(Compound term, TermContext parent)
			throws TermVisualizationException, TermInstantiationException,
			PrologException {
		super(term, parent);
	}

	@Override
	protected Border createBorder(Compound term, TermContext context)
			throws TermVisualizationException {
		return new SimpleLoweredBorder();
	}

}

package net.nansore.cedalion.figures;

import net.nansore.cedalion.eclipse.TermContext;
import net.nansore.cedalion.eclipse.TermVisualizationException;
import net.nansore.cedalion.execution.TermInstantiationException;
import net.nansore.prolog.Compound;
import net.nansore.prolog.PrologException;

import org.eclipse.draw2d.Border;
import org.eclipse.draw2d.SimpleRaisedBorder;

public class Raised extends BorderFigure {

	public Raised(Compound term, TermContext parent) throws TermVisualizationException, TermInstantiationException, PrologException {
		super(term, parent);
		
	}

    protected Border createBorder(Compound term, TermContext context) throws TermVisualizationException {
    	return new SimpleRaisedBorder();
    }

}

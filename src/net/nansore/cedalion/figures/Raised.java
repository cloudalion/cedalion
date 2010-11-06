package net.nansore.cedalion.figures;

import org.eclipse.draw2d.Border;
import org.eclipse.draw2d.LineBorder;
import org.eclipse.draw2d.SimpleRaisedBorder;
import org.eclipse.swt.graphics.Color;

import net.nansore.cedalion.eclipse.TermContext;
import net.nansore.cedalion.eclipse.TermVisualizationException;
import net.nansore.cedalion.execution.TermInstantiationException;
import net.nansore.prolog.Compound;
import net.nansore.prolog.PrologException;

public class Raised extends BorderFigure {

	public Raised(Compound term, TermContext parent) throws TermVisualizationException, TermInstantiationException, PrologException {
		super(term, parent);
		
	}

	@Override
	public void dispose() {
		// TODO Auto-generated method stub
		
	}
    protected Border createBorder(Compound term, TermContext context) throws TermVisualizationException {
    	return new SimpleRaisedBorder();
    }

}

package net.nansore.cedalion.figures;

import net.nansore.cedalion.eclipse.TermContext;
import net.nansore.cedalion.eclipse.TermVisualizationException;
import net.nansore.cedalion.execution.TermInstantiationException;
import net.nansore.cedalion.execution.TermInstantiator;
import net.nansore.prolog.Compound;
import net.nansore.prolog.PrologException;

import org.eclipse.draw2d.FlowLayout;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.RoundedRectangle;
import org.eclipse.draw2d.geometry.Dimension;

public class RoundRect extends TermContextProxy {

	public RoundRect(Compound term, TermContext parent) throws TermVisualizationException, TermInstantiationException, PrologException {
		super(parent);
        setLayoutManager(new FlowLayout());
        RoundedRectangle roundRect = new RoundedRectangle();
        roundRect.setCornerDimensions(new Dimension(20, 20));
        roundRect.setOutline(true);
        roundRect.setLineWidth(5);
        roundRect.setFill(true);
        roundRect.setLayoutManager(new FlowLayout());
        roundRect.setBackgroundColor(TextColor.createColor(term.arg(2), this));
        roundRect.add((IFigure) TermInstantiator.instance().instantiate(term.arg(1), this));
        add(roundRect);
	}

	@Override
	public void dispose() {
		// TODO Auto-generated method stub

	}

}

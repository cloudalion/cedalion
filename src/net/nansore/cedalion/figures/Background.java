package net.nansore.cedalion.figures;

import net.nansore.cedalion.eclipse.TermContext;
import net.nansore.cedalion.eclipse.TermVisualizationException;
import net.nansore.cedalion.execution.TermInstantiationException;
import net.nansore.cedalion.execution.TermInstantiator;
import net.nansore.prolog.Compound;
import net.nansore.prolog.PrologException;

import org.eclipse.draw2d.FlowLayout;

/**
 * This figure displays a solid-colored background behind its contents
 * The associtated term has 2 arguments:
 * 1. The contents (figure)
 * 2. The color (RGB) to be used as background 
 */
public class Background extends TermContextProxy {

	private TermFigure figure;

	public Background(Compound term, TermContext parent) throws TermVisualizationException, TermInstantiationException, PrologException {
		super(parent);
        setLayoutManager(new FlowLayout());
        setBackgroundColor(TextColor.createColor(term.arg(2), this));
        figure = (TermFigure) TermInstantiator.instance().instantiate(term.arg(1), this);
		add(figure);
	}

	public void dispose() {
		figure.dispose();
	}

}

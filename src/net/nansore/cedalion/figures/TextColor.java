/*
 * Created on Jan 26, 2006
 */
package net.nansore.cedalion.figures;

import net.nansore.cedalion.eclipse.TermContext;
import net.nansore.cedalion.eclipse.TermVisualizationException;
import net.nansore.cedalion.execution.TermInstantiationException;
import net.nansore.cedalion.execution.TermInstantiator;
import net.nansore.prolog.Compound;
import net.nansore.prolog.PrologException;

import org.eclipse.draw2d.IFigure;
import org.eclipse.swt.graphics.Color;

/**
 * Modifies the color of the text in the contents.
 * Takes 2 arguments:
 * 1. the contents
 * 2. the color (RGB) in which we wish to color the text.
 */
public class TextColor extends TermContextProxy {
    
    private Color color;
	private TermFigure figure;

    public TextColor(Compound term, TermContext context) throws TermVisualizationException, TermInstantiationException, PrologException {
        super(context);
        try {
        	color = createColor(term.arg(2), context);
            figure = (TermFigure) TermInstantiator.instance().instantiate((Compound)term.arg(1), this);
			add((IFigure) figure);
        } catch (ClassCastException e) {
            throw new TermVisualizationException("A color figure should get a compound term and a color term");
        }
    }
    
    public static Color createColor(Object colorDescriptor, TermContext context) throws TermVisualizationException {
    	if(colorDescriptor instanceof Compound && ((Compound)colorDescriptor).name().equals("cpi#rgb")) {
    		Compound term = (Compound)colorDescriptor;
    		Number red = (Number)term.arg(1);
            Number green = (Number)term.arg(2);
            Number blue = (Number)term.arg(3);
            return new Color(context.getTextEditor().getDisplay(), red.intValue(), green.intValue(), blue.intValue());
    	}
    	return new Color(context.getTextEditor().getDisplay(), 128, 128, 128);
	}

	public Color getColor() {
        return color;
    }

    /* (non-Javadoc)
     * @see net.nansore.visualterm.figures.TermFigure#updateFigure(int)
     */
    public void updateFigure() throws TermVisualizationException {
    }

    /* (non-Javadoc)
     * @see net.nansore.visualterm.Disposable#dispose()
     */
    public void dispose() {
    	figure.dispose();
        color.dispose();
    }
}

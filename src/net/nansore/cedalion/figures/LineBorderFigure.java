/*
 * Created on Jul 24, 2006
 */
package net.nansore.cedalion.figures;


import net.nansore.cedalion.eclipse.TermContext;
import net.nansore.cedalion.eclipse.TermVisualizationException;
import net.nansore.cedalion.execution.TermInstantiationException;
import net.nansore.prolog.Compound;
import net.nansore.prolog.PrologException;

import org.eclipse.draw2d.Border;
import org.eclipse.draw2d.LineBorder;
import org.eclipse.swt.graphics.Color;

/**
 * A BorderFigure, that displays a line border around the contents.  Takes 2 in addition to the contents:
 * 2. The width of the line
 * 3. The color (RGB) of the line
 */
public class LineBorderFigure extends BorderFigure {

    public LineBorderFigure(Compound term, TermContext parent) throws TermVisualizationException, TermInstantiationException, PrologException {
        super(term, parent);
    }

    protected Border createBorder(Compound term, TermContext context) throws TermVisualizationException {
        try {
            int width = ((Integer) term.arg(2)).intValue();
            Color clr = TextColor.createColor(term.arg(3), context);
            return new LineBorder(clr, width);
        } catch (RuntimeException e) {
            throw new TermVisualizationException("A lineBorder element must have 2 arguments: the content of the border and the width");
        }        
    }

}

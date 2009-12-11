/*
 * Created on Jan 19, 2006
 */
package net.nansore.visualterm.figures;

import net.nansore.cedalion.eclipse.TermContext;
import net.nansore.cedalion.eclipse.TermVisualizationException;
import net.nansore.prolog.Compound;

import org.eclipse.draw2d.Label;

/**
 * @author boaz
 */
public class label extends Label implements TermFigure {

    public label(Compound term, TermContext context) {
        setForegroundColor(context.getColor());
        setFont(context.getFont(TermContext.NORMAL_FONT));
        setText(translateString(term));
    }

	private String translateString(Compound term) {
		return (String)term.arg(1);
	}

    /* (non-Javadoc)
     * @see net.nansore.visualterm.figures.TermFigure#updateFigure()
     */
    public void updateFigure() throws TermVisualizationException {
    }

    /* (non-Javadoc)
     * @see net.nansore.visualterm.figures.TermFigure#dispose()
     */
    public void dispose() {
        // TODO Auto-generated method stub
        
    }
    
    
}

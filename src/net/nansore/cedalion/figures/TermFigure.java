/*
 * Created on Jan 19, 2006
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package net.nansore.cedalion.figures;

import net.nansore.cedalion.eclipse.TermVisualizationException;
import net.nansore.cedalion.execution.TermInstantiationException;

import org.eclipse.draw2d.IFigure;

/**
 * A common interface for all term figures, that is, figures that represent Cedalion terms. 
 */
public interface TermFigure extends IFigure {

    /**
     * Causes the figure to update itself
     * @throws TermVisualizationException
     * @throws net.nansore.cedalion.eclipse.TermVisualizationException 
     * @throws TermInstantiationException 
     */
    void updateFigure() throws TermVisualizationException, TermInstantiationException;

    /**
     * Dispose of this figure and the resources it occupies
     */
    void dispose();

}

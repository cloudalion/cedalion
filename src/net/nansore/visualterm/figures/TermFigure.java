/*
 * Created on Jan 19, 2006
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package net.nansore.visualterm.figures;

import net.nansore.cedalion.eclipse.TermVisualizationException;
import net.nansore.cedalion.execution.TermInstantiationException;

import org.eclipse.draw2d.IFigure;

/**
 * @author boaz
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
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
     * 
     */
    void dispose();
    
}

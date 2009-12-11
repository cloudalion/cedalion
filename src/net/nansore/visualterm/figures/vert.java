/*
 * Created on Jan 20, 2006
 */
package net.nansore.visualterm.figures;

import net.nansore.cedalion.eclipse.TermContext;
import net.nansore.cedalion.eclipse.TermVisualizationException;
import net.nansore.cedalion.execution.TermInstantiationException;
import net.nansore.prolog.Compound;
import net.nansore.prolog.PrologException;

import org.eclipse.draw2d.FlowLayout;

/**
 * @author boaz
 */
public class vert extends horiz {

    /**
     * @param term
     * @param context
     * @throws TermVisualizationException
     * @throws PrologException 
     * @throws TermInstantiationException 
     */
    public vert(Compound term, TermContext context) throws TermVisualizationException, TermInstantiationException, PrologException {
        super(term, context);
        // TODO Auto-generated constructor stub
    }
    
    protected void setLayout() {
        setLayoutManager(new FlowLayout(false));
    }

}

/*
 * Created on Jan 19, 2006
 */
package net.nansore.cedalion.figures;

import java.util.ArrayList;
import java.util.List;

import net.nansore.cedalion.eclipse.TermContext;
import net.nansore.cedalion.eclipse.TermVisualizationException;
import net.nansore.cedalion.execution.TermInstantiationException;
import net.nansore.cedalion.execution.TermInstantiator;
import net.nansore.prolog.Compound;
import net.nansore.prolog.PrologException;

import org.eclipse.draw2d.FlowLayout;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.MouseEvent;
import org.eclipse.draw2d.MouseListener;
import org.eclipse.draw2d.Panel;

/**
 * @author boaz
 */
public class HorizontalFlow extends Panel implements TermFigure {
	private List<TermFigure> childFigures = new ArrayList<TermFigure>();

    public HorizontalFlow(Compound term, final TermContext context) throws TermVisualizationException, TermInstantiationException, PrologException {
        setLayout();
        
        context.bindFigure(this);
        Object list = term.arg(1);
        while(list instanceof Compound && ((Compound)list).arity() == 2) {
            Compound compound = ((Compound)list);
            Object figure = TermInstantiator.instance().instantiate((Compound)compound.arg(1), context);
			add((IFigure) figure);
            childFigures.add((TermFigure)figure);
            list = compound.arg(2);
        }
        
        /// Testing
        addMouseListener(new MouseListener() {

			public void mouseDoubleClicked(MouseEvent arg0) {
			}

			public void mousePressed(MouseEvent arg0) {
				// TODO Auto-generated method stub
				
			}

			public void mouseReleased(MouseEvent arg0) {
				// TODO Auto-generated method stub
				
			}});
    }

    /**
     * 
     */
    protected void setLayout() {
        setLayoutManager(new FlowLayout());
    }

    /* (non-Javadoc)
     * @see net.nansore.visualterm.figures.TermFigure#updateFigure()
     */
    public void updateFigure() throws TermVisualizationException {
    	// Nothing to do
    }

    /* (non-Javadoc)
     * @see net.nansore.visualterm.figures.TermFigure#dispose()
     */
    public void dispose() {
    	for(TermFigure figure : childFigures)
    		figure.dispose();
    }
}

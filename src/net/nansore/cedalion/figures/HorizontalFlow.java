/*
 * Created on Jan 19, 2006
 */
package net.nansore.cedalion.figures;


import net.nansore.cedalion.eclipse.TermContext;
import net.nansore.cedalion.eclipse.TermVisualizationException;
import net.nansore.cedalion.execution.TermInstantiationException;
import net.nansore.prolog.Compound;
import net.nansore.prolog.PrologException;

import org.eclipse.draw2d.FlowLayout;
import org.eclipse.draw2d.MouseEvent;
import org.eclipse.draw2d.MouseListener;

/**
 * @author boaz
 */
public class HorizontalFlow extends FlowFigure {
	public HorizontalFlow(Compound term, final TermContext context) throws TermVisualizationException, TermInstantiationException, PrologException {
        super(term, context);
    }

	/**
     * 
     */
    @Override
	protected void setLayout() {
        FlowLayout flowLayout = new FlowLayout();
//        flowLayout.setMinorAlignment(FlowLayout.ALIGN_CENTER);
		setLayoutManager(flowLayout);
    }
}

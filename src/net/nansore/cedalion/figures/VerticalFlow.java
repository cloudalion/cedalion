/*
 * Created on Jan 20, 2006
 */
package net.nansore.cedalion.figures;

import net.nansore.cedalion.eclipse.TermContext;
import net.nansore.cedalion.eclipse.TermVisualizationException;
import net.nansore.cedalion.execution.TermInstantiationException;
import net.nansore.cedalion.helpers.HasPivotOffset;
import net.nansore.cedalion.helpers.Pivot;
import net.nansore.cedalion.helpers.PivotHorizontalLayout;
import net.nansore.prolog.Compound;
import net.nansore.prolog.PrologException;

import org.eclipse.draw2d.FlowLayout;

/**
 * A FlowFigure that lays out its contents vertically.  Takes no extra arguments.
 */
public class VerticalFlow extends FlowFigure implements HasPivotOffset {

    private FlowLayout layout;

	/**
     * @param term
     * @param context
     * @throws TermVisualizationException
     * @throws PrologException 
     * @throws TermInstantiationException 
     */
    public VerticalFlow(Compound term, TermContext context) throws TermVisualizationException, TermInstantiationException, PrologException {
        super(term, context);
        // TODO Auto-generated constructor stub
    }
    
    protected void setLayout(Compound term) {
        layout = new FlowLayout(false);
		setLayoutManager(layout);
    }

	@Override
	public int getPivotOffset() {
		int totalOffset = 0;
		if(childFigures.size() > 0) {
			// First, search for a pivot.
			for(TermFigure child : childFigures) {
				if(child instanceof Pivot) {
					return PivotHorizontalLayout.getChildPivotOffset(child) + totalOffset;
				}
				totalOffset += child.getPreferredSize().height + layout.getMajorSpacing();
			}
			return PivotHorizontalLayout.getChildPivotOffset(childFigures.get(0));			
		}
		else
			return 0;
	}

}

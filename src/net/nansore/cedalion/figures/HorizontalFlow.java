/*
 * Created on Jan 19, 2006
 */
package net.nansore.cedalion.figures;


import net.nansore.cedalion.eclipse.TermContext;
import net.nansore.cedalion.eclipse.TermVisualizationException;
import net.nansore.cedalion.execution.TermInstantiationException;
import net.nansore.cedalion.helpers.HasPivotOffset;
import net.nansore.cedalion.helpers.PivotHorizontalLayout;
import net.nansore.prolog.Compound;
import net.nansore.prolog.PrologException;

import org.eclipse.draw2d.FlowLayout;

/**
 * A FlowFigure that lays out its contents horizontally.  Takes no extra arguments.
 */
public class HorizontalFlow extends FlowFigure implements HasPivotOffset {
	public HorizontalFlow(Compound term, final TermContext context) throws TermVisualizationException, TermInstantiationException, PrologException {
        super(term, context);
    }
	
	private boolean alignmentSet;
	private int minorAlignment;

	/**
     * 
     */
    @Override
	protected void setLayout(Compound term) {
    	minorAlignment = FlowLayout.ALIGN_LEFTTOP;
    	alignmentSet = false;
        if(term.arity() > 1) {
        	if(term.arg(2).toString().equals("cpi#middle")) {
        		minorAlignment = FlowLayout.ALIGN_CENTER;
        	} else if(term.arg(2).toString().equals("cpi#bottom")) {
        		minorAlignment = FlowLayout.ALIGN_RIGHTBOTTOM;
        	}
        	alignmentSet = true;
        } else {
        	/*// Try to figure out the alignment based on a child with alignment
        	for(TermFigure child : childFigures) {
        		if(!(child instanceof HorizontalFlow))
        			continue;
        		HorizontalFlow horizontalFlowChild = (HorizontalFlow)child;
				if(horizontalFlowChild.isAlignmentSet()) {
        			minorAlignment = horizontalFlowChild.getAlignment();
        			alignmentSet = true;
        			break;
        		}
        	}*/
        	setLayoutManager(new PivotHorizontalLayout());
        	return;
        }
        FlowLayout flowLayout = new FlowLayout();
        flowLayout.setMinorAlignment(minorAlignment);
		setLayoutManager(flowLayout);
    }
    
    public boolean isAlignmentSet() {
    	return alignmentSet;
    }
    
    public int getAlignment() {
    	return minorAlignment;
    }

	@Override
	public int getPivotOffset() {
		if(getLayoutManager() instanceof PivotHorizontalLayout) {
			return ((PivotHorizontalLayout)getLayoutManager()).getPivotOffset(this);
		} else {
			// Find a child that has the same preferred size as the container.  This will be the pivot (if found)
			int containerSize = getPreferredSize().height;
			for(TermFigure child : childFigures) {
				if(child.getPreferredSize().height == containerSize) {
					return PivotHorizontalLayout.getChildPivotOffset(child);
				}
			}
			return getPreferredSize().height/2;
		}
	}
}

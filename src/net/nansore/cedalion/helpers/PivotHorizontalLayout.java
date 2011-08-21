package net.nansore.cedalion.helpers;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.draw2d.AbstractLayout;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Rectangle;

public class PivotHorizontalLayout extends AbstractLayout {

	@Override
	/**
	 * Layout the objects next to each other, so that their "pivot" height is always at the same Y coordinate.
	 * we start by making this point at 0, and then we move all children by the maximum value.
	 */
	public void layout(IFigure container) {
		doLayout(container, true, new Rectangle());
	}

	private int doLayout(IFigure container, boolean applyChange, Rectangle containerBounds) {
		int x = container.getBounds().x;
		int y = container.getBounds().y;
		
		int maxOffset = 0;
		List<Rectangle> childrenBounds = new ArrayList<Rectangle>();
		@SuppressWarnings("rawtypes")
		List children = container.getChildren();
		for(int i = 0; i < children.size(); i++) {
			IFigure child = (IFigure) children.get(i);
			Dimension prefSize = child.getPreferredSize();
			int pivotOffset = getChildPivotOffset(child);
			childrenBounds.add(new Rectangle(x, y - pivotOffset, prefSize.width, prefSize.height));
			x += prefSize.width + 5;
			maxOffset = Math.max(maxOffset, pivotOffset);
		}
		
		for(int i = 0; i < children.size(); i++) {
			childrenBounds.get(i).y += maxOffset;
			IFigure child = (IFigure) children.get(i);
			if(applyChange)
				child.setBounds(childrenBounds.get(i));
			if(i == 0)
				containerBounds.setBounds(childrenBounds.get(i));
			else			
				containerBounds.union(childrenBounds.get(i));
		}
		return maxOffset;
	}

	public static int getChildPivotOffset(IFigure child) {
		if(child instanceof HasPivotOffset)
			return ((HasPivotOffset)child).getPivotOffset();
		else
		{
			// If the child has exactly one sub-figure, use the first one
			if(child.getChildren().size() == 1)
				return getChildPivotOffset((IFigure) child.getChildren().get(0)) + child.getInsets().top;
			else
				return child.getPreferredSize().height/2;			
		}
	}

	@Override
	protected Dimension calculatePreferredSize(IFigure container, int wHint,
			int hHint) {
			Rectangle bounds = new Rectangle();
			doLayout(container, false, bounds);
			return new Dimension(bounds.width, bounds.height);
	}

	public int getPivotOffset(IFigure container) {
		return doLayout(container, false, new Rectangle());
	}

}

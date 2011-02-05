package net.nansore.cedalion.figures;

import net.nansore.cedalion.eclipse.TermContext;
import net.nansore.cedalion.eclipse.TermVisualizationException;
import net.nansore.prolog.Compound;

import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.Panel;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.swt.graphics.Color;

public class Box extends Panel implements TermFigure {

	private static final int MARGIN = 1;
	private int width;
	private int height;
	private Color color;

	public Box(Compound term, TermContext context) {
		color = context.getColor();
		width = (Integer)term.arg(1);
		height = width;
		setBackgroundColor(color);
	}
	
	public void updateFigure() throws TermVisualizationException {
	}

	public void dispose() {
		erase();
	}

	@Override
	public void validate() {
		super.validate();
		int newWidth = width;
		int newHeight = height;
		if(isHorizontal()) {
			newWidth = getContainer().getClientArea().width - MARGIN;
		} else {
			newHeight = getContainer().getClientArea().height - MARGIN;
		}
		if(newWidth != width || newHeight != height) {
			width = newWidth;
			height = newHeight;
			revalidate();
		}
	}

	private IFigure getContainer() {
		IFigure f = getParent();
		while(f != null && !(f instanceof FlowFigure))
			f = f.getParent();
		return f;
	}

	@Override
	public Dimension getPreferredSize(int wHint, int hHint) {
		return new Dimension(width, height);
	}

	private boolean isHorizontal() {
		IFigure f = getContainer();
		return f != null && f instanceof VerticalFlow;
	}

}

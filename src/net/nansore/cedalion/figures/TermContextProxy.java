/*
 * Created on Jan 26, 2006
 */
package net.nansore.cedalion.figures;

import java.io.IOException;

import net.nansore.cedalion.eclipse.TermContext;
import net.nansore.cedalion.eclipse.TermVisualizationException;
import net.nansore.prolog.Compound;

import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.FlowLayout;
import org.eclipse.draw2d.MouseEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbenchPart;

/**
 * This is a default implementation of the TermContext and TermFigure interfaces, one that is meant to be a convenient base class for term figures.
 * TermContext methods are implemented by proxying them to the object's own context, and TermFigure's methods have empty implementations.
 */
public abstract class TermContextProxy extends Figure implements TermContext, TermFigure {

    public TermContextProxy(TermContext parent) {
        context = parent;
        setLayoutManager(new FlowLayout());
        context.bindFigure(this);
    }

    @Override
    public Control getCanvas() {
        return context.getCanvas();
    }

	@Override
    public void handleClick(MouseEvent me) {
        context.handleClick(me);
    }
    public String getResource() {
    	return context.getResource();
	}

	@Override
    public void figureUpdated() {
        context.figureUpdated();
    }

	@Override
    public void updateFigure() throws TermVisualizationException {
    }
    private TermContext context;

	@Override
    public Text getTextEditor() {
        return context.getTextEditor();
    }

	@Override
    public void bindFigure(TermFigure figure) {
        context.bindFigure(figure);
    }

	@Override
    public void selectionChanged(TermFigure figure) {
        context.selectionChanged(figure);
    }

	@Override
    public void registerTermFigure(Object termID, TermFigure figure) {
        context.registerTermFigure(termID, figure);
    }

	@Override
    public void setFocus() {
        context.setFocus();
    }

	@Override
    public Color getColor() {
        return context.getColor();
    }
    
	@Override
	public Compound getPath() {
		return context.getPath();
	}

	@Override
    public Font getFont(int fontType) {
        return context.getFont(fontType);
    }
	
	@Override
	public void unregisterTermFigure(Object id, TermFigure figure) {
	    context.unregisterTermFigure(id, figure);
	}

	@Override
	public IWorkbenchPart getWorkbenchPart() {
		return context.getWorkbenchPart();
	}

	@Override
	public void performDefaultAction() {
        context.performDefaultAction();
    }

	@Override
	public String getPackage() {
		return context.getPackage();
	}

	@Override
	public Image getImage(String imageName) throws IOException {
		return context.getImage(imageName);
	}
	
	@Override
	public VisualTerm getFocused() {
		return context.getFocused();
	}
	
	@Override
	public void setFocused(VisualTerm visualTerm) {
		context.setFocused(visualTerm);
	}
}

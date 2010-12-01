package net.nansore.cedalion.eclipse;


import java.io.IOException;

import net.nansore.cedalion.execution.TermInstantiationException;
import net.nansore.cedalion.execution.TermInstantiator;
import net.nansore.cedalion.figures.TermFigure;
import net.nansore.cedalion.figures.VisualTerm;
import net.nansore.prolog.Compound;
import net.nansore.prolog.PrologException;

import org.eclipse.draw2d.FigureCanvas;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.MouseEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;


public class CedalionView extends ViewPart implements TermContext {

	/**
	 * The ID of the view as specified by the extension.
	 */
	public static final String ID = "net.nansore.cedalion.eclipse.CedalionView";
	private FigureCanvas canvas;
	private TermContext context;

	/*
	 * The content provider class is responsible for
	 * providing objects to the view. It can wrap
	 * existing objects in adapters or simply return
	 * objects as-is. These objects may be sensitive
	 * to the current input of the view, or ignore
	 * it and always show the same content 
	 * (like Task List, for example).
	 */
	 
	/**
	 * The constructor.
	 */
	public CedalionView() {
	}

	/**
	 * This is a callback that will allow us
	 * to create the viewer and initialize it.
	 */
	public void createPartControl(Composite parent) {
		canvas = new FigureCanvas(parent);
		// Create the help context id for the viewer's control
		PlatformUI.getWorkbench().getHelpSystem().setHelp(canvas, "net.nansore.cedalion.viewer");
	}

	/**
	 * Passing the focus request to the viewer's control.
	 */
	public void setFocus() {
		canvas.setFocus();
	}

	public void setVisualization(Compound visualization) throws TermInstantiationException, PrologException {
		context = Activator.getDefault().currentContext();
		IFigure figure = (IFigure)TermInstantiator.instance().instantiate(visualization, this);
		canvas.setContents(figure);
		canvas.setBackground(new Color(canvas.getDisplay(), new RGB(255, 255, 255)));
	}

	@Override
	public void bindFigure(TermFigure figure) {
		context.bindFigure(figure);
	}

	@Override
	public void figureUpdated() {
		context.figureUpdated();
	}

	@Override
	public Control getCanvas() {
		return canvas;
	}

	@Override
	public Color getColor() {
		return context.getColor();
	}

	@Override
	public VisualTerm getFocused() {
		return context.getFocused();
	}

	@Override
	public Font getFont(int fontType) {
		return context.getFont(fontType);
	}

	@Override
	public Image getImage(String imageName) throws IOException {
		return context.getImage(imageName);
	}

	@Override
	public String getPackage() {
		return context.getPackage();
	}

	@Override
	public Compound getPath() {
		return context.getPath();
	}

	@Override
	public String getResource() {
		return context.getResource();
	}

	@Override
	public Text getTextEditor() {
		return context.getTextEditor();
	}

	@Override
	public IWorkbenchPart getWorkbenchPart() {
		return context.getWorkbenchPart();
	}

	@Override
	public void handleClick(MouseEvent me) {
		context.handleClick(me);
	}

	@Override
	public void performDefaultAction() {
		context.performDefaultAction();
	}

	@Override
	public void registerDispose(TermFigure disp) {
		context.registerDispose(disp);
	}

	@Override
	public void registerTermFigure(Object termID, TermFigure figure) {
		context.registerTermFigure(termID, figure);
	}

	@Override
	public void selectionChanged(TermFigure figure) {
		context.selectionChanged(figure);	
	}

	@Override
	public void setFocused(VisualTerm visualTerm) {
		context.setFocused(visualTerm);
	}

	@Override
	public void unregisterTermFigure(Object termID, TermFigure figure) {
		context.unregisterTermFigure(termID, figure);
	}
}

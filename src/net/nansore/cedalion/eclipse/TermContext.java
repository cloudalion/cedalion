/*
 * Created on Jan 19, 2006
 */
package net.nansore.cedalion.eclipse;

import java.io.IOException;

import net.nansore.cedalion.figures.TermFigure;

import org.eclipse.draw2d.MouseEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbenchPart;

/**
 * @author boaz
 */
public interface TermContext {

    /**
     * @return the text widget used for editing figure text
     */
    Text getTextEditor();

    /**
     * @param label
     */
    void bindFigure(TermFigure figure);

    /**
     * @param term
     */
    void selectionChanged(TermFigure figure);

    /**
     * Notify the context that the given figure displays the given termID
     * @param termID the term ID
     * @param figure the figure that displays it
     */
    void registerTermFigure(Object termID, TermFigure figure);
    void unregisterTermFigure(Object termID, TermFigure figure);

	void setFocus();

    /**
     * @return
     */
    Color getColor();

    final static int NORMAL_FONT = 1;
    final static int SYMBOL_FONT = 2;
    /**
     * @param fontType TODO
     * @return
     */
    Font getFont(int fontType);

    /**
     * @param color
     */
    void registerDispose(TermFigure disp);

    /**
     * 
     */
    void figureUpdated();

	String getResource();

    void handleClick(MouseEvent me);

    Control getCanvas();

	IWorkbenchPart getWorkbenchPart();

    void performDefaultAction();

	String getPackage();

	Image getImage(String imageName) throws IOException;
}

/*
 * Created on Jan 19, 2006
 */
package net.nansore.cedalion.eclipse;

import java.io.IOException;

import net.nansore.cedalion.figures.TermFigure;
import net.nansore.cedalion.figures.VisualTerm;
import net.nansore.prolog.Compound;

import org.eclipse.draw2d.MouseEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbenchPart;

/**
 * This interface represents the "place" where a figure representing a Cedalion code element is placed.
 * It provides information and services from the "environment".
 */
public interface TermContext {

    /**
     * @return the text widget used for editing figure text
     */
    Text getTextEditor();

    /**
     * Allows children to bind their figures to receive mouse events
     * @param figure the figure to be bound
     */
    void bindFigure(TermFigure figure);

    /**
     * Tells the "environment" that the selection has now changed
     * @param figure the newly selected figure
     */
    void selectionChanged(TermFigure figure);

    /**
     * Notify the context that the given figure displays the given termID
     * @param termID the term ID
     * @param figure the figure that displays it
     */
    void registerTermFigure(Object termID, TermFigure figure);
    /**
     * Remove record of the given termID and figure
     * @param termID the term ID
     * @param figure the figure bound to that term ID
     */
    void unregisterTermFigure(Object termID, TermFigure figure);

	/**
	 * Request focus for the widget displaying the child figure
	 */
    void setFocus();

    /**
     * @return the current color
     */
    Color getColor();

    final static int NORMAL_FONT = 1;
    final static int SYMBOL_FONT = 2;
    /**
     * @param fontType what kind of font do we wish to create? Either NORMAL_FONT or SYMBOL_FONT 
     * @return the created font
     */
    Font getFont(int fontType);

    /**
     * Notify that the figure has been updated
     */
    void figureUpdated();

	/**
	 * @return the resource name, used internally by Cedalion to represent the file
	 */
    String getResource();

    /**
     * Notify that a mouse click has occured
     * @param me the draw2d mouse event
     */
    void handleClick(MouseEvent me);

    /**
     * @return the canvas used to draw the child figures
     */
    Control getCanvas();

	/**
	 * @return the Eclipse workbench where the child figures reside.
	 */
    IWorkbenchPart getWorkbenchPart();

    /**
     * Notifies the container to perform the action typically associated with a double-click
     */
    void performDefaultAction();

	/**
	 * @return the namespace associated with the current file
	 */
    String getPackage();

	/**
	 * Creates an image associated with the given name
	 * @param imageName the name of the image
	 * @return a new or reused Image
	 * @throws IOException if something goes wrong, e.g., bad image name
	 */
    Image getImage(String imageName) throws IOException;
	
	/**
	 * @return the path associated with the current location
	 */
    Compound getPath();

	/**
	 * Notify that visualTerm has received focus
	 * @param visualTerm the object that has received focus
	 */
    void setFocused(VisualTerm visualTerm);

	/**
	 * @return the object that has last received focus
	 */
    VisualTerm getFocused();
}

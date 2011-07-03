package net.nansore.cedalion.figures;

import net.nansore.cedalion.eclipse.TermContext;
import net.nansore.cedalion.eclipse.TermVisualizationException;
import net.nansore.prolog.Compound;

import org.eclipse.draw2d.Label;

/**
 * Displays a symbol using the symbol font, given its Unicode.
 * Takes one argument: the Unicode of the character to be displayed.
 */
public class Symbol extends Label implements TermFigure {

	private char character;

	public Symbol(Compound term, TermContext context) {
		try {
            character = (char)((Integer)term.arg(1)).intValue();
            setFont(context.getFont(TermContext.SYMBOL_FONT));
            setForegroundColor(context.getColor());
            StringBuffer buff = new StringBuffer();
            buff.append(character);
            setText(buff.toString());
            context.bindFigure(this);
        } catch (ClassCastException e) {
            setText("<symbol>");
        }
	}
	
	public void updateFigure() throws TermVisualizationException {
	}

	public void dispose() {
		erase();
	}

}

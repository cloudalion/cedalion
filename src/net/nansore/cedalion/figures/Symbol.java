package net.nansore.cedalion.figures;

import net.nansore.cedalion.eclipse.TermContext;
import net.nansore.cedalion.eclipse.TermVisualizationException;
import net.nansore.prolog.Compound;

import org.eclipse.draw2d.Label;

public class Symbol extends Label implements TermFigure {

	private char character;

	public Symbol(Compound term, TermContext context) {
		try {
            character = (char)((Integer)term.arg(1)).intValue();
            setFont(context.getFont(TermContext.SYMBOL_FONT));
            StringBuffer buff = new StringBuffer();
            buff.append(character);
            setText(buff.toString());
        } catch (ClassCastException e) {
            setText("<symbol>");
        }
	}
	
	public void updateFigure() throws TermVisualizationException {
	}

	public void dispose() {
	}

}

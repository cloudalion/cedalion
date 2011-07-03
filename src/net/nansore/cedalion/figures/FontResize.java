/**
 * 
 */
package net.nansore.cedalion.figures;

import net.nansore.cedalion.eclipse.TermContext;
import net.nansore.cedalion.eclipse.TermVisualizationException;
import net.nansore.cedalion.execution.TermInstantiationException;
import net.nansore.prolog.Compound;
import net.nansore.prolog.PrologException;

import org.eclipse.swt.graphics.FontData;

/**
 * A font modifier that resizes the font by a given offset.  The offset is given as the second argument.
 */
public class FontResize extends FontModifier {
	
	public FontResize(Compound term, TermContext context) throws TermVisualizationException, TermInstantiationException, PrologException {
		super(term, context);
	}

	protected FontData modifyFont(FontData[] fontData, int i, Compound term) {
		try {
			int fontSizeOffset = (Integer)term.arg(2);
			return new FontData(fontData[i].getName(), (int)fontData[i].height + fontSizeOffset, fontData[i].getStyle());
		} catch (ClassCastException e) {
			e.printStackTrace();
			return null;
		}
	}
}
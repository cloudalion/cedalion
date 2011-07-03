/*
 * Created on Jan 26, 2006
 */
package net.nansore.cedalion.figures;

import net.nansore.cedalion.eclipse.TermContext;
import net.nansore.cedalion.eclipse.TermVisualizationException;
import net.nansore.cedalion.execution.TermInstantiationException;
import net.nansore.prolog.Compound;
import net.nansore.prolog.PrologException;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.FontData;

/**
 * A font modifier that makes the font of its contents italic.  Takes no extra arguments.
 */
public class Italic extends FontModifier {
    public Italic(Compound term, TermContext context) throws TermVisualizationException, TermInstantiationException, PrologException {
        super(term, context);
    }
    
    /**
     * @param fontData
     * @param i
     * @return
     */
    protected FontData modifyFont(FontData[] fontData, int i, Compound term) {
        return new FontData(fontData[i].getName(), (int)fontData[i].height, fontData[i].getStyle() | SWT.ITALIC);
    }

}

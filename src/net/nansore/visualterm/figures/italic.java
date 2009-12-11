/*
 * Created on Jan 26, 2006
 */
package net.nansore.visualterm.figures;

import net.nansore.cedalion.eclipse.TermContext;
import net.nansore.cedalion.eclipse.TermVisualizationException;
import net.nansore.cedalion.execution.TermInstantiationException;
import net.nansore.prolog.Compound;
import net.nansore.prolog.PrologException;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.FontData;

/**
 * @author boaz
 */
public class italic extends FontModifier {
    public italic(Compound term, TermContext context) throws TermVisualizationException, TermInstantiationException, PrologException {
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
